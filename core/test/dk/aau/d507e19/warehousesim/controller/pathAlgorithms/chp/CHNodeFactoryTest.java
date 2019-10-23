package dk.aau.d507e19.warehousesim.controller.pathAlgorithms.chp;

import dk.aau.d507e19.warehousesim.controller.path.Path;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.controller.robot.RobotController;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

public class CHNodeFactoryTest {

    private static final Heuristic distanceHeuristic = new Heuristic() {
        @Override
        public double getHeuristic(Path path, GridCoordinate goal, RobotController robotController) {
            GridCoordinate currentCoordinate = path.getLastStep().getGridCoordinate();
            int difX = Math.abs(currentCoordinate.getX() - goal.getX());
            int difY = Math.abs(currentCoordinate.getY() - goal.getY());
            return difX + difY;
        }
    };

    private static final GCostCalculator distanceGCostCallculator = new GCostCalculator() {
        @Override
        public double getGCost(Path path, RobotController robotController) {
            return path.getFullPath().size();
        }
    };

    private static CHNodeFactory nodeFactory;

    @BeforeClass
    public static void setUp() throws Exception {
        RobotController robotController = Mockito.mock(RobotController.class);
        nodeFactory = new CHNodeFactory(distanceHeuristic, distanceGCostCallculator, robotController);
    }

    @Ignore
    public void createInitialNode() {


    }

    @Ignore
    public void createNode() {

    }

    @Ignore
    public void createWaitingNode() {
    }

}