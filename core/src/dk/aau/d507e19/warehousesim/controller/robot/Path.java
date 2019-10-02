package dk.aau.d507e19.warehousesim.controller.robot;

import java.util.ArrayList;

public class Path {
    ArrayList<GridCoordinate> path = new ArrayList<>();
    ArrayList<GridCoordinate> originalPath = new ArrayList<>();

    public Path(ArrayList<GridCoordinate> pathToTarget) {
        this.path = pathToTarget;
        originalPath.addAll(path);
        if(!isValidPath()) throw new IllegalArgumentException("Paths must be continuous");
        removeAllButCorners();
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

            // Check if turned around. It cannot turn around with only 2 moves.
            if(i > 2){
                // If turning around the same way in x direction
                if(path.get(i).getX() == path.get(i - 2).getX()
                        && path.get(i).getY() == path.get(i - 2).getY()){
                    corners.add(path.get(i - 1));
                    lastDirection = "x";
                    continue;
                }

                // If turning around the same way in y direction
                if(path.get(i).getY() == path.get(i - 2).getY()
                        && path.get(i).getX() == path.get(i - 2).getX()){
                    corners.add(path.get(i - 1));
                    lastDirection = "y";
                    continue;
                }
            }

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

        System.out.println("AFTER: ");
        for (GridCoordinate grid : corners) {
            System.out.println("X: " + grid.getX() + " Y: " + grid.getY());
        }

        path = corners;
    }

    public boolean isValidPath(){
        for (int i = 0; i < path.size() - 1; i++) {
            if((path.get(i).getY() == path.get(i + 1).getY() - 1 || path.get(i).getY() == path.get(i + 1).getY() + 1)
                    && path.get(i).getX() == path.get(i + 1).getX()){
                continue;
            }

            if((path.get(i).getX() == path.get(i + 1).getX() - 1 || path.get(i).getX() == path.get(i + 1).getX() + 1)
                    && path.get(i).getY() == path.get(i + 1).getY()){
                continue;
            }
            System.out.println(path.get(i).getX() + ":" + path.get(i).getY());
            return false;
        }
        return true;
    }

    public ArrayList<GridCoordinate> getPath() {
        return path;
    }
}
