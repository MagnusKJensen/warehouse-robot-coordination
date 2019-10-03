package dk.aau.d507e19.warehousesim.controller.pathAlgorithms;

import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.Path;

import java.util.ArrayList;

public class DummyPathFinder implements PathFinder {

    @Override
    public Path calculatePath(GridCoordinate start, GridCoordinate destination) {
        ArrayList<GridCoordinate> pathList = new ArrayList<>();

        pathList.addAll(generateHorizontalLine(start.getX(), destination.getX(), start.getY()));
        pathList.addAll(generateVerticalLine(start.getY(), destination.getY(), destination.getX()));


        for (GridCoordinate gc:pathList) {
            System.out.println("X: " + gc.getX() + ", Y:" + gc.getY());
        }

        return new Path(pathList);
    }

    private ArrayList<GridCoordinate> generateHorizontalLine(int startX, int endX, int y){
        ArrayList<GridCoordinate> coordinates = new ArrayList<>();
        if(startX > endX){
            for(int i = startX; i >= endX; i--){
                coordinates.add(new GridCoordinate(i, y));
            }
        } else {
            for(int i = startX; i <= endX; i++){
                coordinates.add(new GridCoordinate(i, y));
            }
        }

        System.out.println("CORDS:");
        for (GridCoordinate gc:coordinates) {
            System.out.println("X: " + gc.getX() + ", Y:" + gc.getY());
        }
        System.out.println("____________________");
        return coordinates;
    }

    private ArrayList<GridCoordinate> generateVerticalLine(int startY, int endY, int x){
        ArrayList<GridCoordinate> coordinates = new ArrayList<>();
        if(startY > endY){
            for(int i = startY; i >= endY; i--){
                coordinates.add(new GridCoordinate(x, i));
            }
        } else {
            for(int i = startY; i <= endY; i++){
                coordinates.add(new GridCoordinate(x, i));
            }
        }

        return coordinates;
    }
}
