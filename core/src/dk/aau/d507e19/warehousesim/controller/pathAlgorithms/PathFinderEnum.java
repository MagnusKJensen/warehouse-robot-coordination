package dk.aau.d507e19.warehousesim.controller.pathAlgorithms;

import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.aStar.Astar;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.aStarExtended.AstarExtended;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.chp.CHPathfinder;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.rrt.RRTPlanner;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.rrt.RRTStarExtended;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.rrt.RRTType;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.controller.robot.RobotController;
import dk.aau.d507e19.warehousesim.controller.server.Server;

public enum PathFinderEnum {
    DUMMYPATHFINDER("DummyPathFinder", false), ASTAR("A*", true), ASTARCORNERS("A* Corners", true),
    CHPATHFINDER("CustomH - Turns", true), RTT("RTT", false), RRTSTAR("RTT*", false), RRTSTAREXTENDED("RRT*EXTENDED",false);

    private String name;
    private boolean works;
    PathFinderEnum(String name, boolean works) {
        this.name = name;
        this.works = works;
    }

    public boolean works() {
        return works;
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
            case RRTSTAREXTENDED:
                return new RRTPlanner(RRTType.RRT_STAR_EXTENDED,robotController);
            default:
                throw new RuntimeException("Could not identify pathfinder " + this.getName());
        }
    }
}
