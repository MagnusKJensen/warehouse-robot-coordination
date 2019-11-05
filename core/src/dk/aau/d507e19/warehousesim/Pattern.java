package dk.aau.d507e19.warehousesim;

import dk.aau.d507e19.warehousesim.controller.robot.Direction;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.server.ReservationManager;
import dk.aau.d507e19.warehousesim.storagegrid.GridBounds;
import javafx.geometry.Bounds;

import java.util.ArrayList;

public class Pattern {

    public enum PatternType {
        STACKED_LINES, SPIRAL;
    }

    private PatternType patternType;
    private int padding;
    private GridBounds bounds;

    public Pattern(PatternType patternType, int padding, GridBounds bounds) {
        this.patternType = patternType;
        this.padding = padding;
        this.bounds = bounds;
    }

    public ArrayList<GridCoordinate> generatePattern(int points) {
        switch (patternType) {
            case SPIRAL:
                return generateSpiral(points);
            case STACKED_LINES:
                return generateStackedLines(points);
        }
        throw new IllegalArgumentException("No pattern generation method exists for the given patternType");
    }

    private ArrayList<GridCoordinate> generateStackedLines(int points) {
        ArrayList<GridCoordinate> gridCoordinates = new ArrayList<>();

        int stepsToSkip = 0;
        for (int y = bounds.startY; y < bounds.endY; y+= (padding + 1)) {
            for (int x = bounds.startX; x < bounds.endX; x++) {
                if(stepsToSkip == 0){
                    points--;
                    stepsToSkip = padding; // Reset steps to skip
                    gridCoordinates.add(new GridCoordinate(x, y));

                    if(points == 0)
                        return gridCoordinates;
                }else{
                    stepsToSkip--;
                }
            }
        }

        return gridCoordinates;
    }

    private ArrayList<GridCoordinate> generateSpiral(int points) {
        int xMin = bounds.startX - 1;
        int xMax = bounds.endX + 1;
        int yMin = bounds.startY;
        int yMax = bounds.endY + 1;

        int x = xMin, y = yMin;

        ArrayList<GridCoordinate> gridCoordinates = new ArrayList<>();
        Direction[] directions = {Direction.EAST, Direction.NORTH, Direction.WEST, Direction.SOUTH};

        int coordsToSkip = 0;
        while(points > 0){
            for(Direction direction : directions){
                switch (direction){
                    case NORTH:
                        yMax -= 1;
                        break;
                    case SOUTH:
                        yMin += 1;
                        break;
                    case EAST:
                        xMax -= 1;
                        break;
                    case WEST:
                        xMin += 1;
                        break;
                }

                // Avoid duplicating corners (no need for this on the first tile)
                if(x != bounds.startX || y != bounds.startY){
                    x += direction.xDir;
                    y += direction.yDir;
                }

                while(y >= yMin && y <= yMax && x >= xMin && x <= xMax){
                    if(coordsToSkip == 0){
                        gridCoordinates.add(new GridCoordinate(x, y));
                        points--;
                        coordsToSkip = padding;
                    }else{
                        coordsToSkip--;
                    }

                    if(points == 0){
                        for(GridCoordinate gridCoordinate : gridCoordinates){
                            System.out.println(gridCoordinate);
                        }
                        return gridCoordinates;
                    }

                    x += direction.xDir;
                    y += direction.yDir;
                }

                // Make sure we do no exceed bounds
                x = limit(xMin, xMax, x);
                y = limit(yMin, yMax, y);
            }

        }

        return null;
    }

    private int limit(int min, int max, int actual){
        if(actual > max) return max;
        if(actual < min) return min;
        return actual;
    }


}
