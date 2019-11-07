package dk.aau.d507e19.warehousesim.controller.robot;

import dk.aau.d507e19.warehousesim.Simulation;
import dk.aau.d507e19.warehousesim.SimulationApp;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.PathFinderEnum;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.PathFinder;
import dk.aau.d507e19.warehousesim.controller.robot.plan.task.BinDelivery;
import dk.aau.d507e19.warehousesim.controller.robot.plan.task.Navigation;
import dk.aau.d507e19.warehousesim.controller.robot.plan.task.ReservationNavigation;
import dk.aau.d507e19.warehousesim.controller.robot.plan.task.Task;
import dk.aau.d507e19.warehousesim.controller.server.Server;
import dk.aau.d507e19.warehousesim.controller.server.TimeFrame;
import dk.aau.d507e19.warehousesim.exception.DoubleReservationException;
import dk.aau.d507e19.warehousesim.statistics.StatisticsManager;

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
        if (!currentTask.isCompleted())
            currentTask.perform();

        removeCompletedTasks();

        updateStatus();
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
       else if(tasks.get(0) instanceof Navigation){
           if(tasks.size() > 1) robot.setCurrentStatus(Status.RELOCATING_BUSY);
           else robot.setCurrentStatus(Status.RELOCATING);
       }
       else robot.setCurrentStatus(Status.BUSY);
    }

    public LinkedList<Task> getTasks() {
        return tasks;
    }


    public boolean requestMove(){
        if(robot.getCurrentStatus() != Status.AVAILABLE){
            if(!interruptCurrentTask())
                return false;
        }

        GridCoordinate newPosition;// = server.getNewPosition();

        if(robot.getCurrentStatus() == Status.AVAILABLE){
            // Go to optimal spot to wait for task
            newPosition = server.getOptimalIdleRobotPosition();
        }else{
            do { // Find random neighbour tile to go to
                Direction randomDirection = Direction.values()[random.nextInt(Direction.values().length)];
                newPosition = new GridCoordinate(robot.getGridCoordinate().getX() + randomDirection.xDir, robot.getGridCoordinate().getY() + randomDirection.yDir);
            }while (!server.getGridBounds().isWithinBounds(newPosition));
        }



        assignImmediateTask(Navigation.getInstance(this, newPosition));
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
}
