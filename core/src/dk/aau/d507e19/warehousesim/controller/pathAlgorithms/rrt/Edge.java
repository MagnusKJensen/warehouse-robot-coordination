package dk.aau.d507e19.warehousesim.controller.pathAlgorithms.rrt;

import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;

public class Edge {
    final private GridCoordinate point1,point2;
    final private double distance;

    public Edge(GridCoordinate point1, GridCoordinate point2) {
        this.point1 = point1;
        this.point2 = point2;
        distance = getDistanceBetweenPoints(this.point1,this.point2);
    }
    protected double getDistanceBetweenPoints(GridCoordinate pos1, GridCoordinate pos2) {
        return Math.sqrt(Math.pow(pos2.getX() - pos1.getX(), 2) + Math.pow(pos2.getY() - pos1.getY(), 2));
    }

    public GridCoordinate getPoint1() {
        return point1;
    }

    public GridCoordinate getPoint2() {
        return point2;
    }

    public double getDistance() {
        return distance;
    }
}
