package dk.aau.d507e19.warehousesim.controller.robot.plan.task;

import dk.aau.d507e19.warehousesim.TickTimer;
import dk.aau.d507e19.warehousesim.TimeUtils;
import dk.aau.d507e19.warehousesim.controller.path.Line;
import dk.aau.d507e19.warehousesim.controller.path.Path;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.PartialPathFinder;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.PathFinder;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.MovementPredictor;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.controller.robot.RobotController;
import dk.aau.d507e19.warehousesim.controller.robot.plan.LineTraversal;
import dk.aau.d507e19.warehousesim.controller.server.Reservation;
import dk.aau.d507e19.warehousesim.controller.server.ReservationManager;
import dk.aau.d507e19.warehousesim.controller.server.Server;
import dk.aau.d507e19.warehousesim.controller.server.TimeFrame;

import java.util.ArrayList;

public abstract class Navigation implements Task{

    protected int TICKS_BETWEEN_RETRIES = TimeUtils.secondsToTicks(1);
    private final int maxRetries;
    private int remainingRetries;

    static final int UNLIMITED_RETRIES = -1;

    protected GridCoordinate destination;
    private Path path;
    private ArrayList<LineTraversal> lineTraversals = new ArrayList<>();

    RobotController robotController;
    Robot robot;

    private TickTimer retryTimer = new TickTimer(TICKS_BETWEEN_RETRIES);
    private boolean isCompleted = false;
    private boolean hasFailed = false;

    public static Navigation getInstance(RobotController robotController, GridCoordinate destination){
        if(robotController.getPathFinder() instanceof PartialPathFinder) {
            return new SmartNavigation(robotController, destination);
        }else if(robotController.getPathFinder().accountsForReservations()){
            return new ReservationNavigation(robotController, destination);
        }else{
            return new StepAsideNavigator(robotController, destination);
        }
    }

    public static Navigation getInstance(RobotController robotController, GridCoordinate destination, int maxRetries){
        if(robotController.getPathFinder() instanceof PartialPathFinder){
            return new SmartNavigation(robotController, destination, maxRetries);
        } else if(robotController.getPathFinder().accountsForReservations()){
            return new ReservationNavigation(robotController, destination, maxRetries);
        }else{
            return new StepAsideNavigator(robotController, destination, maxRetries);
        }
    }

    public Navigation(RobotController robotController, GridCoordinate destination) {
        this(robotController, destination, UNLIMITED_RETRIES);
    }

    public Navigation(RobotController robotController, GridCoordinate destination, int maxRetries) {
        this.robotController = robotController;
        this.destination = destination;
        this.robot = robotController.getRobot();
        this.maxRetries = maxRetries;
        remainingRetries = maxRetries + 1;
        retryTimer.setRemainingTicks(0);
    }

    void setTicksBetweenRetries(int i) {
        TICKS_BETWEEN_RETRIES = i;
    }

    protected void setNewPath(Path newPath){
        this.path = newPath;
    }

    @Override
    public final void perform() {
        if(isCompleted() || hasFailed())
            throw new RuntimeException("Can't perform task that has failed or is already completed");

        if(path == null){
            if(maxRetries != UNLIMITED_RETRIES && remainingRetries == 0)
                fail();

            if(retryTimer.isDone()){
                retryTimer.reset();
                remainingRetries--;

                boolean pathFound = planPath();
                if(pathFound)
                    traversePath();
            }else{
                retryTimer.decrement();
            }
        }else{
            traversePath();
        }
    }

    private void fail() {
        hasFailed = true;
    }

    protected final boolean destinationReached(){
        return robot.getGridCoordinate().equals(destination);
    }

    private void traversePath() {
        if(lineTraversals.isEmpty())
            createLineTraversals();

        // If still empty check if we have reached the destination
        if(lineTraversals.isEmpty()){
            if(destinationReached())
                complete();
            else
                clearPath();

            return;
        }

        LineTraversal currentLineTraversal = lineTraversals.get(0);
        currentLineTraversal.perform();
        if(currentLineTraversal.isCompleted()){
            robot.addToDistanceTraveled(currentLineTraversal.getDistance());
            lineTraversals.remove(currentLineTraversal);

            // Path has finished traversing
            if(lineTraversals.isEmpty()){
                if(destination.equals(robot.getGridCoordinate())){
                    // We have reached the destination and the navigation is complete
                    complete();
                }else{
                    // We have finished the path but are still not at the destination
                    clearPath();
                }
            }

        }
    }

    private void clearPath(){
        this.path = null;
        lineTraversals.clear();
    }

    private void createLineTraversals() {
        lineTraversals.clear();
        ArrayList<Line> lines = path.getLines();
        for(Line line : lines){
            lineTraversals.add(new LineTraversal(robot, line));
        }
    }

    // Attempt to find a full or partial path and assign it to the path variable
    // Return true if path was found
    // and return false if not
    abstract boolean planPath();

    @Override
    public final boolean hasFailed() {
        return hasFailed;
    }

    protected final boolean askOccupyingRobotToMove(GridCoordinate dest) {
        Server server = robotController.getServer();
        Reservation indefiniteRes = new Reservation(robot, dest, TimeFrame.indefiniteTimeFrameFrom(server.getTimeInTicks()));
        ArrayList<Reservation> conflicts = server.getReservationManager().getConflictingReservations(indefiniteRes);

        for(Reservation reservation : conflicts){
            if(reservation.getTimeFrame().getTimeMode() == TimeFrame.TimeMode.UNBOUNDED){
                if(reservation.getRobot().equals(robot))
                    throw new RuntimeException("Robot tried to ask itself to move");

                return reservation.getRobot().getRobotController().requestMove();
            }
        }

        throw new RuntimeException("No occupying robot; no robot has reserved grid tile :" + dest + " indefinitely");
    }

    protected void reservePath(Path path, boolean reserveLastTileIndefinitely) {
        Server server = robotController.getServer();
        ArrayList<Reservation> reservations = MovementPredictor.calculateReservations(robot, path, server.getTimeInTicks(), 0);

        if (reserveLastTileIndefinitely) { // Replace last reservation with an indefinite one
            Reservation lastReservation = reservations.get(reservations.size() - 1);
            TimeFrame indefiniteTimeFrame = TimeFrame.indefiniteTimeFrameFrom(lastReservation.getTimeFrame().getStart());
            Reservation indefiniteReservation = new Reservation
                    (lastReservation.getRobot(), lastReservation.getGridCoordinate(), indefiniteTimeFrame);
            reservations.remove(reservations.size() - 1);
            reservations.add(indefiniteReservation);
        }

        server.getReservationManager().reserve(reservations);
    }

    private void complete() {
        isCompleted = true;
    }


    public boolean interrupt(){
        if(canInterrupt()){
            clearPath();
            return true;
        }
        return false;
    }

    boolean isMoving() {
        return path != null;
    }

    @Override
    public boolean isCompleted() {
        return isCompleted;
    }

    @Override
    public final void setRobot(Robot robot) {
        this.robot = robot;
    }

    protected void updateReservations(Path newPath) {
        // Remove old reservations
        robotController.getServer().getReservationManager().removeReservationsBy(robot);

        // Add reservations from new path
        if(robotController.getPathFinder().accountsForReservations()){
            if (newPath.getFullPath().size() > 1)
                reservePath(newPath, true);
            else
                reserveCurrentTileIndefinitely();
        }
    }

    private void reserveCurrentTileIndefinitely() {
        Server server = robotController.getServer();
        ReservationManager reservationManager = server.getReservationManager();
        reservationManager.reserve(robot, robot.getGridCoordinate(), TimeFrame.indefiniteTimeFrameFrom(server.getTimeInTicks()));
    }

}
