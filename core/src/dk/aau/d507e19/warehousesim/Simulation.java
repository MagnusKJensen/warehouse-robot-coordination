package dk.aau.d507e19.warehousesim;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import dk.aau.d507e19.warehousesim.controller.robot.*;
import dk.aau.d507e19.warehousesim.controller.server.Server;
import dk.aau.d507e19.warehousesim.input.SimulationInputProcessor;
import dk.aau.d507e19.warehousesim.storagegrid.ProductDistributor;
import dk.aau.d507e19.warehousesim.storagegrid.StorageGrid;
import dk.aau.d507e19.warehousesim.storagegrid.Tile;
import dk.aau.d507e19.warehousesim.storagegrid.product.Bin;

import java.util.ArrayList;

public class Simulation {
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;

    private Server server;
    private StorageGrid storageGrid;
    private ArrayList<Robot> robots = new ArrayList<>();
    private ArrayList<Robot> selectedRobots = new ArrayList<>();
    private Tile selectedTile;

    private long tickCount = 0L;

    private OrthographicCamera gridCamera;
    private OrthographicCamera fontCamera;
    private ScreenViewport gridViewport;

    private SimulationApp simulationApp;

    private SimulationInputProcessor inputProcessor;

    public Simulation(SimulationApp simulationApp){
        this.simulationApp = simulationApp;
        this.gridCamera = simulationApp.getWorldCamera();
        this.fontCamera = simulationApp.getFontCamera();
        this.gridViewport = simulationApp.getWorldViewport();

        server = new Server(this);
        inputProcessor = new SimulationInputProcessor(this);

        font = GraphicsManager.getFont();
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        // Just for testing and adding picker points
        ArrayList<GridCoordinate> pickerPoints = new ArrayList<>();
        pickerPoints.add(new GridCoordinate(0,0));
        pickerPoints.add(new GridCoordinate(2,0));

        storageGrid = new StorageGrid(WarehouseSpecs.wareHouseWidth, WarehouseSpecs.wareHouseHeight, pickerPoints);
        ProductDistributor.distributeProducts(storageGrid);

        initRobots();
    }

    private void initRobots() {
        // Auto generate robots
        for (int i = 0; i < WarehouseSpecs.numberOfRobots; i++)
            robots.add(new Robot(new Position(i, 0), i, this));

        // Assign test task to first robot
        robots.get(0).assignOrder(new Order(new GridCoordinate(3,6), RoboAction.PICK_UP));
        robots.get(1).assignOrder(new Order(new GridCoordinate(10,5), RoboAction.PICK_UP));
        robots.get(2).assignOrder(new Order(new GridCoordinate(0,8), RoboAction.MOVE));
        robots.get(3).assignOrder(new Order(new GridCoordinate(3,3), RoboAction.PICK_UP));
        robots.get(4).assignOrder(new Order(new GridCoordinate(1,2), RoboAction.PICK_UP));

        // For testing of the bin system
        robots.get(robots.size() - 1).setBin(new Bin());
        robots.get(robots.size() - 1).setCurrentStatus(Status.CARRYING);
        robots.get(robots.size() - 1).assignOrder(new Order(new GridCoordinate(2,0), RoboAction.DELIVER));
        robots.get(robots.size() - 2).assignOrder(new Order(new GridCoordinate(1,1), RoboAction.PICK_UP));

        selectedRobots.add(robots.get(0));
        selectedRobots.add(robots.get(1));
    }

    public void update(){
        tickCount += 1;
        for(Robot robot : robots){
            robot.update();
        }
    }

    public void render(OrthographicCamera gridCamera, OrthographicCamera fontCamera){
        shapeRenderer.setProjectionMatrix(gridCamera.combined);
        batch.setProjectionMatrix(gridCamera.combined);

        storageGrid.render(shapeRenderer, batch);
        renderSelectedRobotsPaths();
        renderRobots();
        renderTickCount(gridCamera, fontCamera);
    }

    private void renderSelectedRobotsPaths() {
        for(Robot robot : selectedRobots){
            if(robot.hasPlannedPath())
                storageGrid.renderPathOverlay(robot.getPathToTarget().getFullPath(), shapeRenderer);
        }
    }

    private void renderRobots(){
        batch.begin();
        for(Robot robot : robots)
            robot.render(batch);
        batch.end();
    }

    private void renderTickCount(OrthographicCamera gridCamera, OrthographicCamera fontCamera){
        Vector3 textPos = new Vector3(15 ,15 , 0);
        batch.setProjectionMatrix(fontCamera.combined);
        batch.begin();
        font.setColor(Color.BLUE);
        font.draw(batch, String.valueOf(tickCount), textPos.x, textPos.y);
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

    public long getSimulatedTime() {
        return tickCount * SimulationApp.MILLIS_PER_TICK;
    }

    public Position screenToWorldPosition(int screenX, int screenY){
        Vector3 worldCoords = gridViewport.unproject(new Vector3(screenX, screenY, 0));
        return new Position(worldCoords.x, worldCoords.y);
    }

    public SimulationInputProcessor getInputProcessor() {
        return inputProcessor;
    }

    public void selectTile(Tile tile){
        selectedTile = tile;
        simulationApp.getSideMenu().getTileInfoMenu().changeText(selectedTile.toString());
    }

    public void selectRobot(Robot robot) {
        if(selectedRobots.contains(robot)){
            selectedRobots.remove(robot);
        }else{
            selectedRobots.add(robot);
        }
    }

    public int getGridHeight() {
        return WarehouseSpecs.wareHouseHeight; // todo get from storagegrid instead of warehousespecs
    }

    public int getGridWidth() {
        return WarehouseSpecs.wareHouseWidth;
    }

    public Server getServer() {
        return server;
    }

    public Tile getSelectedTile() {
        return selectedTile;
    }
}
