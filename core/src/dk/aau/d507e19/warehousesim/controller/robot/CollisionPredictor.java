package dk.aau.d507e19.warehousesim.controller.robot;

import dk.aau.d507e19.warehousesim.controller.server.Server;

public class CollisionPredictor {

    private Robot robot;

    public CollisionPredictor(Robot robot, Server server){
        this.robot = robot;
    }

    public boolean willCollide(){
        float breakingTime = robot.getCurrentSpeed() / robot.getDecelerationBinSecond();   // t = v / a
        float breakingDistance = (float) (0.5f * robot.getDecelerationBinSecond() * Math.pow(breakingTime, 2)); // s = 1/2 a t^2




        return false;
    }






}
