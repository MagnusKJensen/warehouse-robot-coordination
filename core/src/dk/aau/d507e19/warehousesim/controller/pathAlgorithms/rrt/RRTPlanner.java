package dk.aau.d507e19.warehousesim.controller.pathAlgorithms.rrt;
import dk.aau.d507e19.warehousesim.controller.path.Path;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.PathFinder;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.RobotController;


import java.util.Optional;

public class RRTPlanner implements PathFinder {
    RobotController robotController;
    RRTType algorithm;
    private RRT rrt;
    private RRTStar rrtStar;

    public RRTPlanner(RRTType algorithm, RobotController rrtRobotController) {
        this.algorithm = algorithm;
        this.robotController = rrtRobotController;
        this.rrt = new RRT(this.robotController);
        this.rrtStar = new RRTStar(this.robotController);
    }

    @Override
    public Optional<Path> calculatePath(GridCoordinate start, GridCoordinate destination) {
        switch (algorithm){
            case RRT: return Optional.of(new Path(rrt.generateRRTPath(start,destination)));
            case RRT_STAR: return Optional.of(new Path(rrtStar.generatePath(start, destination)));
            default: throw new RuntimeException("No type called " + algorithm);
        }
    }
}
