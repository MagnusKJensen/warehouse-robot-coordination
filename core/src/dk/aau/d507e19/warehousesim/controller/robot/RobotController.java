package dk.aau.d507e19.warehousesim.controller.robot;

import dk.aau.d507e19.warehousesim.Simulation;
import dk.aau.d507e19.warehousesim.SimulationApp;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.PathFinderEnum;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.PathFinder;
import dk.aau.d507e19.warehousesim.controller.robot.plan.task.*;
import dk.aau.d507e19.warehousesim.controller.server.Reservation;
import dk.aau.d507e19.warehousesim.controller.server.Server;
import dk.aau.d507e19.warehousesim.controller.server.TimeFrame;
import dk.aau.d507e19.warehousesim.exception.DoubleReservationException;
import dk.aau.d507e19.warehousesim.statistics.StatisticsManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

public class RobotController {
    private Server server;
    private PathFinder pathFinder;
    private Robot robot;
    private long idleTimeTicks = 0;

    private LinkedList<Task> tasks = new LinkedList<>();
    private Random random;

    public RobotController(Server server, Robot robot, PathFinderEnum pathFinderEnum){
        this.server = server;
        this.robot = robot;
        this.pathFinder = pathFinderEnum.getPathFinder(server, this);
        this.random = new Random(Simulation.RANDOM_SEED);
        reserveCurrentSpot();
    }

    private void reserveCurrentSpot() {
        try {
            TimeFrame indefiniteTimeFrame = TimeFrame.indefiniteTimeFrameFrom(server.getTimeInTicks());
            server.getReservationManager().reserve(robot, robot.getGridCoordinate(), indefiniteTimeFrame);
        } catch (DoubleReservationException e) {
            throw new RuntimeException("Robot could not reserve the tile it starts on");
        }
    }

    public boolean assignTask(Task task){
        tasks.add(task);
        updateStatus();
        return true;
    }

    public boolean assignImmediateTask(Task task){
        tasks.add(0, task);
        updateStatus();
        return true;
    }

    public void update() {
        if(tasks.isEmpty()){
            idleTimeTicks++;
            return;
        }

        Task currentTask = tasks.peekFirst();
        currentTask.perform();

        if(currentTask.hasFailed() && currentTask instanceof BinDelivery)
            throw new RuntimeException("Bin delivery failed");

        removeCompletedTasks();
        removeFailedTasks();

        updateStatus();
    }

    private void removeFailedTasks() {
        tasks.removeIf(Task::hasFailed);
    }

    private void removeCompletedTasks() {
        tasks.removeIf(Task::isCompleted);
    }

    public PathFinder getPathFinder() {
        return this.pathFinder;
    }

    public Server getServer() {
        return this.server;
    }

    public Robot getRobot() {
        return robot;
    }

    public boolean hasTask(){
        return !tasks.isEmpty();
    }

    public void updateStatus() {
       if(tasks.isEmpty()) robot.setCurrentStatus(Status.AVAILABLE);
       else if(tasks.get(0) instanceof BinDelivery) robot.setCurrentStatus(Status.BUSY);
       else if(tasks.get(0) instanceof Relocation || tasks.get(0) instanceof OneTileRelocationTask){
           if(tasks.size() > 1) robot.setCurrentStatus(Status.RELOCATING_BUSY);
           else robot.setCurrentStatus(Status.RELOCATING);
       }
       else robot.setCurrentStatus(Status.BUSY);
    }

    public LinkedList<Task> getTasks() {
        return tasks;
    }


    public boolean requestMove(){
        if(robot.getCurrentStatus() == Status.RELOCATING)
            return false; // Already in the process of relocating

        if(robot.getCurrentStatus() != Status.AVAILABLE){
            if(!interruptCurrentTask())
                return false;
        }

        assignImmediateTask(new Relocation(server, this));
        return true;
    }

    private boolean interruptCurrentTask() {
        if(tasks.isEmpty()) return true;
        Task firstTask = tasks.getFirst();

        // Simple navigation tasks are discarded if they are interrupted
        if(firstTask instanceof ReservationNavigation){
            boolean interrupted = firstTask.interrupt();
            if(interrupted){
                tasks.removeFirst();
                return true;
            }
            return false;
        }

        return firstTask.interrupt();
    }

    public long getIdleTimeTicks() {
        return idleTimeTicks;
    }

    public boolean hasOrderAssigned() {
        for(Task task : tasks){
            if(task instanceof BinDelivery)
                return true;
        }
        return false;
    }

    // Immediately
    public void moveOneStepTo(GridCoordinate neighbour) {
        OneTileRelocationTask relocationTask = new OneTileRelocationTask(this, neighbour);
        assignImmediateTask(relocationTask);
        updateStatus();
    }

    public boolean requestStepAside(Robot authorityRobot) {
        if(!canInterrupt(authorityRobot))
            return false;

        // Find neighbours that are not unavailable
        ArrayList<GridCoordinate> availableNeighbours = this.getRobot().getGridCoordinate().getNeighbours(server.getGridBounds());

        // Find any free spaces and try to move there if possible
        for(GridCoordinate neighbour : availableNeighbours){
            Reservation reservation = new Reservation(robot, neighbour, TimeFrame.indefiniteTimeFrameFrom(server.getTimeInTicks()));
            if(server.getReservationManager().hasConflictingReservations(reservation))
                continue;
            moveOneStepTo(neighbour);
            return true;
        }

        // No free neighbour tiles
        return false;
    }

    public boolean canInterrupt(Robot authorityRobot) {
        if(robot.getCurrentStatus() == Status.RELOCATING || robot.getCurrentStatus() == Status.RELOCATING_BUSY)
            return false; // Already in the process of relocating

        if(robot.getCurrentStatus() != Status.AVAILABLE){
            // Can't be interrupted by lower priority robots (unless idle)
            int askingPriority = server.getPriority(authorityRobot);
            int selfPriority = server.getPriority(this.robot);
            if(askingPriority < selfPriority)
                return false;

            return tasks.getFirst().canInterrupt();
        }

        return true;
    }
}
