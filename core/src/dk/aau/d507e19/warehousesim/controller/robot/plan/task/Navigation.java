package dk.aau.d507e19.warehousesim.controller.robot.plan.task;

import dk.aau.d507e19.warehousesim.TickTimer;
import dk.aau.d507e19.warehousesim.controller.path.Line;
import dk.aau.d507e19.warehousesim.controller.path.Path;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.controller.robot.RobotController;
import dk.aau.d507e19.warehousesim.controller.robot.plan.LineTraversal;

import java.util.ArrayList;

public class Navigation implements Task {

    private int maximumRetries = -1;
    private int timeBetweenRetriesInTicks = 30;

    private GridCoordinate destination;
    private Path path;
    private ArrayList<LineTraversal> lineTraversals = new ArrayList<>();

    private RobotController robotController;
    private Robot robot;

    private TickTimer retryTimer;
    private boolean isCompleted = false;


    public Navigation(RobotController robotController, GridCoordinate destination) {
        this.robotController = robotController;
        this.destination = destination;
    }

    public void setMaximumRetries(int maxRetries){
        this.maximumRetries = maxRetries;
    }

    @Override
    public void perform() {
        if(isCompleted())
            throw new RuntimeException("Can't perform task that is already completed");

        if(path == null){
            if(retryTimer.isDone()){
                retryTimer.decrement();
            }else {
                planPath();
                retryTimer.reset();
                traversePath();
            }
        }else{
            traversePath();
        }
    }

    private void traversePath() {
        if(lineTraversals.isEmpty())
            createLineTraversals();

        LineTraversal currentLineTraversal = lineTraversals.get(0);
        currentLineTraversal.perform();
        if(currentLineTraversal.isDone()){
            lineTraversals.remove(currentLineTraversal);

            // Path has finished traversing
            if(lineTraversals.isEmpty())
                isCompleted = true;
        }
    }

    private void createLineTraversals() {
        lineTraversals.clear();
        ArrayList<Line> lines = path.getLines();
        for(Line line : lines)
            lineTraversals.add(new LineTraversal(robot, line));
    }

    private void planPath() {
        GridCoordinate start = robot.getGridCoordinate();
        path = robotController.getPathFinder().calculatePath(start, destination).get();
    }

    public void interrupt(){
        this.path = null;
        lineTraversals.clear();
    }

    @Override
    public boolean isCompleted() {
        return isCompleted;
    }

    @Override
    public boolean hasFailed() {
        return false;
    }
}
