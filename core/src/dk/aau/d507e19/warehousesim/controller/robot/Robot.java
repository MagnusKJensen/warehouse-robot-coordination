package dk.aau.d507e19.warehousesim.controller.robot;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import dk.aau.d507e19.warehousesim.*;
import dk.aau.d507e19.warehousesim.controller.path.Path;
import dk.aau.d507e19.warehousesim.storagegrid.BinTile;
import dk.aau.d507e19.warehousesim.storagegrid.PickerTile;
import dk.aau.d507e19.warehousesim.storagegrid.Tile;
import dk.aau.d507e19.warehousesim.storagegrid.product.Bin;

public class Robot {
    private Simulation simulation;
    private Position currentPosition;
    private Task currentTask;
    private Status currentStatus;
    private float currentSpeed;
    private Path pathToTarget;
    private Bin bin = null;
    private int robotID;

    /**
     * Robot STATS
     */
    // Pickup time
    private final static int pickUpTimeInTicks = SimulationApp.TICKS_PER_SECOND * WarehouseSpecs.robotPickUpSpeedInSeconds;
    private final static int deliverTimeInTicks = SimulationApp.TICKS_PER_SECOND * WarehouseSpecs.robotDeliverToPickerInSeconds;
    private int ticksLeftForCurrentTask = 0;
    // Speed
    private final float maxSpeedBinsPerSecond = WarehouseSpecs.robotTopSpeed / WarehouseSpecs.binSizeInMeters;
    private final float accelerationBinSecond = WarehouseSpecs.robotAcceleration / WarehouseSpecs.binSizeInMeters;
    private final float decelerationBinSecond = WarehouseSpecs.robotDeceleration / WarehouseSpecs.binSizeInMeters;
    private final float minSpeedBinsPerSecond = WarehouseSpecs.robotMinimumSpeed / WarehouseSpecs.binSizeInMeters;

    private final float ROBOT_SIZE = Tile.TILE_SIZE;

    private LineTraverser currentTraverser;
    private RobotController robotController;

    public Robot(Position startingPosition, int robotID, Simulation simulation) {
        this.currentPosition = startingPosition;
        this.simulation = simulation;
        this.robotID = robotID;
        currentStatus = Status.AVAILABLE;

        // Initialize controller for this robot
        this.robotController = new RobotController(simulation.getServer(), this);
    }

    public void update() {
        if (currentStatus == Status.TASK_ASSIGNED_PICK_UP) {
            // If destination is reached start pickup
            if (pathToTarget.getStrippedPath().size() == 1) pickupProduct();
            // If movement still needed
            else moveWithLineTraverser();
        } else if (currentStatus == Status.TASK_ASSIGNED_CARRYING){
            // If delivery station already reached
            if(pathToTarget.getStrippedPath().size() == 1) deliverProduct();
            // If movement still needed
            else moveWithLineTraverser();
        } else if (currentStatus == Status.TASK_ASSIGNED_MOVE){
            // If target reached, show as available
            if(pathToTarget.getStrippedPath().size() == 1) currentStatus = Status.AVAILABLE;
            // If movement still needed
            else moveWithLineTraverser();
        }
    }

    private void deliverProduct(){
        if (ticksLeftForCurrentTask == 0) {
            currentStatus = Status.AVAILABLE;
        } else {
            // If still picking up the product
            ticksLeftForCurrentTask -= 1;
        }
    }

    private void pickupProduct(){
        if (ticksLeftForCurrentTask == 0) {
            int x = currentTask.getDestination().getX();
            int y = currentTask.getDestination().getY();

            Tile tile = simulation.getStorageGrid().getTile(x,y);
            if(tile instanceof BinTile && ((BinTile) tile).hasBin()){
                bin = ((BinTile) tile).releaseBin();
            }
            else throw new RuntimeException("Robot could not pick up bin at (" + x + "," + y + ")");
            currentStatus = Status.CARRYING;
        } else {
            // If still picking up the product
            ticksLeftForCurrentTask -= 1;
        }
    }

    private void moveWithLineTraverser(){
        currentTraverser.traverse();
        if (currentTraverser.destinationReached()){
            pathToTarget.getStrippedPath().remove(0);

            // Create new traverser for next line in the path
            if(pathToTarget.getStrippedPath().size() > 1)
                assignTraverser();
        }
    }


    public void render(SpriteBatch batch) {
        switch (currentStatus) {
            case AVAILABLE:
                batch.draw(GraphicsManager.getTexture("Simulation/Robots/robotAvailable.png"), currentPosition.getX(), currentPosition.getY(), Tile.TILE_SIZE, Tile.TILE_SIZE);
                break;
            case TASK_ASSIGNED_PICK_UP:
            case TASK_ASSIGNED_MOVE:
                batch.draw(GraphicsManager.getTexture("Simulation/Robots/robotTaskAssigned.png"), currentPosition.getX(), currentPosition.getY(), Tile.TILE_SIZE, Tile.TILE_SIZE);
                break;
            case TASK_ASSIGNED_CARRYING:
            case CARRYING:
                batch.draw(GraphicsManager.getTexture("Simulation/Robots/robotTaskAssignedCarrying.png"), currentPosition.getX(), currentPosition.getY(), Tile.TILE_SIZE, Tile.TILE_SIZE);
                break;
            default:
                throw new RuntimeException("Robot status unavailable");
        }
    }

    public void assignTask(Task task) {
        currentTask = task;
        if(task.getRoboAction() == RoboAction.PICK_UP){
            currentStatus = Status.TASK_ASSIGNED_PICK_UP;
            ticksLeftForCurrentTask = pickUpTimeInTicks;
        } else if (task.getRoboAction() == RoboAction.DELIVER){
            if(currentStatus != Status.CARRYING) throw new IllegalArgumentException("Robot is not carrying anything");
            // If target is not a PickerTile
            if(!(simulation.getStorageGrid().getTile(task.getDestination().getX(), task.getDestination().getY()) instanceof PickerTile)){
                throw new IllegalArgumentException("Target at (" + task.getDestination().getX() + "," + task.getDestination().getY() + ") is not a PickerTile");
            }
            currentStatus = Status.TASK_ASSIGNED_CARRYING;
            ticksLeftForCurrentTask = deliverTimeInTicks;
        } else if (task.getRoboAction() == RoboAction.MOVE){
            currentStatus = Status.TASK_ASSIGNED_MOVE;
            ticksLeftForCurrentTask = 0;
        }

        pathToTarget = robotController.getPath(
                new GridCoordinate((int) currentPosition.getX(), (int) currentPosition.getY()), task.getDestination());

        // If the robot has to move
        if(pathToTarget.getStrippedPath().size() > 1) assignTraverser();
    }

    private void assignTraverser() {
        currentTraverser = new LineTraverser(pathToTarget.getStrippedPath().get(0).getGridCoordinate(),
                pathToTarget.getStrippedPath().get(1).getGridCoordinate(), this);
    }

    public void cancelTask() {
        // TODO: 03/10/2019 Manage situations where the robot is in between tiles
    }

    public Position getCurrentPosition() {
        return currentPosition;
    }

    public void decelerate() {
        if (currentSpeed > 0) {
            currentSpeed -= decelerationBinSecond / (float) SimulationApp.TICKS_PER_SECOND;
            if (currentSpeed < minSpeedBinsPerSecond)
                currentSpeed = minSpeedBinsPerSecond;
        }
    }

    public void accelerate() {
        if (currentSpeed < maxSpeedBinsPerSecond) {
            currentSpeed += accelerationBinSecond / (float) SimulationApp.TICKS_PER_SECOND;
            if (currentSpeed > maxSpeedBinsPerSecond)
                currentSpeed = maxSpeedBinsPerSecond;
        }
    }

    public float getAccelerationBinSecond() {
        return accelerationBinSecond;
    }

    public float getDecelerationBinSecond() {
        return decelerationBinSecond;
    }

    public float getCurrentSpeed() {
        return currentSpeed;
    }

    public float getMaxSpeedBinsPerSecond() {
        return maxSpeedBinsPerSecond;
    }

    public void move(float deltaX, float deltaY) {
        currentPosition.setX(currentPosition.getX() + deltaX);
        currentPosition.setY(currentPosition.getY() + deltaY);
    }

    public float getMinimumSpeed() {
        return WarehouseSpecs.robotMinimumSpeed;
    }

    public Status getCurrentStatus() {
        return currentStatus;
    }

    public boolean hasPlannedPath(){
        return pathToTarget != null
                && (currentStatus == Status.TASK_ASSIGNED_PICK_UP
                || currentStatus == Status.TASK_ASSIGNED_MOVE
                || currentStatus == Status.TASK_ASSIGNED_CARRYING);
    }

    public Path getPathToTarget() {
        return pathToTarget;
    }


    public void setCurrentStatus(Status currentStatus) {
        this.currentStatus = currentStatus;
    }

    public void setBin(Bin bin) {
        this.bin = bin;
    }

    public boolean collidesWith(Position collider){
        boolean withInXBounds = collider.getX() >= currentPosition.getX()
                && collider.getX() <= currentPosition.getX() + ROBOT_SIZE;
        boolean withInYBounds = collider.getY() >= currentPosition.getY()
                && collider.getY() <= currentPosition.getY() + ROBOT_SIZE;
        return withInXBounds && withInYBounds;
    }


    public int getRobotID() {
        return robotID;
    }
}
