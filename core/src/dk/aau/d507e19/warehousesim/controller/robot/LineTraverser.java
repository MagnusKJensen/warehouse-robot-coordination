package dk.aau.d507e19.warehousesim.controller.robot;

public class LineTraverser {

    private Robot robot;
    private Direction direction;
    private int totalDistance;
    private float distanceTraveled = 0f;

    public LineTraverser(GridCoordinate startCoordinate, GridCoordinate destinationCoordinate, Robot robot){
        this.robot = robot;
        this.direction = getDirection(startCoordinate.getX(), startCoordinate.getY(),
                          destinationCoordinate.getX(), destinationCoordinate.getY());
        totalDistance = getTotalDistance(startCoordinate, destinationCoordinate);
    }

    private Direction getDirection(int startX, int startY, int destinationX, int destinationY){
        if(startX < destinationX)
            return Direction.EAST;
        if (startX > destinationX)
            return Direction.WEST;
        if(startY < destinationY)
            return Direction.NORTH;
        if (startY > destinationY)
            return Direction.SOUTH;

        throw new IllegalArgumentException("Destination coordinate must be different from start coordinate");
    }

    private int getTotalDistance(GridCoordinate startCoordinate, GridCoordinate destinationCoordinate) {
        if(direction == Direction.EAST || direction == Direction.WEST){
            return Math.abs(destinationCoordinate.getX() - startCoordinate.getX());
        }else{
            return Math.abs(destinationCoordinate.getY() - startCoordinate.getY());
        }
    }


    public void traverse(){
        /*if(shouldAccelerate()){
            robot.accelerate();
        }else if(shouldDecelerate()){
            robot.decelerate();
        }*/

        /*if(targetCloserThanSpeed()) {
            currentSpeed = 0;
            currentPosition.setX(pathToTarget.getCornersPath().get(0).getX());
            currentPosition.setY(pathToTarget.getCornersPath().get(0).getY());
            pathToTarget.getCornersPath().remove(0);
        }*/

        //robot.move(robot.getCurrentSpeed() * direction.xDir ,robot.getCurrentSpeed() * direction.yDir);
        robot.move(0.1f * direction.xDir ,0.1f * direction.yDir);
        distanceTraveled += 0.1f * direction.xDir + 0.1f * direction.yDir;
    }

    private boolean shouldDecelerate() {

        return false;
    }

    public boolean shouldAccelerate(){
        return false;
    }

    private float maxSpeed(){
        return -1;
    }


    public boolean destinationReached() {
        return(distanceTraveled >= (float) totalDistance);
    }
/*
    private boolean targetCloserThanSpeed() {
        final float delta = 0.01f;
        // If moving in x direction and target closer than speed
        if (Math.abs(currentPosition.getX() - pathToTarget.getCornersPath().get(0).getX()) < (currentSpeed / SimulationApp.TICKS_PER_SECOND)
                && Math.abs(currentPosition.getY() - pathToTarget.getCornersPath().get(0).getY()) < delta){
            return true;
        }
        // If moving in y direction and target closer than speed
        else if (Math.abs(currentPosition.getY() - pathToTarget.getCornersPath().get(0).getY()) < (currentSpeed / SimulationApp.TICKS_PER_SECOND)
                && Math.abs(currentPosition.getX() - pathToTarget.getCornersPath().get(0).getX()) < delta){
            return true;
        }

        return false;
    }*/


}
