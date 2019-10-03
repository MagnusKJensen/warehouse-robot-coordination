package dk.aau.d507e19.warehousesim.controller.robot;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import dk.aau.d507e19.warehousesim.Position;
import dk.aau.d507e19.warehousesim.SimulationApp;
import dk.aau.d507e19.warehousesim.Tile;
import dk.aau.d507e19.warehousesim.WarehouseSpecs;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.PathFinder;

public class Robot {
    private Position currentPosition;
    private Task currentTask;
    private Status currentStatus;
    private float currentSpeed;
    private Path pathToTarget;

    /**
     * Robot STATS
     */
    // Pickup time
    private final static int pickUpTimeInTicks = SimulationApp.TICKS_PER_SECOND * WarehouseSpecs.robotPickUpSpeedInSeconds;
    private int ticksLeftForCurrentTask = 0;
    // Speed
    private final static float maxSpeedBinsPerSecond = WarehouseSpecs.robotTopSpeed / WarehouseSpecs.binSizeInMeters;
    private final static float accelerationBinSecond = WarehouseSpecs.robotAcceleration / WarehouseSpecs.binSizeInMeters;
    private final static float decelerationBinSecond = WarehouseSpecs.robotDeceleration / WarehouseSpecs.binSizeInMeters;

    private LineTraverser currentTraverser;
    private PathFinder pathFinder;

    public Robot(Position currentPosition, PathFinder pathFinder) {
        this.currentPosition = currentPosition;
        this.pathFinder = pathFinder;
        currentStatus = Status.AVAILABLE;
    }

    public void update() {
        // todo: if robot gets task, where it is already on top of the product
        if (currentStatus == Status.TASK_ASSIGNED) {
            /**
             * If destination is reached
             */
            if (pathToTarget.getCornersPath().size() == 1) {
                // If done 
                if (ticksLeftForCurrentTask == 0) {
                    currentStatus = Status.CARRYING;
                } else {
                    // If still picking up the product
                    ticksLeftForCurrentTask -= 1;
                }
            } else {
                /**
                 * If movement still needed
                 */
                currentTraverser.traverse();
                if (currentTraverser.destinationReached()){
                    pathToTarget.getCornersPath().remove(0);

                    // Create new traverser for next line in the path
                    if(pathToTarget.getCornersPath().size() > 1)
                        assignTraverser();
                }


            }

            // If not moving a full speed, accelerate!

        }
    }


    public void render(SpriteBatch batch) {
        switch (currentStatus) {
            case AVAILABLE:
                batch.draw(new Texture("Simulation/Robots/robotAvailable.png"), currentPosition.getX(), currentPosition.getY(), Tile.TILE_SIZE, Tile.TILE_SIZE);
                break;
            case TASK_ASSIGNED:
                batch.draw(new Texture("Simulation/Robots/robotTaskAssigned.png"), currentPosition.getX(), currentPosition.getY(), Tile.TILE_SIZE, Tile.TILE_SIZE);
                break;
            case TASK_ASSIGNED_CARRYING:
            case CARRYING:
                batch.draw(new Texture("Simulation/Robots/robotTaskAssignedCarrying.png"), currentPosition.getX(), currentPosition.getY(), Tile.TILE_SIZE, Tile.TILE_SIZE);
                break;
            default:
                throw new RuntimeException("Robot status unavailable");
        }
    }

    public void assignTask(Task task) {
        currentTask = task;
        currentStatus = Status.TASK_ASSIGNED;
        ticksLeftForCurrentTask = pickUpTimeInTicks;
        pathToTarget = pathFinder.calculatePath(
                new GridCoordinate((int) currentPosition.getX(),(int) currentPosition.getY()), task.getDestination());

        // If the robot has to move
        if(pathToTarget.getCornersPath().size() > 1)
            assignTraverser();
    }

    private void assignTraverser(){
        currentTraverser = new LineTraverser(pathToTarget.getCornersPath().get(0),
                pathToTarget.getCornersPath().get(1), this);
    }

    public void cancelTask() {
        // TODO: 03/10/2019 Manage situations where the robot is in between tiles
    }

    public Position getCurrentPosition() {
        return currentPosition;
    }

    protected void decelerate() {
        if (currentSpeed > 0) {
            currentSpeed -= accelerationBinSecond / (float) SimulationApp.TICKS_PER_SECOND;

            if (currentSpeed < 0) {
                currentSpeed = 0;
            }
        }
    }

    protected void accelerate() {
        if (currentSpeed < maxSpeedBinsPerSecond) {
            currentSpeed += accelerationBinSecond / (float) SimulationApp.TICKS_PER_SECOND;

            if (currentSpeed > maxSpeedBinsPerSecond) {
                currentSpeed = maxSpeedBinsPerSecond;
            }
        }
    }

    public float getAccelerationBinSecond() {
        return accelerationBinSecond;
    }

    public static float getDecelerationBinSecond() {
        return decelerationBinSecond;
    }

    public float getCurrentSpeed() {
        return currentSpeed;
    }

    public static float getMaxSpeedBinsPerSecond() {
        return maxSpeedBinsPerSecond;
    }

    public void move(float deltaX, float deltaY) {
        currentPosition.setX(currentPosition.getX() + deltaX);
        currentPosition.setY(currentPosition.getY() + deltaY);
// // TODO: 03/10/2019 Make sure we don't go beyond target

    }
}
