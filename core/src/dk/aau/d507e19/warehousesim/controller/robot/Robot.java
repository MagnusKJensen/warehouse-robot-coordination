package dk.aau.d507e19.warehousesim.controller.robot;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import dk.aau.d507e19.warehousesim.Position;
import dk.aau.d507e19.warehousesim.SimulationApp;
import dk.aau.d507e19.warehousesim.Tile;
import dk.aau.d507e19.warehousesim.WarehouseSpecs;

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
    private final static float binsPerSecond = WarehouseSpecs.robotTopSpeed / WarehouseSpecs.binSizeInMeters;
    private final static float accelerationBinSecond = WarehouseSpecs.robotAcceleration / WarehouseSpecs.binSizeInMeters;

    public Robot(Position currentPosition) {
        this.currentPosition = currentPosition;
        currentStatus = Status.AVAILABLE;
    }

    public void update(){
        // todo: if robot gets task, where it is already on top of the product
        if(currentStatus == Status.TASK_ASSIGNED){
            /**
             * If standing on top of product
             */
            if (pathToTarget.getPath().isEmpty()){
                // If done
                if(ticksLeftForCurrentTask == 0){
                    currentStatus = Status.CARRYING;
                } else {
                    // If still picking up the product
                    ticksLeftForCurrentTask -= 1;
                }
            } else {
                /**
                 * If movement still needed
                 */
                // If not moving a full speed, accelerate!
                if(currentSpeed < binsPerSecond){
                    currentSpeed += accelerationBinSecond / (float) SimulationApp.TICKS_PER_SECOND;

                    if(currentSpeed > binsPerSecond){
                        currentSpeed = binsPerSecond;
                    }
                }
                // Target reached. Stop
                if(targetCloserThanSpeed()) {
                    currentSpeed = 0;
                    currentPosition.setX(pathToTarget.getPath().get(0).getX());
                    currentPosition.setY(pathToTarget.getPath().get(0).getY());
                    pathToTarget.getPath().remove(0);
                }
                // Moving up the x axis
                else if(currentPosition.getX() < pathToTarget.getPath().get(0).getX()){
                    currentPosition.setX(currentPosition.getX() + (currentSpeed / SimulationApp.TICKS_PER_SECOND));
                }
                // Moving down the x axis
                else if (currentPosition.getX() > pathToTarget.getPath().get(0).getX()){
                    currentPosition.setX(currentPosition.getX() - (currentSpeed / SimulationApp.TICKS_PER_SECOND));
                }
                // Moving up the y axis
                else if (currentPosition.getY() < pathToTarget.getPath().get(0).getY()){
                    currentPosition.setY(currentPosition.getY() + (currentSpeed / SimulationApp.TICKS_PER_SECOND));
                }
                // Moving down the y axis
                else if (currentPosition.getY() > pathToTarget.getPath().get(0).getY()){
                    currentPosition.setY(currentPosition.getY() + (currentSpeed / SimulationApp.TICKS_PER_SECOND));
                }
            }
        }
    }

    private boolean targetCloserThanSpeed() {
        final float delta = 0.01f;
        // If moving in x direction and target closer than speed
        if (Math.abs(currentPosition.getX() - pathToTarget.getPath().get(0).getX()) < (currentSpeed / SimulationApp.TICKS_PER_SECOND)
                && Math.abs(currentPosition.getY() - pathToTarget.getPath().get(0).getY()) < delta){
            return true;
        }
        // If moving in y direction and target closer than speed
        else if (Math.abs(currentPosition.getY() - pathToTarget.getPath().get(0).getY()) < (currentSpeed / SimulationApp.TICKS_PER_SECOND)
                && Math.abs(currentPosition.getX() - pathToTarget.getPath().get(0).getX()) < delta){
            return true;
        }

        return false;
    }

    public void render(SpriteBatch batch){
        switch (currentStatus) {
            case AVAILABLE:
                batch.draw(new Texture("Simulation/Robots/robotAvailable.png"), currentPosition.getX(),currentPosition.getY(), Tile.TILE_SIZE,Tile.TILE_SIZE);
                break;
            case TASK_ASSIGNED:
                batch.draw(new Texture("Simulation/Robots/robotTaskAssigned.png"), currentPosition.getX(),currentPosition.getY(),Tile.TILE_SIZE,Tile.TILE_SIZE);
                break;
            case TASK_ASSIGNED_CARRYING:
            case CARRYING:
                batch.draw(new Texture("Simulation/Robots/robotTaskAssignedCarrying.png"), currentPosition.getX(),currentPosition.getY(),Tile.TILE_SIZE,Tile.TILE_SIZE);
                break;
            default:
                throw new RuntimeException("Robot status unavailable");
        }
    }

    public void assignTask(Task task){
        currentTask = task;
        currentStatus = Status.TASK_ASSIGNED;
        ticksLeftForCurrentTask = pickUpTimeInTicks;
    }

    public Position getCurrentPosition() {
        return currentPosition;
    }

    public void setPathToTarget(Path pathToTarget) {
        this.pathToTarget = pathToTarget;
    }
}
