package dk.aau.d507e19.warehousesim.controller.robot.plan;

import dk.aau.d507e19.warehousesim.Position;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;

public class SpeedCalculator {

    private Robot robot;
    private int totalDistance;
    private float achievableSpeed;
    private float breakingDistance;

    public SpeedCalculator(Robot robot, int totalDistance) {
        this.robot = robot;
        this.totalDistance = totalDistance;
        this.achievableSpeed = calculateAchievableSpeed();
        this.breakingDistance = calculateBreakingSpeed();
    }

    private float calculateAchievableSpeed(){
        float achievableSpeed =
                (float) Math.sqrt((2 * robot.getAccelerationBinSecond() * robot.getDecelerationBinSecond() * totalDistance)
                        /(robot.getAccelerationBinSecond() + robot.getDecelerationBinSecond()));
        if(achievableSpeed >= robot.getMaxSpeedBinsPerSecond()){
            return robot.getMaxSpeedBinsPerSecond();
        }

        return achievableSpeed;
    }

    private float calculateBreakingSpeed(){
        float breakingTime = calculateAchievableSpeed() / robot.getDecelerationBinSecond();
        return (float) (0.5f * robot.getDecelerationBinSecond() * Math.pow(breakingTime, 2));
    }

    public Position getPositionAfter(long ticks){
        
        return null;
    }

    public float getSpeedAfter(long ticks){

        return 0;
    }

    public float getAchievableSpeed() {
        return achievableSpeed;
    }

    public float getBreakingDistance() {
        return breakingDistance;
    }

}
