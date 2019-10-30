package dk.aau.d507e19.warehousesim.controller.robot;

import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.DummyPathFinder;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.aStar.Astar;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.PathFinder;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.chp.CHPathfinder;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.rrt.RRTPlanner;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.rrt.RRTType;
import dk.aau.d507e19.warehousesim.controller.robot.plan.task.Navigation;
import dk.aau.d507e19.warehousesim.controller.robot.plan.task.Task;
import dk.aau.d507e19.warehousesim.controller.server.Server;
import dk.aau.d507e19.warehousesim.controller.server.TimeFrame;
import dk.aau.d507e19.warehousesim.exception.DoubleReservationException;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Optional;

public class RobotController {
    private Server server;
    private PathFinder pathFinder;
    private Robot robot;

    private LinkedList<Task> tasks = new LinkedList<>();

    public RobotController(Server server, Robot robot, String pathFinderString){
        this.server = server;
        this.robot = robot;
        this.pathFinder = generatePathFinder(pathFinderString);
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

    private PathFinder generatePathFinder(String pathFinderString) {
        switch (pathFinderString) {
            case "Astar":
                return new Astar(server, robot);
            case "RRT*":
                return new RRTPlanner(RRTType.RRT_STAR, this);
            case "RRT":
                return new RRTPlanner(RRTType.RRT, this);
            case "DummyPathFinder":
                return new DummyPathFinder();
            case "CustomH - Turns":
                return CHPathfinder.defaultCHPathfinder(server.getGridBounds(), this);
            default:
                throw new RuntimeException("Could not identify pathfinder " + pathFinderString);
        }
    }

    public boolean assignTask(Task task){
        tasks.add(task);
        robot.setCurrentStatus(Status.BUSY);
        return true;
    }

    public void cancelTask(Task task) {
        // todo
    }

    public void update() {
        if(tasks.isEmpty()){
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
        if(robot.getCurrentStatus() == Status.BUSY)
            return false;

        GridCoordinate newPosition = server.getNewPosition();
        assignTask(new Navigation(this, newPosition));
        return true;
    }

}
