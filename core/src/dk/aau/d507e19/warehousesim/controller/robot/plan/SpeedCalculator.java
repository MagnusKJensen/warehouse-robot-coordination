package dk.aau.d507e19.warehousesim.controller.robot.plan;

import dk.aau.d507e19.warehousesim.Position;
import dk.aau.d507e19.warehousesim.SimulationApp;
import dk.aau.d507e19.warehousesim.TimeUtils;
import dk.aau.d507e19.warehousesim.controller.path.Line;
import dk.aau.d507e19.warehousesim.controller.path.Step;
import dk.aau.d507e19.warehousesim.controller.robot.Direction;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;

public class SpeedCalculator {

    private Robot robot;
    private float length;
    private Position start;
    private Direction direction;

    // The max speed achievable before having to decelerate
    private float achievableSpeed;

    // Initial speed
    private float initialSpeed;


    /* Time spent accelerating, driving at max speed and decelerating */
    private float pauseDuration;
    private float accelerationDuration;
    private float maxSpeedDuration;
    private float breakingDuration;
    private float totalDuration;

    /* Distance passed during acceleration, driving at max speed and deceleration */
    private float breakingDistance;
    private float maxSpeedDistance;
    private float accelerationDistance;
    private float totalDistance;

    private enum Phase {
        PAUSING_PHASE, ACCELERATION_PHASE, MAX_SPEED_PHASE, DECELERATION_PHASE, FINISHED;
    }

    public SpeedCalculator(Robot robot, Line line) {
        this.robot = robot;
        this.start = line.getStart().getGridCoordinate().toPosition();
        this.length = line.getLength();
        this.initialSpeed = robot.getCurrentSpeed();
        pauseDuration = (line.getStart().isWaitingStep()) ? line.getStart().getWaitTimeInTicks() : 0L;
        direction = line.getDirection();
        calculateConstants();
    }
    public SpeedCalculator(Robot robot, Position start, GridCoordinate end, float length){
        if(start.isSameAs(end))
            throw new RuntimeException("Start must be different from end");

        this.robot = robot;
        this.start = start;
        this.length = length;
        this.initialSpeed = robot.getCurrentSpeed();
        pauseDuration = 0;
        direction = determineDirection(start,end);
        calculateConstants();
    }

    private void calculateConstants() {
        achievableSpeed = calculateAchievableSpeed();

        breakingDistance = calculateBreakingDistance();
        accelerationDistance = calculateAccelerationDistance();
        maxSpeedDistance = length - accelerationDistance - breakingDistance;
        totalDistance = breakingDistance + maxSpeedDistance + accelerationDistance;

        accelerationDuration = (achievableSpeed - initialSpeed) / robot.getAccelerationBinSecond();
        maxSpeedDuration = maxSpeedDistance / robot.getMaxSpeedBinsPerSecond();
        breakingDuration = achievableSpeed / robot.getDecelerationBinSecond();

        pauseDuration = TimeUtils.tickToSeconds(pauseDuration);
        totalDuration = pauseDuration + accelerationDuration + maxSpeedDuration + breakingDuration;
    }

    private float calculateAchievableSpeed() {
        float d = robot.getDecelerationBinSecond();
        float a = robot.getAccelerationBinSecond();
        float v0 = initialSpeed;

        float vmax = (float) Math.sqrt((d*Math.pow(v0, 2) + 2*a*length*d)/(a+d));
        if(vmax > robot.getMaxSpeedBinsPerSecond()) vmax = robot.getMaxSpeedBinsPerSecond();
        return vmax;


        /*
        float ToUOverA = 2*initialSpeed/robot.getAccelerationBinSecond();
        float etOverA = 1/robot.getAccelerationBinSecond();
        float etOverD = 1/robot.getDecelerationBinSecond();
        float uIAndenOverA = (float) Math.pow(initialSpeed,2)/robot.getAccelerationBinSecond();

        float achievableSpeed = (float) (((ToUOverA) + Math.sqrt(Math.pow(ToUOverA,2) - 4 *(etOverA+etOverD)*(uIAndenOverA-2*length)))/
                (2*(etOverA + etOverD)));

        float achievableSpeed1 =
                (float) Math.sqrt((2 * robot.getAccelerationBinSecond() * robot.getDecelerationBinSecond() * length)
                        / (robot.getAccelerationBinSecond() + robot.getDecelerationBinSecond()));
        if (achievableSpeed>= robot.getMaxSpeedBinsPerSecond()) {
            return robot.getMaxSpeedBinsPerSecond();
                    return achievableSpeed;

        }*/

    }

    private float calculateBreakingDistance() {
        float breakingTime = achievableSpeed / robot.getDecelerationBinSecond();   // t = v / a
        return (float) (0.5f * robot.getDecelerationBinSecond() * Math.pow(breakingTime, 2)); // s = 1/2 a t^2
    }

    private float calculateAccelerationDistance() {
        float accelerationTime = (achievableSpeed - initialSpeed) / robot.getAccelerationBinSecond();              // t = (v - v1) / a
        return (float) (initialSpeed * accelerationTime + 0.5f * robot.getAccelerationBinSecond() * Math.pow(accelerationTime, 2)); // s = v1 + 1/2 a t^2 //v1 = current speed, which in this case is initial spd
    }

    public float getAchievableSpeed() {
        return achievableSpeed;
    }

    public float getBreakingDistance() {
        return breakingDistance;
    }

    public float getSpeedAfter(long ticks) {
        float timeInSeconds = (float) ticks / SimulationApp.TICKS_PER_SECOND;
        Phase phase = getPhase(timeInSeconds);

        if (phase == Phase.ACCELERATION_PHASE) {
            if(initialSpeed > 0){
                if(achievableSpeed < robot.getAccelerationBinSecond()*timeInSeconds + initialSpeed)
                    return achievableSpeed;
                return robot.getAccelerationBinSecond()*timeInSeconds + initialSpeed;
            }
            return robot.getAccelerationBinSecond() * timeInSeconds;
        } else if (phase == Phase.MAX_SPEED_PHASE) {
            return robot.getMaxSpeedBinsPerSecond();
        } else if (phase == Phase.DECELERATION_PHASE) {
            float timeSpentDecelerating = timeInSeconds - pauseDuration - accelerationDuration - maxSpeedDuration;
            return achievableSpeed - (robot.getDecelerationBinSecond() * timeSpentDecelerating);
        } else { // Phase == Finished or Phase == Pausing
            return 0;
        }
    }

    public Position getPositionAfter(long ticks) {
        float timeInSeconds = (float) ticks / (float) SimulationApp.TICKS_PER_SECOND;
        Phase phase = getPhase(timeInSeconds);
        float distance = 0;

        switch (phase) {
            case PAUSING_PHASE:
                distance = 0;
                break;
            case ACCELERATION_PHASE:
                distance = distanceWithConstantAcceleration(timeInSeconds - pauseDuration);
                break;

            case MAX_SPEED_PHASE:
                distance = distanceWithConstantAcceleration(accelerationDuration)
                        + distanceAtMaxSpeed(timeInSeconds - pauseDuration - accelerationDuration);
                break;
            case DECELERATION_PHASE:
                distance = distanceWithConstantAcceleration(accelerationDuration)
                        + distanceAtMaxSpeed(maxSpeedDuration)
                        + distanceWithDeceleration(timeInSeconds - pauseDuration - accelerationDuration - maxSpeedDuration);
                break;
            case FINISHED:
                distance = length;
                break;

        }


        float distX = direction.xDir * distance;
        float distY = direction.yDir * distance;
        return new Position(start.getX() + distX, start.getY() + distY);
    }

    private float distanceWithConstantAcceleration(float timeInSeconds) {
        float acceleration = robot.getAccelerationBinSecond();
        return (initialSpeed * timeInSeconds) + 0.5f * acceleration * (float) Math.pow(timeInSeconds, 2); // s = v_initial + 1/2 a t^2
    }

    private float distanceAtMaxSpeed(float timeInSeconds) {
        return robot.getMaxSpeedBinsPerSecond() * timeInSeconds;
    }

    private float distanceWithDeceleration(float timeInSeconds) {
        float deceleration = robot.getDecelerationBinSecond();
        return distanceAtConstantSpeed(timeInSeconds, achievableSpeed) - (0.5f * deceleration * (float) Math.pow(timeInSeconds, 2));
    }

    private float distanceAtConstantSpeed(float timeInSeconds, float speed) {
        return timeInSeconds * speed;
    }

    private Phase getPhase(float timeInSeconds) {
        if (timeInSeconds <= pauseDuration) {
            // If still performing the initial acceleration
            return Phase.PAUSING_PHASE;
        }else if (timeInSeconds <= pauseDuration + accelerationDuration) {
            // If still performing the initial acceleration
            return Phase.ACCELERATION_PHASE;
        } else if (timeInSeconds <= pauseDuration + accelerationDuration + maxSpeedDuration) {
            // Full speed reached, but not breaking yet
            return Phase.MAX_SPEED_PHASE;
        } else if (timeInSeconds <= pauseDuration + accelerationDuration + maxSpeedDuration + breakingDuration) {
            // Started breaking, but not stopped yet
            return Phase.DECELERATION_PHASE;
        } else {
            // The entire line has been traversed
            return Phase.FINISHED;
        }
    }

    public long amountOfTicksToReach() {
        return amountOfTicksToReach(length);
    }

    public long amountOfTicksToReach(float distance) {
        float timeToReach;

        if (distance <= accelerationDistance) {
            timeToReach = (float) Math.sqrt((2f * distance) / robot.getAccelerationBinSecond());
            timeToReach += pauseDuration;
        } else if (distance <= accelerationDistance + maxSpeedDistance) {
            float distanceAtMaxSpeed = distance - accelerationDistance;
            timeToReach = accelerationDuration + (distanceAtMaxSpeed / robot.getMaxSpeedBinsPerSecond());
            timeToReach += pauseDuration;
        } else if (distance < totalDistance) {
            float distanceSpentBreaking = distance - accelerationDistance - maxSpeedDistance;
            float timeSpentBreaking = (float)
                    -(-achievableSpeed + (Math.sqrt(Math.pow(achievableSpeed, 2)
                            - (2 * distanceSpentBreaking * robot.getDecelerationBinSecond()))))
                    / (robot.getDecelerationBinSecond());

            timeToReach = timeSpentBreaking + accelerationDuration + maxSpeedDuration + pauseDuration;
        } else {
            timeToReach = pauseDuration + accelerationDuration + maxSpeedDuration + breakingDuration;
        }

        return TimeUtils.secondsToTicks(timeToReach);
    }

    public long getTotalTimeInTicks() {
        return TimeUtils.secondsToTicks(totalDuration);
    }
    public Direction getDirection() {
        return direction;
    }

    private static Direction determineDirection(Position start, GridCoordinate end) {
        if (start.getX() < end.getX())
            return Direction.EAST;
        if (start.getX() > end.getX())
            return Direction.WEST;
        if (start.getY() < end.getY())
            return Direction.NORTH;
        if (start.getY() > end.getY())
            return Direction.SOUTH;

        throw new IllegalArgumentException("Destination coordinate must be different from start coordinate");
    }


}
