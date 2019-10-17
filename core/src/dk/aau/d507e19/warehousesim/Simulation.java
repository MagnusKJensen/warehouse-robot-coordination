package dk.aau.d507e19.warehousesim;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import dk.aau.d507e19.warehousesim.controller.robot.*;
import dk.aau.d507e19.warehousesim.controller.server.Reservation;
import dk.aau.d507e19.warehousesim.controller.server.Server;
import dk.aau.d507e19.warehousesim.input.SimulationInputProcessor;
import dk.aau.d507e19.warehousesim.storagegrid.BinTile;
import dk.aau.d507e19.warehousesim.storagegrid.ProductDistributor;
import dk.aau.d507e19.warehousesim.storagegrid.StorageGrid;
import dk.aau.d507e19.warehousesim.storagegrid.Tile;
import dk.aau.d507e19.warehousesim.storagegrid.product.Product;
import dk.aau.d507e19.warehousesim.storagegrid.product.SKU;

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

        storageGrid = new StorageGrid(WarehouseSpecs.wareHouseWidth, WarehouseSpecs.wareHouseHeight, this);

        if(WarehouseSpecs.isRandomProductDistribution) ProductDistributor.distributeProductsRandomly(storageGrid);
        else ProductDistributor.distributeProducts(storageGrid);

        initRobots();
    }

    private void initRobots() {
        // Auto generate robots
        for (int i = 0; i < WarehouseSpecs.numberOfRobots; i++){
            robots.add(new Robot(new Position(i, 0), i, this));
            // Assign test task to first robot
            for(int j = 0; j < 5; j++){
                robots.get(i).assignOrder(new Order(new Product(new SKU("0"), 0), 1));
            }
        }
    }

    public void update(){
        tickCount += 1;
        for(Robot robot : robots){
            robot.update();
        }
        updateSideMenuScrollPanes();
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
        if(tickCount == 0){
            updateSideMenuScrollPanes();
        }
    }

    public void selectRobot(Robot robot) {
        if(selectedRobots.contains(robot)){
            selectedRobots.remove(robot);
        }else{
            selectedRobots.add(robot);
        }

        // Make sure, that the scroll panes will also update even before the program is running
        if(tickCount == 0){
            updateSideMenuScrollPanes();
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

    private void renderTickCount(OrthographicCamera gridCamera, OrthographicCamera fontCamera){
        Vector3 textPos = new Vector3(15 ,15 , 0);
        batch.setProjectionMatrix(fontCamera.combined);
        batch.begin();
        font.setColor(Color.WHITE);
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

    public long getTimeInTicks() {
        return tickCount;
    }
}
