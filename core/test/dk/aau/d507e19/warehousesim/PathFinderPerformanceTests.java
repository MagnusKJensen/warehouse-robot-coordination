package dk.aau.d507e19.warehousesim;

import dk.aau.d507e19.warehousesim.controller.path.Path;
import dk.aau.d507e19.warehousesim.controller.path.Step;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.PathFinderEnum;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.aStar.Astar;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.chp.CHPathfinder;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.controller.robot.RobotController;
import dk.aau.d507e19.warehousesim.controller.server.Reservation;
import dk.aau.d507e19.warehousesim.controller.server.ReservationManager;
import dk.aau.d507e19.warehousesim.controller.server.Server;
import dk.aau.d507e19.warehousesim.controller.server.TimeFrame;
import dk.aau.d507e19.warehousesim.repeatedtestutils.RepeatTest;
import dk.aau.d507e19.warehousesim.repeatedtestutils.RepeatedTestRule;
import dk.aau.d507e19.warehousesim.storagegrid.GridBounds;
import org.junit.*;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;


public class PathFinderPerformanceTests {
    private static long seed = 0L;
    private GridBounds gridBounds;
    private GridCoordinate destination;
    private GridCoordinate start;
    private Random random;
    private Server server;
    private Robot robot;
    private ReservationManager reservationManager;
    private static ArrayList<Long> TAResults = new ArrayList<>();
    private static ArrayList<Long> AStarResults = new ArrayList<>();
    private static int TAIterations = 0;
    private static int AStarIterations = 0;

    @Rule
    public RepeatedTestRule repeatRule = new RepeatedTestRule();

    @Before
    public void setUp() {
        random = new Random(seed);
        int width = 50;
        int height = 50;
        gridBounds = new GridBounds(width - 1,height - 1);
        ArrayList<GridCoordinate> randomPath = generateRandomPath(gridBounds);

        server = Mockito.mock(Server.class);
        when(server.getTimeInTicks()).thenReturn(0L);
        when(server.getGridWidth()).thenReturn(width);
        when(server.getGridHeight()).thenReturn(height);
        reservationManager = createFilledReservationTable(server, gridBounds, randomPath);
        when(server.getReservationManager()).thenReturn(reservationManager);

        robot = Mockito.mock(Robot.class);
        when(robot.getGridCoordinate()).thenReturn(randomPath.get(0));
        when(robot.getRobotID()).thenReturn(1);
        start = randomPath.get(0);
        destination = randomPath.get(randomPath.size() - 1);

        seed++;
    }

    @AfterClass
    public static void finish(){
        // long aStarAverageTime = getAverageTime(AStarResults, AStarIterations);
        // long TAAverageTime = getAverageTime(TAResults, TAIterations);

        // System.out.println("ASTAR average calculations time: " + aStarAverageTime + " (" + AStarIterations + " iterations)");
        // 7System.out.println("TA* average calculations time: " + TAAverageTime + " (" + TAIterations + " iterations)");
    }

    @Ignore
    @RepeatTest(times = 1000)
    public void testTAStarPerformance(){
        RobotController robotController = new RobotController(server, robot, PathFinderEnum.TA_STAR_PATHFINDER);
        when(robot.getRobotController()).thenReturn(robotController);
        CHPathfinder chPathfinder = CHPathfinder.defaultCHPathfinder(gridBounds, robot.getRobotController());

        long startTime = System.currentTimeMillis();
        Path path = chPathfinder.calculatePath(start, destination);
        long finishTime = System.currentTimeMillis();

        if(start.equals(path.getFullPath().get(0).getGridCoordinate())
                && destination.equals(path.getFullPath().get(path.getFullPath().size() - 1).getGridCoordinate())){
            TAIterations++;
            TAResults.add(finishTime - startTime);
        }

        System.out.println("TA* time used: " + (finishTime - startTime) + " ms" + ". Iteration " + TAIterations);
    }

    @Ignore
    @RepeatTest(times = 1000)
    public void testAStarPerformance(){
        Astar astar = new Astar(server, robot);

        long startTime = System.currentTimeMillis();
        Path path = astar.calculatePath(start, destination);
        long finishTime = System.currentTimeMillis();

        if(start.equals(path.getFullPath().get(0).getGridCoordinate())
                && destination.equals(path.getFullPath().get(path.getFullPath().size() - 1).getGridCoordinate())){
            AStarIterations++;
            AStarResults.add(finishTime - startTime);
        }

        System.out.println("A* time used: " + (finishTime - startTime) + " ms" + ". Iteration " + AStarIterations);
    }

    private ReservationManager createFilledReservationTable(Server server, GridBounds gridBounds, ArrayList<GridCoordinate> randomPath) {
        ReservationManager reservationManager = new ReservationManager(gridBounds.getWidth(), gridBounds.getHeight(), server);

        int reservationsToDo = 100;

        while(reservationsToDo > 0){
            GridCoordinate newReservation = new GridCoordinate(getRandomNumberInRange(gridBounds.startX, gridBounds.endX), getRandomNumberInRange(gridBounds.startY, gridBounds.endY));
            if(!randomPath.contains(newReservation) && reservationManager.canReserve(newReservation, TimeFrame.indefiniteTimeFrameFrom(0L))){
                reservationManager.reserve(new Reservation(Mockito.mock(Robot.class), newReservation, TimeFrame.indefiniteTimeFrameFrom(0L)));
                reservationsToDo--;
            }
        }

        return reservationManager;
    }

    private ArrayList<GridCoordinate> generateRandomPath(GridBounds gridBounds) {
        ArrayList<GridCoordinate> path = new ArrayList<>();
        GridCoordinate start = new GridCoordinate(getRandomNumberInRange(gridBounds.startX, gridBounds.endX), getRandomNumberInRange(gridBounds.startY, gridBounds.endY));
        path.add(start);

        // Max length = manhattan distance from one corner to the next
        int pathMaxLength = gridBounds.endX;

        while(path.size() < pathMaxLength){
            GridCoordinate currentTile = path.get(path.size() - 1);

            int distanceToUp = 0, distanceToRight = 0, distanceToDown = 0, distanceToLeft = 0;

            GridCoordinate upTile = new GridCoordinate(currentTile.getX(), currentTile.getY() + 1);
            if(isWithInGrid(upTile, gridBounds)){
                distanceToUp = manhattanDistance(start, upTile);
            }

            GridCoordinate rightTile = new GridCoordinate(currentTile.getX() + 1, currentTile.getY());
            if(isWithInGrid(rightTile, gridBounds)){
                distanceToRight = manhattanDistance(start, rightTile);
            }

            GridCoordinate downTile = new GridCoordinate(currentTile.getX(), currentTile.getY() - 1);
            if(isWithInGrid(downTile, gridBounds)){
                distanceToDown = manhattanDistance(start, downTile);
            }

            GridCoordinate leftTile = new GridCoordinate(currentTile.getX() - 1, currentTile.getY());
            if(isWithInGrid(leftTile, gridBounds)){
                distanceToLeft = manhattanDistance(start, leftTile);
            }

            int highestValue = findHighestValue(distanceToUp, distanceToRight, distanceToDown, distanceToLeft);

            ArrayList<GridCoordinate> candidatesForNextTile = new ArrayList<>();
            if(distanceToUp == highestValue) candidatesForNextTile.add(upTile);
            if(distanceToRight == highestValue) candidatesForNextTile.add(rightTile);
            if(distanceToDown == highestValue) candidatesForNextTile.add(downTile);
            if(distanceToLeft == highestValue) candidatesForNextTile.add(leftTile);

            path.add(candidatesForNextTile.get(random.nextInt(candidatesForNextTile.size())));
        }

        return path;
    }

    private int findHighestValue(int distanceToUp, int distanceToRight, int distanceToDown, int distanceToLeft) {
        int highestValue1 = Math.max(distanceToUp, distanceToRight);
        int highestValue2 = Math.max(distanceToDown, distanceToLeft);
        return Math.max(highestValue1, highestValue2);
    }

    private boolean isWithInGrid(GridCoordinate newTile, GridBounds gridBounds) {
        if(newTile.getX() > gridBounds.endX || newTile.getY() > gridBounds.endY){
            return false;
        }
        if(newTile.getX() < 0 || newTile.getY() < 0){
            return false;
        }

        return true;
    }

    private boolean gridCordIsContainedIn(GridCoordinate gridCoordinate, Path path) {
        for(Step step : path.getFullPath()){
            if(step.getGridCoordinate().equals(gridCoordinate))return true;
        }
        return false;
    }

    private int getRandomNumberInRange(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        return random.nextInt((max - min) + 1) + min;
    }

    public int manhattanDistance(GridCoordinate start, GridCoordinate dest){
        // distance = abs(ydistance) + abs(xdistance)
        return Math.abs(dest.getX() - start.getX()) + Math.abs(dest.getY() - start.getY());
    }

    public void printGrid(Path path){
        System.out.println("Dest: " + destination);
        for(int x = 0; x <= gridBounds.endX; x++){
            for(int y = 0; y <= gridBounds.endY; y++){
                if(new GridCoordinate(x,y).equals(destination)){
                    String str = String.format("%2s", "D");
                    System.out.print(str);
                }
                else if(new GridCoordinate(x,y).equals(start)){
                    String str = String.format("%2s", "S");
                    System.out.print(str);
                }
                else if(reservationManager.isReserved(new GridCoordinate(x,y), TimeFrame.indefiniteTimeFrameFrom(0L))){
                    String str = String.format("%2s", "X");
                    System.out.print(str);
                }
                else if (gridCordIsContainedIn(new GridCoordinate(x,y), path)){
                    String str = String.format("%2s", "P");
                    System.out.print(str);
                }
                else {
                    String str = String.format("%2s", " ");
                    System.out.print(str);
                }
            }
            System.out.println();
        }
    }

    public void printGrid(){
        System.out.println("Dest: " + destination);
        for(int x = 0; x <= gridBounds.endX; x++){
            for(int y = 0; y <= gridBounds.endY; y++){
                if(new GridCoordinate(x,y).equals(destination)){
                    String str = String.format("%2s", "D");
                    System.out.print(str);
                }
                else if(new GridCoordinate(x,y).equals(start)){
                    String str = String.format("%2s", "S");
                    System.out.print(str);
                }
                else if(reservationManager.isReserved(new GridCoordinate(x,y), TimeFrame.indefiniteTimeFrameFrom(0L))){
                    String str = String.format("%2s", "X");
                    System.out.print(str);
                }
                else {
                    String str = String.format("%2s", " ");
                    System.out.print(str);
                }
            }
            System.out.println();
        }
    }

    private static long getAverageTime(ArrayList<Long> results, long iterations) {
        long sum = 0;
        for(Long l : results){
            sum += l;
        }

        return sum / iterations;
    }
}
