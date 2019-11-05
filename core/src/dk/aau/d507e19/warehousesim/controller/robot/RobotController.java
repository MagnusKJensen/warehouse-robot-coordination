package dk.aau.d507e19.warehousesim.controller.robot;

import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.PathFinderEnum;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.PathFinder;
import dk.aau.d507e19.warehousesim.controller.robot.plan.task.Navigation;
import dk.aau.d507e19.warehousesim.controller.robot.plan.task.Task;
import dk.aau.d507e19.warehousesim.controller.server.Server;
import dk.aau.d507e19.warehousesim.controller.server.TimeFrame;
import dk.aau.d507e19.warehousesim.exception.DoubleReservationException;

import java.util.LinkedList;

public class RobotController {
    private Server server;
    private PathFinder pathFinder;
    private Robot robot;
    private long idleTimeTicks = 0;

    private LinkedList<Task> tasks = new LinkedList<>();

    public RobotController(Server server, Robot robot, PathFinderEnum pathFinderEnum){
        this.server = server;
        this.robot = robot;
        this.pathFinder = pathFinderEnum.getPathFinder(server, this);
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
        robot.setCurrentStatus(Status.BUSY);
        return true;
    }

    public boolean assignImmediateTask(Task task){
        tasks.add(0, task);
        robot.setCurrentStatus(Status.BUSY);
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
       else robot.setCurrentStatus(Status.BUSY);
    }

    public LinkedList<Task> getTasks() {
        return tasks;
    }


    public boolean requestMove(){
        if(robot.getCurrentStatus() == Status.BUSY){
            if(!interruptCurrentTask())
                return false;
        }

        GridCoordinate newPosition = server.getNewPosition();
        assignImmediateTask(new Navigation(this, newPosition));
        return true;
    }

    private boolean interruptCurrentTask() {
        if(tasks.isEmpty()) return true;
        return tasks.getFirst().interrupt();
    }

    public long getIdleTimeTicks() {
        return idleTimeTicks;
    }
}
