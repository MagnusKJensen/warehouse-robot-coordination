package dk.aau.d507e19.warehousesim.controller.robot.plan;

import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.controller.robot.RobotController;
import dk.aau.d507e19.warehousesim.controller.server.Server;

import java.util.ArrayList;

public class ChainMover {

    private final Server server;
    private final Robot askingRobot;
    private final Robot rootRobot;

    private final ArrayList<RobotController> visitedRobots = new ArrayList<>();

    public ChainMover(Robot askingRobot, Robot firstRobotToMove, Server server) {
        if (!askingRobot.getGridCoordinate().isNeighbourOf(firstRobotToMove.getGridCoordinate()))
            throw new IllegalArgumentException("Robot must be an immediate neighbour of the first robot in the chain" +
                    " when using the chain mover");
        this.askingRobot = askingRobot;
        this.rootRobot = firstRobotToMove;
        this.server = server;
    }

    public boolean attemptChainMove() {
        visitedRobots.clear();
        visitedRobots.add(rootRobot.getRobotController());
        visitedRobots.add(askingRobot.getRobotController());
        final RobotControllerNode rootNode = new RobotControllerNode(rootRobot.getRobotController());

        ArrayList<RobotControllerNode> leaves = rootNode.getNonExhaustedLeafNodes();
        do {
            for (RobotControllerNode leaf : leaves) {
                boolean hasSteppedAside = leaf.getRobotController().requestStepAside(askingRobot);
                if (hasSteppedAside) {
                    moveParents(leaf);
                    return true;
                } else {
                    addChildren(leaf);
                }
            }
            leaves = rootNode.getNonExhaustedLeafNodes();
        } while (!leaves.isEmpty());

        return false;
    }

    private void addChildren(RobotControllerNode leaf) {
        Robot childRobot = leaf.getRobotController().getRobot();
        ArrayList<GridCoordinate> availableNeighbours = childRobot.getGridCoordinate().getNeighbours(server.getGridBounds());

        int addedNeighbours = 0;
        for (GridCoordinate neighbour : availableNeighbours) {
            if (server.getReservationManager().isReservedIndefinitely(neighbour)) {
                Robot blockingRobot = server.getReservationManager().getIndefiniteReservationsAt(neighbour).getRobot();

                // If we have already visited this robot, just skip it
                if (visitedRobots.contains(blockingRobot.getRobotController()))
                    continue;

                // If this robot is not interruptable added it to the visited list and continue
                if (!blockingRobot.getRobotController().canInterrupt(askingRobot)) {
                    visitedRobots.add(blockingRobot.getRobotController());
                    continue;
                }

                // If we haven't already visited this robot, add it to the tree
                addedNeighbours++;
                leaf.addChild(new RobotControllerNode(blockingRobot.getRobotController(), leaf));
                visitedRobots.add(blockingRobot.getRobotController());

            }
        }

        // If no neighbours are available this leaf is exhausted
        if (addedNeighbours == 0) leaf.setExhausted(true);
    }

    private void moveParents(RobotControllerNode child) {
        if(!child.hasParent())
            return;

        final RobotControllerNode parent = child.getParent();
        parent.getRobotController().moveOneStepTo(child.getRobotController().getRobot().getGridCoordinate());

        moveParents(parent);
    }


}
