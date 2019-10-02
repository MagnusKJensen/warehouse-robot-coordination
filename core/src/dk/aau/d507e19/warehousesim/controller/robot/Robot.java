package dk.aau.d507e19.warehousesim.controller.robot;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import dk.aau.d507e19.warehousesim.Position;
import dk.aau.d507e19.warehousesim.SimulationApp;
import dk.aau.d507e19.warehousesim.Tile;
import dk.aau.d507e19.warehousesim.WareHouseSpecs;

public class Robot {
    private Position currentPosition;
    private Task currentTask;
    private Status currentStatus;

    /**
     * Robot STATS
     */
    // Pickup time
    private final static int pickUpTimeInTicks = SimulationApp.TICKS_PER_SECOND * WareHouseSpecs.robotPickUpSpeedInSeconds;
    private int ticksLeftForCurrentTask = 0;
    // Speed
    private final static float binsPerSecond = WareHouseSpecs.robotTravelSpeed / WareHouseSpecs.binSizeInMeters;

    public Robot(Position currentPosition) {
        this.currentPosition = currentPosition;
        currentStatus = Status.AVAILABLE;
    }

    public void update(){
        if(currentStatus == Status.TASK_ASSIGNED){
            // If standing on top of pickup
            if (currentTask.getPath().isEmpty()){
                // If done
                if(ticksLeftForCurrentTask == 0){
                    currentStatus = Status.CARRYING;
                } else {
                    // If still picking up the product
                    ticksLeftForCurrentTask -= 1;
                }
            } else {
                // If movement still needed
                currentPosition.setX(currentTask.getPath().get(0).getX());
                currentPosition.setY(currentTask.getPath().get(0).getY());
                currentTask.getPath().remove(0);
            }
        }
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
}
