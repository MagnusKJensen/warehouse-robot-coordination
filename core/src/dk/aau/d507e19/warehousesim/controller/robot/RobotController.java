package dk.aau.d507e19.warehousesim.controller.robot;

import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.Astar;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.DummyPathFinder;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.PathFinder;
import dk.aau.d507e19.warehousesim.controller.server.Server;

public class RobotController {

    private Server server;
    private PathFinder pathFinder;
    private TaskManager taskManager;
    private Robot robot;

    public RobotController(Server server, Robot robot){
        this.server = server;
        this.robot = robot;
        this.pathFinder = new DummyPathFinder();
    }

    public RobotController(Server server, PathFinder pathFinder, TaskManager taskManager, Robot robot){
        this.server = server;
        this.pathFinder = pathFinder;
        this.taskManager = taskManager;
    }

    public Path getPath(GridCoordinate gridCoordinate, GridCoordinate destination) {
        return pathFinder.calculatePath(gridCoordinate, destination);
    }
}
