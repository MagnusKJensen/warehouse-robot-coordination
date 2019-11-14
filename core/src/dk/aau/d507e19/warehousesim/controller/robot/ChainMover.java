package dk.aau.d507e19.warehousesim.controller.robot;

import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.rrt.Node;
import dk.aau.d507e19.warehousesim.controller.server.Server;

import java.util.ArrayList;

public class ChainMover {

    private final GridCoordinate start, destination;
    private final Server server;
    private final Robot startingRobot;

    private final ArrayList<RobotController> visitedRobots = new ArrayList<>();

    public ChainMover(Robot startingRobot, GridCoordinate destination, Server server) {
        if(!startingRobot.getGridCoordinate().isNeighbourOf(destination))
            throw new IllegalArgumentException("Robot must be an immediate neighbour of the destination" +
                    " when using the chain mover");

        if(!server.getReservationManager().isReservedIndefinitely(destination))
            throw new IllegalArgumentException("Cannot perform chain move when destination is not reserved indefinitely");

        this.start = startingRobot.getGridCoordinate();
        this.destination = destination;
        this.server = server;
        this.startingRobot = startingRobot;
    }

    public boolean attemptChainMove(){
        visitedRobots.clear();
        visitedRobots.add(startingRobot.getRobotController());
        final Node<RobotController> robotTree = createInitialTree();



        return false;
    }

    private Node<RobotController> createInitialTree() {
        Robot firstBlockingRobot = server.getReservationManager().getIndefiniteReservationsAt(destination).getRobot();
        visitedRobots.add(firstBlockingRobot.getRobotController());
        return new Node<RobotController>(firstBlockingRobot.getRobotController(), null, false);
    }

}
