package dk.aau.d507e19.warehousesim.controller.robot;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import dk.aau.d507e19.warehousesim.Position;

public class Robot {


    private Position currentPosition;
    private Task currentTask;
    private Status currentStatus;

    public Robot(Position currentPosition) {
        this.currentPosition = currentPosition;
        currentStatus = Status.AVAILABLE;
    }

    public void update(){
        if(!currentPosition.equals(currentTask.getTarget())){

        }
    }

    public void render(SpriteBatch batch){

    }




}
