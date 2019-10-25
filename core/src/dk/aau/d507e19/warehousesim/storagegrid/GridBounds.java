package dk.aau.d507e19.warehousesim.storagegrid;

import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;

public class GridBounds {

    public final int startX, startY, endX, endY;

    public GridBounds(int startX, int startY, int endX, int endY) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }


    public GridBounds(int endX, int endY) {
        this.endX = endX;
        this.endY = endY;
        this.startX = 0;
        this.startY = 0;
    }

    public boolean isWithinBounds(GridCoordinate gridCoordinate){
        boolean isWithinXBounds = gridCoordinate.getX() >= startX && gridCoordinate.getX() <= endX;
        boolean isWithinYBounds = gridCoordinate.getY() >= startY && gridCoordinate.getY() <= endY;
        return isWithinXBounds && isWithinYBounds;
    }


}
