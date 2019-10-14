package dk.aau.d507e19.warehousesim.controller.robot.plan;

import dk.aau.d507e19.warehousesim.controller.robot.Robot;

public class SpeedCalculator {

    private Robot robot;
    private int totalDistance;

    public SpeedCalculator(Robot robot, int totalDistance) {
        this.robot = robot;
        this.totalDistance = totalDistance;
    }

    public float calculateAchievableSpeed(){
        float achievableSpeed =
                (float) Math.sqrt((2 * robot.getAccelerationBinSecond() * robot.getDecelerationBinSecond() * totalDistance)
                        /(robot.getAccelerationBinSecond() + robot.getDecelerationBinSecond()));
        if(achievableSpeed >= robot.getMaxSpeedBinsPerSecond()){
            return robot.getMaxSpeedBinsPerSecond();
        }

        return achievableSpeed;
    }

    public float getBreakingDistance(){
        float breakingTime = calculateAchievableSpeed() / robot.getDecelerationBinSecond();
        float breakingDistance = (float) (0.5f * robot.getDecelerationBinSecond() * Math.pow(breakingTime, 2));
        return breakingDistance;
    }


}
