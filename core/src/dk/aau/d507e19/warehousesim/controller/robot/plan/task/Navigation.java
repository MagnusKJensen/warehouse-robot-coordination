package dk.aau.d507e19.warehousesim.controller.robot.plan.task;

import dk.aau.d507e19.warehousesim.TickTimer;
import dk.aau.d507e19.warehousesim.TimeUtils;
import dk.aau.d507e19.warehousesim.controller.path.Line;
import dk.aau.d507e19.warehousesim.controller.path.Path;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.PathFinder;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.controller.robot.RobotController;
import dk.aau.d507e19.warehousesim.controller.robot.plan.LineTraversal;
import dk.aau.d507e19.warehousesim.controller.server.Reservation;
import dk.aau.d507e19.warehousesim.controller.server.Server;
import dk.aau.d507e19.warehousesim.controller.server.TimeFrame;

import java.util.ArrayList;

public abstract class Navigation implements Task{

    protected int TICKS_BETWEEN_RETRIES = TimeUtils.secondsToTicks(1);

    protected GridCoordinate destination;
    private Path path;
    private ArrayList<LineTraversal> lineTraversals = new ArrayList<>();

    RobotController robotController;
    Robot robot;

    private TickTimer retryTimer = new TickTimer(TICKS_BETWEEN_RETRIES);
    private boolean isCompleted = false;

    public static Navigation getInstance(RobotController robotController, GridCoordinate destination){
        if(robotController.getPathFinder().accountsForReservations()){
            return new ReservationNavigation(robotController, destination);
        }else{
            return new StepAsideNavigator(robotController, destination);
        }
    }

    public Navigation(RobotController robotController, GridCoordinate destination) {
        this.robotController = robotController;
        this.destination = destination;
        this.robot = robotController.getRobot();
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
        if(isCompleted())
            throw new RuntimeException("Can't perform task that is already completed");

        if(path == null){
            if(retryTimer.isDone()){
                retryTimer.reset();

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

    abstract boolean canInterrupt();

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

}
