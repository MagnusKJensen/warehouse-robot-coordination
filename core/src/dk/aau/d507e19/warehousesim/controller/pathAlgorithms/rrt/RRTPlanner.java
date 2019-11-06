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


    public RRTBase getPlanner(){
        switch(algorithm){
            case RRT_STAR: return rrtStar;
            case RRT: return rrt;
            default: throw new RuntimeException("Algorithm is of unknown type, no type called " + algorithm);
        }
    }
    public RRTPlanner(RRTType algorithm, RobotController robotController) {
        this.algorithm = algorithm;
        this.robotController = robotController;
        this.rrt = new RRT(this.robotController);
        this.rrtStar = new RRTStar(this.robotController);
    }

    @Override
    public Path calculatePath(GridCoordinate start, GridCoordinate destination) {
        switch (algorithm){
            case RRT: return new Path(rrt.generateRRTPath(start,destination));
            case RRT_STAR: return new Path(rrtStar.generatePath(start, destination));
            default: throw new RuntimeException("No type called " + algorithm);
        }
    }

    @Override
    public boolean accountsForReservations() {
        return false;
    }
}
