package dk.aau.d507e19.warehousesim.controller.robot;

import java.util.ArrayList;

public class Task {
    private ArrayList<GridCoordinate> path = new ArrayList<>();
    private Action action;

    public Task(ArrayList<GridCoordinate> path, Action action) {
        if(path.isEmpty())
            throw new IllegalArgumentException("Path must have at least one coordinate");

        this.path = path;
        this.action = action;
    }

    public ArrayList<GridCoordinate> getPath() {
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

}
