package dk.aau.d507e19.warehousesim.controller.robot;

import dk.aau.d507e19.warehousesim.controller.path.Path;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.OneStepWaitingPathFinder;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.PathFinder;
import dk.aau.d507e19.warehousesim.controller.robot.plan.LineTraversal;
import dk.aau.d507e19.warehousesim.controller.robot.plan.task.Task;
import dk.aau.d507e19.warehousesim.controller.server.Reservation;
import dk.aau.d507e19.warehousesim.controller.server.Server;
import dk.aau.d507e19.warehousesim.exception.NoPathFoundException;

import java.util.ArrayList;

public class OneTileRelocationTask implements Task {

    private RobotController robotController;
    private GridCoordinate destination;
    private Server server;

    private Path path;
    private LineTraversal lineTraversal;

    private final long startTime;
    private boolean completed = false;

    public OneTileRelocationTask(RobotController robotController, GridCoordinate destination) {
        this.robotController = robotController;
        this.destination = destination;
        this.server = robotController.getServer();

        // Start traversal next tick
        startTime = server.getTimeInTicks() + 1;

        reservePath();
        lineTraversal = new LineTraversal(robotController.getRobot(), path.getLines().get(0));
    }

    // Reserve path starting next tick
    public void reservePath(){
        OneStepWaitingPathFinder oneStepPathfinder = new OneStepWaitingPathFinder(robotController.getRobot(), robotController.getServer());
        try {
            path = oneStepPathfinder.calculatePath(robotController.getRobot().getGridCoordinate(), destination, startTime);
            ArrayList<Reservation> reservations;
            reservations = MovementPredictor.calculateReservations(robotController.getRobot(), path, startTime, 0);
            server.getReservationManager().removeReservationsBy(robotController.getRobot());
            server.getReservationManager().reserve(reservations);

            // Sanity check
            if(path.getLines().size() != 1)
                throw new RuntimeException("Found path in OneTileRelocation is longer than one line : " + path);
        } catch (NoPathFoundException e) {
            throw new RuntimeException("Could not reserve path to the given destination");
        }
    }


    @Override
    public void perform() {
        if(server.getTimeInTicks() < startTime)
            return;

        lineTraversal.perform();
        if(lineTraversal.isCompleted())
            complete();
    }

    private void complete() {
        completed = true;
    }

    @Override
    public boolean isCompleted() {
        return isCompleted();
    }

    @Override
    public boolean hasFailed() {
        return false;
    }

    @Override
    public void setRobot(Robot robot) {

    }

    @Override
    public boolean interrupt() {
        return false;
    }
}
