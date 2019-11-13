package dk.aau.d507e19.warehousesim;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.google.gson.Gson;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.PathFinderEnum;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.rrt.Node;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.rrt.RRTPlanner;
import dk.aau.d507e19.warehousesim.controller.robot.*;
import dk.aau.d507e19.warehousesim.controller.server.Reservation;
import dk.aau.d507e19.warehousesim.controller.server.Server;
import dk.aau.d507e19.warehousesim.controller.server.taskAllocator.TaskAllocatorEnum;
import dk.aau.d507e19.warehousesim.exception.CollisionException;
import dk.aau.d507e19.warehousesim.goal.Goal;
import dk.aau.d507e19.warehousesim.goal.OrderGoal;
import dk.aau.d507e19.warehousesim.input.SimulationInputProcessor;
import dk.aau.d507e19.warehousesim.statistics.StatisticsManager;
import dk.aau.d507e19.warehousesim.storagegrid.*;
import dk.aau.d507e19.warehousesim.storagegrid.product.Product;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

import static dk.aau.d507e19.warehousesim.storagegrid.Tile.TILE_SIZE;

public class Simulation {

    public static long RANDOM_SEED;

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

    public static String PATH_TO_RUN_CONFIGS = System.getProperty("user.dir") + File.separator + "warehouseconfigurations/";
    public static String CURRENT_RUN_CONFIG;
    private static WarehouseSpecs warehouseSpecs;
    private static PathFinderEnum pathFinder;
    private static TaskAllocatorEnum taskAllocator;

    private Date simulationStartTime;

    private StatisticsManager statisticsManager;
    private boolean showHeatMap;

    private boolean shouldRenderGridAndRobots = true;

    private GridBounds renderedBounds;

    // Used for fast no graphics simulations
    public Simulation(long randSeed, String runConfigName, PathFinderEnum pathfinder, TaskAllocatorEnum taskAllocator){
        RANDOM_SEED = randSeed;
        Simulation.warehouseSpecs = readWarehouseSpecsFromFile(runConfigName);
        Simulation.CURRENT_RUN_CONFIG = runConfigName;
        Simulation.pathFinder = pathfinder;
        Simulation.taskAllocator = taskAllocator;

        storageGrid = new StorageGrid(Simulation.getWarehouseSpecs().wareHouseWidth, Simulation.getWarehouseSpecs().wareHouseHeight, this);
        if(Simulation.getWarehouseSpecs().isRandomProductDistribution) ProductDistributor.distributeProductsRandomly(storageGrid);
        else ProductDistributor.distributeProducts(storageGrid);

        server = new Server(this, storageGrid);

        goal = new OrderGoal(Simulation.warehouseSpecs.orderGoal, this);

        initRobots();

        simulationStartTime = new Date(System.currentTimeMillis());
        statisticsManager = new StatisticsManager(this);
    }

    public Simulation(long randSeed, String runConfigName, SimulationApp simulationApp, String pathToRunConfig){
        RANDOM_SEED = randSeed;
        Simulation.CURRENT_RUN_CONFIG = runConfigName;
        this.simulationApp = simulationApp;
        this.gridCamera = simulationApp.getWorldCamera();
        this.fontCamera = simulationApp.getFontCamera();
        this.gridViewport = simulationApp.getWorldViewport();

        Simulation.warehouseSpecs = readWarehouseSpecsFromFile(pathToRunConfig);
        Simulation.pathFinder = simulationApp.getPathFinderSelected();
        Simulation.taskAllocator = simulationApp.getTaskAllocatorSelected();

        inputProcessor = new SimulationInputProcessor(this);

        font = GraphicsManager.getFont();
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        storageGrid = new StorageGrid(Simulation.getWarehouseSpecs().wareHouseWidth, Simulation.getWarehouseSpecs().wareHouseHeight, this);
        if(Simulation.getWarehouseSpecs().isRandomProductDistribution) ProductDistributor.distributeProductsRandomly(storageGrid);
        else ProductDistributor.distributeProducts(storageGrid);

        server = new Server(this, storageGrid);

        goal = new OrderGoal(Simulation.warehouseSpecs.orderGoal, this);

        initRobots();

        simulationStartTime = new Date(System.currentTimeMillis());

        statisticsManager = new StatisticsManager(this);

        updateRenderedBounds();
    }

    private WarehouseSpecs readWarehouseSpecsFromFile(String specFileName) {
        File runConfigFile = new File(PATH_TO_RUN_CONFIGS + File.separator + specFileName);
        Gson gson = new Gson();
        try(BufferedReader reader = new BufferedReader(new FileReader(runConfigFile.getPath()))){
            WarehouseSpecs specs = gson.fromJson(reader, WarehouseSpecs.class);
            return specs;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
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
        server.update();
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

        if(shouldRenderGridAndRobots){
            storageGrid.render(shapeRenderer, batch);
            renderSelectedRobotsPaths();
            renderCtrlSelectedRobotTrees();
            if(showHeatMap) storageGrid.renderHeatMap(server.getHeatMap(), shapeRenderer);
            renderRobots();
        }

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
                drawTransparentRectangle(robot);
            }
        }
    }

    private void drawTransparentRectangle(Robot robot) {
        Gdx.gl.glEnable(GL30.GL_BLEND);
        shapeRenderer.setColor(189f/255f, 109f/255f, 227f/255f, 0.7f);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(robot.getCurrentPosition().getX(), robot.getCurrentPosition().getY(), TILE_SIZE, TILE_SIZE);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL30.GL_BLEND);

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

    public static PathFinderEnum getPathFinder() {
        return pathFinder;
    }

    public static void setPathFinder(PathFinderEnum pathFinder) {
        Simulation.pathFinder = pathFinder;
    }

    public static TaskAllocatorEnum getTaskAllocator() {
        return taskAllocator;
    }

    public static void setTaskAllocator(TaskAllocatorEnum taskAllocatorEnum) {
        Simulation.taskAllocator = taskAllocatorEnum;
    }

    public Date getSimulationStartTime() {
        return simulationStartTime;
    }

    public StatisticsManager getStatisticsManager() {
        return statisticsManager;
    }

    public void toggleHeatMap() {
        showHeatMap = !showHeatMap;
    }

    public void toggleRenderGrid(){
        shouldRenderGridAndRobots = !shouldRenderGridAndRobots;
    }

    public void updateRenderedBounds(){
        final int padding = 2;
        int maxTilesRenderedVertically = (int) Math.ceil(gridCamera.viewportHeight * gridCamera.zoom);
        int maxTilesRenderedHorizontally = (int) Math.ceil(gridCamera.viewportWidth * gridCamera.zoom);

        int tileXOffset = (int) gridCamera.position.x;
        int tileYOffset = (int) gridCamera.position.y;

        int xLowerBound = Math.max(0, tileXOffset - (maxTilesRenderedHorizontally / 2) - padding);
        int yLowerBound = Math.max(0, tileYOffset - (maxTilesRenderedVertically / 2) - padding);

        int xUpperBound = Math.min(getGridWidth() - 1, tileXOffset + (maxTilesRenderedHorizontally / 2) + padding);
        int yUpperBound = Math.min(getGridHeight() - 1, tileYOffset + (maxTilesRenderedVertically / 2) + padding);

        //System.out.println("(" + xLowerBound + ", " + yLowerBound + ")" + " to (" + xUpperBound + "," + yUpperBound +  ")");
        renderedBounds = new GridBounds(xLowerBound, yLowerBound, xUpperBound, yUpperBound);
    }

    public GridBounds getRenderedBounds() {
        return renderedBounds;
    }
}
