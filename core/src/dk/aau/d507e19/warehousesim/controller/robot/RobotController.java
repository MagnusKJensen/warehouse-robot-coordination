package dk.aau.d507e19.warehousesim.controller.robot;

import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.DummyPathFinder;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.PathFinderEnum;
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Function;

public class RobotController {
    private Server server;
    private PathFinder pathFinder;
    private Robot robot;

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

    private void checkForCollisions(){
        //find nearby robots
        ArrayList<Robot> nearbyRobots = scanForNearbyRobots();
        if(!nearbyRobots.isEmpty()){
            for(Robot r : nearbyRobots){
                if(goingToCollide(r.getRobotController())){
                    handleCollision();
                }
            }
        }
    }

    private ArrayList<Robot> scanForNearbyRobots(){
        ArrayList<Robot> nearbyRobots = new ArrayList<>();
        //more readable
        Function<GridCoordinate,Double> distance = gc -> Math.sqrt(Math.pow(gc.getX() - robot.getGridCoordinate().getX(), 2) + Math.pow(gc.getY() - robot.getGridCoordinate().getY(), 2));
        for(Robot r: server.getAllRobots()){
            if(distance.apply(r.getGridCoordinate()) <= 2){
                if(!r.equals(robot)){
                    nearbyRobots.add(r);
                }
            }
        }
        return nearbyRobots;
    }

    private boolean goingToCollide(RobotController them){
        //Check if paths have any of the same coordinates
        //If they do, then check if they are in the same timeframe
        //How: Get reservation lists for both, then check the timeframe for the potential collision coordinate
        //If they will collide, return true
        return false;
    }

    private void handleCollision(){

    }

}
