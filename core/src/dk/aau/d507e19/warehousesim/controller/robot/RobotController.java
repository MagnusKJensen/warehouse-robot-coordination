package dk.aau.d507e19.warehousesim.controller.robot;

import dk.aau.d507e19.warehousesim.controller.path.Path;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.DummyPathFinder;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.PathFinder;
import dk.aau.d507e19.warehousesim.controller.robot.plan.Action;
import dk.aau.d507e19.warehousesim.controller.robot.plan.OrderPlanner;
import dk.aau.d507e19.warehousesim.controller.server.Server;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class RobotController {

    private Server server;
    private PathFinder pathFinder;
    private TaskManager taskManager;
    private Robot robot;

    private LinkedList<Action> robotActions = new LinkedList<>();
    private LinkedList<Runnable> planningSteps = new LinkedList<>();

    public RobotController(Server server, Robot robot, String pathFinderString){
        this.server = server;
        this.robot = robot;
        this.pathFinder = generatePathFinder(pathFinderString);
    }

    public RobotController(Server server, PathFinder pathFinder, TaskManager taskManager, Robot robot){
        this.server = server;
        this.pathFinder = pathFinder;
        this.taskManager = taskManager;
    }

    private PathFinder generatePathFinder(String pathFinderString) {
        switch (pathFinderString) {
            case "Astar":
                return new Astar(server, robot);
            case "RRT*":
                return new RRTPlanner(RRTType.RRT_STAR, robot);
            case "RRT":
                return new RRTPlanner(RRTType.RRT, robot);
            case "DummyPathFinder":
                return new DummyPathFinder();
            default:
                throw new RuntimeException("Could not identify pathfinder " + pathFinderString);
        }
    }

    public Path getPath(GridCoordinate gridCoordinate, GridCoordinate destination) {
        return pathFinder.calculatePath(gridCoordinate, destination);
    }

    public void addToPlan(final Order order) {
        final OrderPlanner orderPlanner = new OrderPlanner(this);
        planningSteps.add(() -> robotActions.addAll(orderPlanner.planPickUp(order)));
        planningSteps.add(() -> robotActions.addAll(orderPlanner.planDelivery()));
        planningSteps.add(() -> robotActions.addAll(orderPlanner.planBinReturn()));
    }

    public void update(){
        if(robotActions.isEmpty())
            planNextActions();

        // If robot has nothing to do, set status available and return.
        if(robotActions.isEmpty()){
            robot.setCurrentStatus(Status.AVAILABLE);
            return;
        }

        Action currentAction = robotActions.peekFirst();
        if(!currentAction.isDone())currentAction.perform();
        robot.setCurrentStatus(currentAction.getStatus());

        if(currentAction.isDone())
            robotActions.removeFirst();
    }

    private void planNextActions() {
        if(planningSteps.isEmpty())
            return;

        Runnable planning = planningSteps.pollFirst();
        planning.run();
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
}
