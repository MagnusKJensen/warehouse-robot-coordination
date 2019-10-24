package dk.aau.d507e19.warehousesim.exception;

import dk.aau.d507e19.warehousesim.controller.robot.Robot;

public class CollisionException extends RuntimeException {

    public CollisionException(Robot robot1, Robot robot2, long time) {
        System.err.println("Robot " + robot1.getRobotID() + " with position " + robot1.getCurrentPosition() +
                " collided with robot " + robot2.getRobotID()+ " with position " + robot2.getCurrentPosition() +
                " at time " + time);

    }
}
