package dk.aau.d507e19.warehousesim.controller.robot;

import dk.aau.d507e19.warehousesim.Simulation;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.PathFinderEnum;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.PathFinder;
import dk.aau.d507e19.warehousesim.controller.robot.controlsystems.ControlSystemManager;
import dk.aau.d507e19.warehousesim.controller.robot.plan.task.*;
import dk.aau.d507e19.warehousesim.controller.server.Reservation;
import dk.aau.d507e19.warehousesim.controller.server.Server;
import dk.aau.d507e19.warehousesim.controller.server.TimeFrame;
import dk.aau.d507e19.warehousesim.exception.DoubleReservationException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class RobotController {

    public long ticksSinceOrderAssigned = 0L;
    private Server server;
    private PathFinder pathFinder;
    private Robot robot;
    private ControlSystemManager controlSystemManager;
    private long idleTimeTicks = 0;
    private boolean animationFlag = false;

    private LinkedList<Task> tasks = new LinkedList<>();
    private Random random;

    public RobotController(Server server, Robot robot, PathFinderEnum pathFinderEnum){
        this.server = server;
        this.robot = robot;
        this.pathFinder = pathFinderEnum.getPathFinder(server, this);
        this.random = new Random(Simulation.RANDOM_SEED+ robot.getRobotID());
        this.controlSystemManager = new ControlSystemManager(this);
        reserveCurrentSpot();
    }

    public boolean getAnimationFlag() {
        return animationFlag;
    }

    public Random getRandom() {
        return random;
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
        task.setRobot(this.getRobot());

        if(task instanceof BinDelivery)
            ((BinDelivery) task).addOnCompleteAction(this::resetTimeSinceAssignment);

        updateStatus();
        return true;
    }

    private void resetTimeSinceAssignment() {
        this.ticksSinceOrderAssigned = 0;
    }

    public boolean assignImmediateTask(Task task){
        tasks.add(0, task);
        updateStatus();
        return true;
    }
    private Robot getRobotByID(int ID){
        for (Robot r : server.getAllRobots()){
            if(r.getRobotID()==ID){
                return r;
            }
        }
        return null;
    }
    private boolean containsSubTasWithWrongController(Robot r){
        for(Task t : r.getRobotController().getTasks()){
            if(t instanceof BinDelivery){
                if(!((BinDelivery) t).robotController.equals(r.getRobotController())){
                    return true;
                }
            }
        }return false;
    }
    public void update() {
        if(getServer().getTimeInTicks()%30 == 0) animationFlag = !animationFlag;
        if(tasks.isEmpty()){
            idleTimeTicks++;
            return;
        }
        controlSystemManager.checkSystem();

        if(hasOrderAssigned()){
            ticksSinceOrderAssigned++;
        }else {
            ticksSinceOrderAssigned = 0;
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
       else if(tasks.get(0) instanceof Charging) robot.setCurrentStatus(Status.CHARGING);
       else if(tasks.get(0) instanceof Maintenance) robot.setCurrentStatus(Status.MAINTENANCE);
       else if(tasks.get(0) instanceof OutOfBattery) robot.setCurrentStatus(Status.NOPOWER);
       else if(tasks.get(0) instanceof EmergencyStop) robot.setCurrentStatus(Status.EMERGENCY);
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

    public ControlSystemManager getControlSystemManager() {
        return controlSystemManager;
    }
    public boolean isCharging(){
        if(robot.getCurrentStatus().equals(Status.CHARGING)){
            if(tasks.get(0) instanceof Charging){
                if(((Charging) tasks.get(0)).getChargingTile() == null){
                    return false;
                }
                if(((Charging) tasks.get(0)).getChargingTile().getGridCoordinate().equals(robot.getApproximateGridCoordinate())){
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isUnderMaintenance() {
        if(robot.getCurrentStatus().equals(Status.MAINTENANCE)){
            if(tasks.get(0) instanceof Maintenance){
                if(((Maintenance) tasks.get(0)).getMaintenanceTile() == null ){
                    return false;
                }
                if(((Maintenance) tasks.get(0)).getMaintenanceTile().getGridCoordinate().equals(robot.getApproximateGridCoordinate())){
                    return true;
                }
            }
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
            if(server.getHighestPriority(authorityRobot, this.robot) == this.robot){
                return false;
            }else{
                return tasks.getFirst().canInterrupt();
            }
        }

        return true;
    }

    public boolean isMoving() {
        float maxDelta = 0.00001f;
        return !(robot.getCurrentSpeed() < maxDelta && robot.getCurrentSpeed() > -maxDelta);
    }

    public boolean emergencyStop() {
        if(robot.getCurrentStatus() == Status.EMERGENCY) // || robot.getCurrentStatus() == Status.MAINTENANCE
            return false;

        forceInterruptCurrentTask();
        assignImmediateTask(new EmergencyStop(this));
        return true;
    }

    private void forceInterruptCurrentTask(){
        if(this.getTasks().size() > 0){
            Task firstTask = this.getTasks().getFirst();
            if(firstTask instanceof Navigation){
                ((Navigation)firstTask).forceInterrupt();
            }else if(firstTask instanceof BinDelivery){
                ((BinDelivery) firstTask).forceInterrupt();
            }else if(firstTask instanceof Relocation){
                tasks.removeFirst();
            }
        }
    }

    public void startMaintenance(){
        if(robot.getCurrentStatus() == Status.MAINTENANCE)
            return; // Already in maintenance
        forceInterruptCurrentTask();
        assignImmediateTask(new Maintenance(this));
    }

    public long getTicksSinceOrderAssigned() {
        return ticksSinceOrderAssigned;
    }

    public void removeTask(BinDelivery binDelivery) {
        tasks.remove(binDelivery);
    }
}
