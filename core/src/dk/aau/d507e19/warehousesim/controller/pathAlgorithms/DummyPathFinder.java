package dk.aau.d507e19.warehousesim.controller.pathAlgorithms;

import dk.aau.d507e19.warehousesim.controller.path.Step;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.path.Path;

import java.util.ArrayList;

public class DummyPathFinder implements PathFinder {

    @Override
    public Path calculatePath(GridCoordinate start, GridCoordinate destination) {
        ArrayList<Step> pathList = new ArrayList<>();

        pathList.addAll(generateHorizontalLine(start.getX(), destination.getX(), start.getY()));
        pathList.remove(pathList.size() - 1);
        pathList.addAll(generateVerticalLine(start.getY(), destination.getY(), destination.getX()));

        return new Path(pathList);
    }

    private ArrayList<Step> generateHorizontalLine(int startX, int endX, int y){
        ArrayList<Step> coordinates = new ArrayList<>();
        if(startX > endX){
            for(int i = startX; i >= endX; i--){
                coordinates.add(new Step(i, y));
            }
        } else {
            for(int i = startX; i <= endX; i++){
                coordinates.add(new Step(i, y));
            }
        }

        return coordinates;
    }

    private ArrayList<Step> generateVerticalLine(int startY, int endY, int x){
        ArrayList<Step> coordinates = new ArrayList<>();
        if(startY > endY){
            for(int i = startY; i >= endY; i--){
                coordinates.add(new Step(x, i));
            }
        } else {
            for(int i = startY; i <= endY; i++){
                coordinates.add(new Step(x, i));
            }
        }

        return coordinates;
    }
}
