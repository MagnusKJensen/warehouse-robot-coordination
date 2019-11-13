package dk.aau.d507e19.warehousesim.controller.server;

import com.badlogic.gdx.graphics.Color;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.storagegrid.GridBounds;

import java.util.ArrayList;

public class HeatMap {

    public static final Color coldColor = new Color( 51f / 255f, 191f / 255f, 232f / 255f, 0.6f);
    public static final Color warmColor = new Color( 243f / 255f, 91f / 255f, 33f / 255f, 0.6f);
    public static final Color neutralColor = new Color( 255f / 255f, 255f / 255f, 255f / 255f, 0.1f);

    static class Neighbour{
        final GridCoordinate gridCoordinate;
        final int manhattanDistance;

        public Neighbour(GridCoordinate gridCoordinate, int manhattanDistance) {
            this.gridCoordinate = gridCoordinate;
            this.manhattanDistance = manhattanDistance;
        }
    }

    private static final int heatFactor = 1;

    // Distance at which neighbours will be affected
    private static final int robotHeat = 3 * heatFactor;
    private static final int edgeHeat = 2 * heatFactor;
    private static final int reservationHeat = 1 * heatFactor;
    private static final int indefiniteReservationHeat = 3 * heatFactor;

    private static final int pickerCooling = 4 * heatFactor;
    private static final int pickerMinDistance = 3;

    public static GridCoordinate getLeastCrowdedCoordinate(Server server){
        int[][] heatMap = getHeatMap(server);

        int coolestVal = heatMap[0][0];
        GridCoordinate coolestCoordinate = new GridCoordinate(0, 0);
        for(int x = 0; x < heatMap[0].length; x++){
            for(int y = 0; y < heatMap.length; y++){
                if(heatMap[x][y] < coolestVal){
                    coolestVal = heatMap[x][y];
                    coolestCoordinate = new GridCoordinate(x, y);
                }
            }
        }

        return coolestCoordinate;
    }

    public static int[][] getHeatMap(Server server){
        int[][] heatMap = new int[server.getGridBounds().getWidth()][server.getGridBounds().getHeight()];
        addRobotHeat(server, heatMap);
        // addEdgeHeat(heatMap);
        addPickerHeat(server, heatMap);
        addReservationsHeat(server, heatMap);
        return heatMap;
    }

    private static void addReservationsHeat(Server server, int[][] heatMap) {
        ArrayList<Reservation> reservations = server.getReservationManager().getAllReservations();

        for(Reservation reservation : reservations){
            if(reservation.getTimeFrame().getTimeMode() == TimeFrame.TimeMode.UNBOUNDED){
                ArrayList<Neighbour> neighbours = getNeighbours(reservation.getGridCoordinate(), server.getGridBounds(), indefiniteReservationHeat);
                for(Neighbour neighbour : neighbours){
                    int heat = indefiniteReservationHeat - neighbour.manhattanDistance;
                    heatMap[neighbour.gridCoordinate.getX()][neighbour.gridCoordinate.getY()] += heat;
                }
            }else{
                ArrayList<Neighbour> neighbours = getNeighbours(reservation.getGridCoordinate(), server.getGridBounds(), reservationHeat);
                for(Neighbour neighbour : neighbours){
                    int heat = reservationHeat - neighbour.manhattanDistance;
                    heatMap[neighbour.gridCoordinate.getX()][neighbour.gridCoordinate.getY()] += heat;
                }
            }
        }
    }

    private static void addPickerHeat(Server server, int[][] heatMap) {
        ArrayList<GridCoordinate> pickers = server.getPickerPoints();
        for(GridCoordinate picker : pickers){
            ArrayList<Neighbour> neighbours = getNeighbours(picker, server.getGridBounds(), Math.abs(pickerCooling));
            for(Neighbour neighbour : neighbours){
                if(neighbour.manhattanDistance < pickerMinDistance)
                    continue;

                int heat = - (pickerCooling - neighbour.manhattanDistance);
                heatMap[neighbour.gridCoordinate.getX()][neighbour.gridCoordinate.getY()] += heat;
            }
        }
    }

    private static void addEdgeHeat(int[][] heatMap) {
        int width = heatMap[0].length;
        int height = heatMap.length;

        for(int x = 0; x < heatMap[0].length; x++){
            int verticalEdgeDist = Math.min(x, (width - 1) - x);
            int verticalEdgeFactor = edgeHeat - verticalEdgeDist;
            if(verticalEdgeFactor < 0)  verticalEdgeFactor = 0;

            for(int y = 0; y < heatMap.length; y++){
                int horizontalEdgeDist = Math.min(y, (height - 1) - y);
                int horizontalEdgeFactor = edgeHeat - horizontalEdgeDist;
                if(horizontalEdgeFactor < 0)  horizontalEdgeFactor = 0;
                heatMap[x][y] += horizontalEdgeFactor + verticalEdgeFactor;
            }
        }
    }

    private static void addRobotHeat(Server server, int[][] heatMap) {
        ArrayList<Robot> robots = server.getAllRobots();
        for(Robot robot : robots){
            ArrayList<Neighbour> neighbours = getNeighbours(robot.getApproximateGridCoordinate(), server.getGridBounds(), robotHeat);
            for(Neighbour neighbour : neighbours){
                int heat = robotHeat - neighbour.manhattanDistance;
                heatMap[neighbour.gridCoordinate.getX()][neighbour.gridCoordinate.getY()] += heat;
            }
        }
    }

    // Gets all neighbours within the given distance
    public static ArrayList<Neighbour> getNeighbours(GridCoordinate center, GridBounds bounds, int manhattanDistance){
        ArrayList<Neighbour> neighbours = new ArrayList<>();

        int centerX = center.getX();
        int centerY = center.getY();
        int currentY;
        // Start from the bottom
        currentY = center.getY() - manhattanDistance;
        int endY = center.getY() + manhattanDistance;

        while(currentY <= endY){
            // Max distance to go out vertically to each side
            int xDist = manhattanDistance - Math.abs((currentY - center.getY()));
            int yDist = currentY - centerY;
            GridCoordinate lineCenter = new GridCoordinate(centerX, currentY);
            neighbours.addAll(verticalLine(lineCenter, xDist, yDist));

            currentY++;
        }

        // Remove neighbours located outside the grid
        neighbours.removeIf((n) -> !bounds.isWithinBounds(n.gridCoordinate));
        return neighbours;
    }

    private static ArrayList<Neighbour> verticalLine(GridCoordinate center, int xDist, int yDist){
        ArrayList<Neighbour> neighbours = new ArrayList<>();
        int centerX = center.getX();
        int centerY = center.getY();
        yDist = Math.abs(yDist);

        // Add neighbours to each side
        while(xDist > 0){
            neighbours.add(new Neighbour(new GridCoordinate(centerX + xDist, centerY), xDist + yDist));
            neighbours.add(new Neighbour(new GridCoordinate(centerX - xDist, centerY), xDist + yDist));
            xDist--;
        }

        // Add line center
        neighbours.add(new Neighbour(new GridCoordinate(centerX, centerY), yDist));

        return neighbours;
    }

    public static Color heatColor(int i){
        float amount = Math.abs(((float) i) * 0.3f) / ((float) heatFactor);
        amount = Math.min(amount, 1f);

        if(i < 0)
            return new Color(neutralColor).lerp(coldColor, amount);
        else
            return new Color(neutralColor).lerp(warmColor, amount);

    }
}
