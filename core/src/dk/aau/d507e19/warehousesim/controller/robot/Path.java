package dk.aau.d507e19.warehousesim.controller.robot;

import java.util.ArrayList;

public class Path {
    ArrayList<GridCoordinate> path = new ArrayList<>();
    ArrayList<GridCoordinate> originalPath = new ArrayList<>();

    public Path(ArrayList<GridCoordinate> pathToTarget) {
        this.path = pathToTarget;
        originalPath.addAll(path);
        if(pathToTarget.isEmpty()) throw new IllegalArgumentException("Path must contain at least one coordinate");
        for (GridCoordinate gc:path) {
            System.out.println("X: " + gc.getX() + ", Y:" + gc.getY());
        }
        if(!isValidPath()) throw new IllegalArgumentException("Paths must be continuous");

        removeAllButCorners();

        for (GridCoordinate gc:path) {
            System.out.println("X: " + gc.getX() + ", Y:" + gc.getY());
        }

    }

    private void removeAllButCorners() {
        ArrayList<GridCoordinate> corners = new ArrayList<>();

        boolean xChanged;
        boolean yChanged;
        String lastDirection;

        // Add start position
        corners.add(path.get(0));

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

        path = corners;
    }

    public boolean isValidPath(){
        for (int i = 0; i < path.size() - 1; i++) {
            // If moving along the x axis
            if((Math.abs(path.get(i).getX() - path.get(i + 1).getX()) == 1)
                    && path.get(i).getY() == path.get(i + 1).getY()){
                continue;
            }
            // If moving along the y axis
            if((Math.abs(path.get(i).getY() - path.get(i + 1).getY()) == 1)
                    && path.get(i).getX() == path.get(i + 1).getX()){
                continue;
            }
            return false;
        }
        return true;
    }

    public ArrayList<GridCoordinate> getCornersPath() {
        return path;
    }

    public ArrayList<GridCoordinate> getFullPath() {
        return originalPath;
    }

}
