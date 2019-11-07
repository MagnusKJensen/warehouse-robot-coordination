package dk.aau.d507e19.warehousesim.controller.robot.plan.task;

import dk.aau.d507e19.warehousesim.Simulation;
import dk.aau.d507e19.warehousesim.controller.robot.*;
import dk.aau.d507e19.warehousesim.controller.server.Server;

import java.util.Random;

public class Relocation implements Task {

    private GridCoordinate destination;
    private Server server;
    private RobotController robotController;
    private boolean completed;
    private Navigation navigation;
    private Random random;

    public Relocation(Server server, RobotController robotController) {
        this.server = server;
        this.robotController = robotController;
        this.random = new Random(Simulation.RANDOM_SEED);
    }

    @Override
    public void perform() {
        if(destination == null){
            Robot robot = robotController.getRobot();
            if(!robotController.hasOrderAssigned()){
                // Go to optimal spot to wait for task
                destination = server.getOptimalIdleRobotPosition();
            }else{
                do { // Find random neighbour tile to go to
                    Direction randomDirection = Direction.values()[random.nextInt(Direction.values().length)];
                    destination = new GridCoordinate(robot.getGridCoordinate().getX() + randomDirection.xDir,
                            robot.getGridCoordinate().getY() + randomDirection.yDir);
                }while (!server.getGridBounds().isWithinBounds(destination));
            }

            navigation = Navigation.getInstance(robotController, destination);
        }

        navigation.perform();
        if(navigation.isCompleted())
            complete();
    }

    private void complete(){
        completed = true;
    }

    @Override
    public boolean isCompleted() {
        return completed;
    }

    @Override
    public boolean hasFailed() {
        return false;
    }

    @Override
    public void setRobot(Robot robot) {
        this.robotController = robot.getRobotController();
    }

    @Override
    public boolean interrupt() {
        return false;
    }
}
