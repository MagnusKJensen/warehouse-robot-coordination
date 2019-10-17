package dk.aau.d507e19.warehousesim.controller.robot;

public enum Direction {

    NORTH(0, 1), SOUTH(0, -1), WEST(-1, 0), EAST(1, 0);

    public int xDir, yDir;
    Direction(int xDir, int yDir){
        this.xDir = xDir;
        this.yDir = yDir;
    }

    public int getxDir() {
        return xDir;
    }

    public int getyDir() {
        return yDir;
    }

    public static Direction getOpposite(Direction direction){
        switch (direction){
            case NORTH: return SOUTH;
            case SOUTH: return NORTH;
            case WEST: return EAST;
            case EAST: return WEST;
        }
        throw new RuntimeException("Direction does not have an opposite defined");
    }
}
