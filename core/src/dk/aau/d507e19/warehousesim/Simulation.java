package dk.aau.d507e19.warehousesim;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.rrt.Node;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.rrt.RRTPlanner;
import dk.aau.d507e19.warehousesim.controller.robot.*;
import dk.aau.d507e19.warehousesim.controller.server.Reservation;
import dk.aau.d507e19.warehousesim.controller.server.Server;
import dk.aau.d507e19.warehousesim.exception.CollisionException;
import dk.aau.d507e19.warehousesim.goal.Goal;
import dk.aau.d507e19.warehousesim.goal.OrderGoal;
import dk.aau.d507e19.warehousesim.input.SimulationInputProcessor;
import dk.aau.d507e19.warehousesim.storagegrid.*;
import dk.aau.d507e19.warehousesim.storagegrid.product.Product;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class Simulation {
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;

    private Server server;
    private StorageGrid storageGrid;
    private ArrayList<Robot> robots = new ArrayList<>();
    private ArrayList<Robot> selectedRobots = new ArrayList<>();
    private ArrayList<Robot> ctrlSelectedRobots = new ArrayList<>();
    private Tile selectedTile;

    private long tickCount = 0L;
    private long realTimeSinceStartInMS = 0L;

    private OrthographicCamera gridCamera;
    private OrthographicCamera fontCamera;
    private ScreenViewport gridViewport;

    private SimulationApp simulationApp;

    private SimulationInputProcessor inputProcessor;

    private long ordersProcessed = 0;

    private Goal goal;

    private long tickStopperGoal;

    private static WarehouseSpecs warehouseSpecs;

    public Simulation(SimulationApp simulationApp, WarehouseSpecs warehouseSpecs){
        this.simulationApp = simulationApp;
        this.gridCamera = simulationApp.getWorldCamera();
        this.fontCamera = simulationApp.getFontCamera();
        this.gridViewport = simulationApp.getWorldViewport();
        Simulation.warehouseSpecs = warehouseSpecs;

        inputProcessor = new SimulationInputProcessor(this);

        font = GraphicsManager.getFont();
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        storageGrid = new StorageGrid(Simulation.warehouseSpecs.wareHouseWidth, Simulation.warehouseSpecs.wareHouseHeight, this);
        if(Simulation.warehouseSpecs.isRandomProductDistribution) ProductDistributor.distributeProductsRandomly(storageGrid);
        else ProductDistributor.distributeProducts(storageGrid);

        server = new Server(this, storageGrid);

        goal = new OrderGoal(Simulation.warehouseSpecs.orderGoal, this);

        initRobots();
    }

    private void initRobots() {
        // Auto generate robots
        int x = 0, y = 0;
        ArrayList<GridCoordinate> gridCoordinates;
        gridCoordinates = Simulation.warehouseSpecs.robotPlacementPattern.generatePattern(Simulation.warehouseSpecs.numberOfRobots);

        int id = 0;
        for(GridCoordinate gridCoordinate : gridCoordinates)
            robots.add(new Robot(gridCoordinate.toPosition(), id++, this));
    }

    public void update(){
        tickCount += 1;
        for(Robot robot : robots){
            robot.update();
        }
        server.updateNew();
        goal.update();
        if(Simulation.warehouseSpecs.collisionDetectedEnabled){
            checkForCollisions();
        }
        updateSideMenuScrollPanes();

        if(tickStopperGoal == tickCount) simulationApp.pause();
    }

    private void checkForCollisions() {
        for (Robot robot1 : robots){
            for(Robot robot2 : robots){
                if(robot1.getRobotID() != robot2.getRobotID()){
                    if(robot1.collidesWith(robot2.getCurrentPosition())) throw new CollisionException(robot1, robot2, server.getTimeInTicks());
                }
            }
        }
    }


    private void updateSideMenuScrollPanes() {
        // Update the robot bin content live
        if(!selectedRobots.isEmpty()){
            ArrayList<Product> prods;
            Robot lastSelectedRobot = selectedRobots.get(selectedRobots.size() - 1);
            if(lastSelectedRobot.isCarrying()) prods = lastSelectedRobot.getBin().getProducts();
            else prods = new ArrayList<>();

            simulationApp.getSideMenu().getBinContentScrollPanes().updateRobotBinContent(prods, lastSelectedRobot.getRobotID());
        }

        // Update the tile content
        if(selectedTile instanceof BinTile){
            BinTile tile = (BinTile) selectedTile;

            ArrayList<Product> prods;
            if(tile.getBin() == null) prods = new ArrayList<>();
            else prods = tile.getBin().getProducts();

            simulationApp.getSideMenu().getBinContentScrollPanes().updateBinContent(prods, tile.getPosX(), tile.getPosY());
        }
    }

    public void selectTile(Tile tile){
        selectedTile = tile;

        // Make sure, that the scroll panes will also update even before the program is running
        if(simulationApp.getUpdateMode() == UpdateMode.MANUAL){
            updateSideMenuScrollPanes();
        }
    }

    public void ctrlSelectRobot(Robot robot){
        if(ctrlSelectedRobots.contains(robot)){
            ctrlSelectedRobots.remove(robot);
        }else if(!ctrlSelectedRobots.isEmpty()){
            //always make sure that only one robot is pressed(trees take a lot of screen space)
            ctrlSelectedRobots.remove(0);
            ctrlSelectedRobots.add(robot);
        }else{
            ctrlSelectedRobots.add(robot);
        }
    }
    public void selectRobot(Robot robot) {
        if(selectedRobots.contains(robot)){
            selectedRobots.remove(robot);
        }else{
            selectedRobots.add(robot);
        }

        // Make sure, that the scroll panes will also update even before the program is running
        if(simulationApp.getUpdateMode() == UpdateMode.MANUAL){
            updateSideMenuScrollPanes();
        }
    }

    public void selectAllRobots(){
        // If all robots are selected already
        if(selectedRobots.containsAll(robots)) selectedRobots.clear();
        else selectedRobots.addAll(robots);
    }

    public void render(OrthographicCamera gridCamera, OrthographicCamera fontCamera){
        shapeRenderer.setProjectionMatrix(gridCamera.combined);
        batch.setProjectionMatrix(gridCamera.combined);

        storageGrid.render(shapeRenderer, batch);
        renderSelectedRobotsPaths();
        renderCtrlSelectedRobotTrees();
        renderRobots();
        renderTickCountAndRealTime(gridCamera, fontCamera);
    }

    public void renderCtrlSelectedRobotTrees() {
        for(Robot robot : ctrlSelectedRobots){
            //only do if robot path algo is RRT variant
            if(robot.getRobotController().getPathFinder() instanceof RRTPlanner){
                RRTPlanner planner = (RRTPlanner) robot.getRobotController().getPathFinder();
                ArrayList<Node<GridCoordinate>> listOfNodes = new ArrayList<>(planner.getPlanner().allNodesMap.values());
                //if called before a robot has started growing its tree, root might be null
                if(!listOfNodes.isEmpty()){
                    storageGrid.renderTreeOverlay(listOfNodes, shapeRenderer,planner.getPlanner().getPath());
                }
            }
        }
    }

    private void renderSelectedRobotsPaths() {
        for(Robot robot : selectedRobots){
            server.getReservationManager().removeOutdatedReservationsBy(robot); // todo move this elsewhere
            ArrayList<Reservation> reservations = server.getReservationManager().getReservationsBy(robot);
            storageGrid.renderPathOverlay(reservations, shapeRenderer);

        }
    }

    private void renderRobots(){
        batch.begin();
        for(Robot robot : robots)
            robot.render(batch);
        batch.end();
    }

    private void renderTickCountAndRealTime(OrthographicCamera gridCamera, OrthographicCamera fontCamera){
        Vector3 textPos = new Vector3(15 ,15 , 0);
        batch.setProjectionMatrix(fontCamera.combined);
        batch.begin();
        font.setColor(Color.WHITE);

        realTimeSinceStartInMS =  tickCount / SimulationApp.TICKS_PER_SECOND;

        DecimalFormat df = new DecimalFormat("0.000");
        df.setRoundingMode(RoundingMode.HALF_UP);
        String tickCountAndSeconds = " " + "seconds " + df.format((tickCount / (double)SimulationApp.TICKS_PER_SECOND)) + " / " +  tickCount;
        font.draw(batch, tickCountAndSeconds, textPos.x, textPos.y);
        batch.end();
    }

    public ArrayList<Robot> getAllRobots() {
        return robots;
    }

    public void dispose(){
        batch.dispose();
    }

    public StorageGrid getStorageGrid() {
        return storageGrid;
    }

    public OrthographicCamera getGridCamera() {
        return gridCamera;
    }

    public OrthographicCamera getFontCamera() {
        return fontCamera;
    }

    public long getSimulatedTimeInMS() {
        return tickCount * SimulationApp.MILLIS_PER_TICK;
    }

    public Position screenToWorldPosition(int screenX, int screenY){
        Vector3 worldCoords = gridViewport.unproject(new Vector3(screenX, screenY, 0));
        return new Position(worldCoords.x, worldCoords.y);
    }

    public SimulationInputProcessor getInputProcessor() {
        return inputProcessor;
    }

    public int getGridHeight() {
        return Simulation.warehouseSpecs.wareHouseHeight;
    }

    public int getGridWidth() {
        return Simulation.warehouseSpecs.wareHouseWidth;
    }

    public Server getServer() {
        return server;
    }

    public Tile getSelectedTile() {
        return selectedTile;
    }

    public long getTimeInTicks() {
        return tickCount;
    }

    public SimulationApp getSimulationApp() {
        return simulationApp;
    }

    public void incrementOrderProcessedCount(){
        ++ordersProcessed;
    }

    public long getOrdersProcessed() {
        return ordersProcessed;
    }

    public Goal getGoal() {
        return goal;
    }

    public void setTickStopperGoal(long tickStopperGoal) {
        this.tickStopperGoal = tickStopperGoal;
    }

    public static WarehouseSpecs getWarehouseSpecs() {
        return warehouseSpecs;
    }

    public static void setWarehouseSpecs(WarehouseSpecs warehouseSpecs) {
        Simulation.warehouseSpecs = warehouseSpecs;
    }
}
