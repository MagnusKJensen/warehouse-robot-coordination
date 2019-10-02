package dk.aau.d507e19.warehousesim.controller.robot;

import java.util.ArrayList;

public class Task {
    private ArrayList<GridCoordinate> path = new ArrayList<>();
    private Action action;

    public Task(ArrayList<GridCoordinate> path, Action action) {
        if(path.isEmpty())
            throw new IllegalArgumentException("Path must have at least one coordinate");

        if(isValidPath()) this.path = path;
        else throw new IllegalArgumentException("Path must be continuous.");

        removeAllButCorners();

        this.action = action;
    }

    private void removeAllButCorners() {
        ArrayList<GridCoordinate> corners = new ArrayList<>();

        System.out.println("BEFORE: ");
        for (GridCoordinate grid:path) {
            System.out.println("X: " + grid.getX() + " Y: " + grid.getY());
        }

        boolean xChanged;
        boolean yChanged;
        String lastDirection;

        // If x changed
        if(path.get(0).getX() != path.get(1).getX()) lastDirection = "x";
        else lastDirection = "y"; // if y changed

        for (int i = 1; i < path.size(); i++) {
            xChanged = path.get(i - 1).getX() != path.get(i).getX();
            yChanged = path.get(i - 1).getY() != path.get(i).getY();

            if(xChanged && lastDirection.equals("y")){
                corners.add(path.get(i - 1));
                lastDirection = "x";
                continue;
            }

            if(yChanged && lastDirection.equals("x")){
                corners.add(path.get(i - 1));
                lastDirection = "y";

            }

        }
        // add last target
        corners.add(path.get(path.size() -1));

        path = corners;
    }

    public ArrayList<GridCoordinate> getStrippedPath() {
        return path;
    }

    public Action getAction() {
        return action;
    }

    public GridCoordinate getTarget(){
        if(!path.isEmpty()){
            return path.get(path.size() - 1);
        } else
            return null;
    }

    public boolean isValidPath(){
        // todo: Check that the path is a continues path without jumps - Philip
        return true;
    }

}
