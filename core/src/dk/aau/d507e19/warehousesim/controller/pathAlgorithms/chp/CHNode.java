package dk.aau.d507e19.warehousesim.controller.pathAlgorithms.chp;

import dk.aau.d507e19.warehousesim.controller.path.Path;
import dk.aau.d507e19.warehousesim.controller.path.Step;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;

import java.util.ArrayList;

class CHNode implements Comparable<CHNode>{

    private final GridCoordinate coords;

    private double hCost, fCost, gCost;
    private CHNode parentNode;
    private Path path;


    CHNode(GridCoordinate coords, CHNode parentNode, Path path, double gCost, double hCost) {
        this.coords = coords;
        this.parentNode = parentNode;
        this.path = path;

        this.gCost = gCost;
        this.hCost = hCost;
        this.fCost = gCost + hCost;
    }

    CHNode(GridCoordinate coords, Path path, double gCost, double hCost) {
        this(coords, null, path, gCost, hCost);
    }

    boolean hasParent (){
        return parentNode != null;
    }

    Path getPath() {
        return path;
    }

    double getHCost() {
        return hCost;
    }

    double getFCost() {
        return fCost;
    }

    double getGCost() {
        return gCost;
    }

    @Override
    public int compareTo(CHNode o) {
        return Double.compare(this.fCost, o.fCost);
    }

    public GridCoordinate getGridCoordinate() {
        return this.coords;
    }
}
