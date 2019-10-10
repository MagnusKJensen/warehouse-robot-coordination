package dk.aau.d507e19.warehousesim.controller.pathAlgorithms.rrt;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.PathFinder;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.Path;

public class RRTPlanner implements PathFinder {

    RRTType algorithm;
    private RRT rrt = new RRT();
    private RRTStar rrtStar = new RRTStar();

    public RRTPlanner(RRTType algorithm) {
        this.algorithm = algorithm;
    }

    @Override
    public Path calculatePath(GridCoordinate start, GridCoordinate destination) {
        switch (algorithm){
            case RRT: return new Path(rrt.generateRRTPath(start,destination));
            case RRT_STAR: return null;
            default: throw new RuntimeException("No type called " + algorithm);
        }
    }
}
