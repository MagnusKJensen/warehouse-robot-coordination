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
import dk.aau.d507e19.warehousesim.storagegrid.StorageGrid;

import java.util.ArrayList;

public class Simulation {
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;

    private Server server;
    private StorageGrid storageGrid;
    private ArrayList<Robot> robots = new ArrayList<>();
    private ArrayList<Robot> selectedRobots = new ArrayList<>();

    private long tickCount = 0L;

    private OrthographicCamera gridCamera;
    private OrthographicCamera fontCamera;
    private ScreenViewport gridViewport;

    private SimulationInputProcessor inputProcessor;

    public Simulation(SimulationApp simulationApp){
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
        initRobots();
    }

    private void initRobots() {
        // Auto generate robots
        for (int i = 0; i < WarehouseSpecs.numberOfRobots; i++)
            robots.add(new Robot(new Position(i, 0), i, this));

        // Assign test task to first robot
        robots.get(4).assignTask(new Task(new GridCoordinate(13,5), Action.PICK_UP));
        robots.get(3).assignTask(new Task(new GridCoordinate(12,5), Action.PICK_UP));
        robots.get(2).assignTask(new Task(new GridCoordinate(11,5), Action.MOVE));
        robots.get(1).assignTask(new Task(new GridCoordinate(10,5), Action.PICK_UP));
        robots.get(0).assignTask(new Task(new GridCoordinate(9,5), Action.PICK_UP));

        // For testing of the bin system
    /*    robots.get(robots.size() - 1).setBin(new Bin());
        robots.get(robots.size() - 1).setCurrentStatus(Status.CARRYING);
        robots.get(robots.size() - 1).assignTask(new Task(new GridCoordinate(2,0), Action.DELIVER));
        robots.get(robots.size() - 2).assignTask(new Task(new GridCoordinate(1,1), Action.PICK_UP));*/

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
}
