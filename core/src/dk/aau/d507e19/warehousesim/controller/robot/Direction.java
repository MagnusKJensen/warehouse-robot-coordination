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
}
