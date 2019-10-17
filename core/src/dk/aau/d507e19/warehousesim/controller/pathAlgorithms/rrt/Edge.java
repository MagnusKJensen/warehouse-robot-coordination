package dk.aau.d507e19.warehousesim.controller.pathAlgorithms.rrt;

import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;

public class Edge {
    final private GridCoordinate startPoint, endPoint;
    final private double distance;

    public Edge(GridCoordinate startPoint, GridCoordinate endPoint) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        distance = getDistanceBetweenPoints(this.startPoint,this.endPoint);
    }
    protected double getDistanceBetweenPoints(GridCoordinate pos1, GridCoordinate pos2) {
        return Math.sqrt(Math.pow(pos2.getX() - pos1.getX(), 2) + Math.pow(pos2.getY() - pos1.getY(), 2));
    }

    public GridCoordinate getStartPoint() {
        return startPoint;
    }

    public GridCoordinate getEndPoint() {
        return endPoint;
    }

    public double getDistance() {
        return distance;
    }
}
