package dk.aau.d507e19.warehousesim.controller.robot;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import dk.aau.d507e19.warehousesim.*;
import dk.aau.d507e19.warehousesim.controller.path.Path;
import dk.aau.d507e19.warehousesim.controller.robot.plan.Action;
import dk.aau.d507e19.warehousesim.storagegrid.BinTile;
import dk.aau.d507e19.warehousesim.storagegrid.PickerTile;
import dk.aau.d507e19.warehousesim.storagegrid.Tile;
import dk.aau.d507e19.warehousesim.storagegrid.product.Bin;

import java.util.ArrayList;

public class Robot {
    private Simulation simulation;
    private Position currentPosition;
    private Order currentOrder;
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

    private ArrayList<Action> plan = new ArrayList<>();

    public Robot(Position startingPosition, int robotID, Simulation simulation) {
        this.currentPosition = startingPosition;
        this.simulation = simulation;
        this.robotID = robotID;
        currentStatus = Status.AVAILABLE;

        // Initialize controller for this robot
        this.robotController = new RobotController(simulation.getServer(), this);
    }

    public void update() {
        robotController.update();
        /*if (currentStatus == Status.TASK_ASSIGNED_PICK_UP) {
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
        }*/
    }

    public void deliverBin() {
        currentStatus = Status.AVAILABLE;
    }

    public void pickUpBin() {
        GridCoordinate coordinate = getGridCoordinate();
        Tile tile = simulation.getStorageGrid().getTile(coordinate.getX(), coordinate.getY());
        if (tile instanceof BinTile && ((BinTile) tile).hasBin()) {
            bin = ((BinTile) tile).releaseBin();
        } else throw new RuntimeException("Robot could not pick up bin at ("
                + coordinate.getX() + "," + coordinate.getY() + ")");

        currentStatus = Status.CARRYING;
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

    public void assignOrder(Order order) {
        robotController.addToPlan(order);
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

    public boolean hasPlannedPath() {
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

    public boolean collidesWith(Position collider) {
        boolean withInXBounds = collider.getX() >= currentPosition.getX()
                && collider.getX() <= currentPosition.getX() + ROBOT_SIZE;
        boolean withInYBounds = collider.getY() >= currentPosition.getY()
                && collider.getY() <= currentPosition.getY() + ROBOT_SIZE;
        return withInXBounds && withInYBounds;
    }


    public int getRobotID() {
        return robotID;
    }

    public GridCoordinate getGridCoordinate() {
        GridCoordinate gridCoordinate =
                new GridCoordinate(Math.round(currentPosition.getX()), Math.round(currentPosition.getY()));

        if (!currentPosition.isSameAs(gridCoordinate))
            throw new IllegalStateException("Robot is not at the center of a tile. Current position : "
                    + currentPosition
                    + "\n If you want an approximate grid position use getApproximateGridCoordinate()");

        return gridCoordinate;
    }

    public GridCoordinate getApproximateGridCoordinate() {
        return new GridCoordinate(Math.round(currentPosition.getX()), Math.round(currentPosition.getY()));
    }
}
