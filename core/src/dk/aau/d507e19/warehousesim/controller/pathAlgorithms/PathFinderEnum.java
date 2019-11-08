package dk.aau.d507e19.warehousesim.controller.pathAlgorithms;

import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.aStar.Astar;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.aStarExtended.AstarExtended;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.chp.CHPathfinder;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.rrt.RRTPlanner;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.rrt.RRTType;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.controller.robot.RobotController;
import dk.aau.d507e19.warehousesim.controller.server.Server;

public enum PathFinderEnum {
    DUMMYPATHFINDER("DummyPathFinder"), ASTAR("A*"), ASTARCORNERS("A* Corners"),
    CHPATHFINDER("CustomH - Turns"), RTT("RTT"), RRTSTAR("RTT*");

    String name;
    PathFinderEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public PathFinder getPathFinder(Server server, RobotController robotController){
        switch (this) {
            case ASTAR:
                return new Astar(server, robotController.getRobot());
            case CHPATHFINDER:
                return CHPathfinder.defaultCHPathfinder(server.getGridBounds(), robotController);
            case RTT:
                return new RRTPlanner(RRTType.RRT, robotController);
            case RRTSTAR:
                return new RRTPlanner(RRTType.RRT_STAR, robotController);
            case DUMMYPATHFINDER:
                return new DummyPathFinder();
            case ASTARCORNERS:
                return new AstarExtended(server, robotController.getRobot());
            default:
                throw new RuntimeException("Could not identify pathfinder " + this.getName());
        }
    }
}
