package dk.aau.d507e19.warehousesim;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
//import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.Astar;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.DummyPathFinder;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.rrt.RRTPlanner;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.rrt.RRTType;
import dk.aau.d507e19.warehousesim.controller.robot.*;
import dk.aau.d507e19.warehousesim.storagegrid.StorageGrid;

import java.util.ArrayList;

public class Simulation {
    private SpriteBatch batch;
    private BitmapFont font;
    private StorageGrid storageGrid;
    private ArrayList<Robot> robots = new ArrayList<>();

    private long tickCount = 0L;

    public Simulation(){
        font = GraphicsManager.getFont();
        batch = new SpriteBatch();
        storageGrid = new StorageGrid(WarehouseSpecs.wareHouseWidth, WarehouseSpecs.wareHouseHeight);
        initRobots();
    }

    private void initRobots() {
        // Auto generate robots
        for (int i = 0; i < WarehouseSpecs.numberOfRobots; i++) {
            robots.add(new Robot(new Position(i,0), new RRTPlanner(RRTType.RRT, null), this));
        }

        robots.add(new Robot(new Position(7,7), new DummyPathFinder(), this));
        robots.add(new Robot(new Position(5,5), new DummyPathFinder(), this));

        // Assign test task to first robot
        robots.get(0).assignTask(new Task(new GridCoordinate(3,6), Action.PICK_UP));
        robots.get(1).assignTask(new Task(new GridCoordinate(10,5), Action.PICK_UP));
        robots.get(2).assignTask(new Task(new GridCoordinate(0,8), Action.MOVE));
        robots.get(3).assignTask(new Task(new GridCoordinate(3,3), Action.PICK_UP));
        robots.get(4).assignTask(new Task(new GridCoordinate(1,1), Action.PICK_UP));
        robots.get(robots.size() - 1).assignTask(new Task(new GridCoordinate(0,0), Action.PICK_UP));
        robots.get(robots.size() - 2).assignTask(new Task(new GridCoordinate(2,9), Action.PICK_UP));
    }

    public void update(){
        tickCount += 1;
        for(Robot robot : robots){
            robot.update();
        }
    }

    public void render(OrthographicCamera gridCamera, OrthographicCamera fontCamera){
        storageGrid.render(gridCamera);

        batch.setProjectionMatrix(gridCamera.combined);
        batch.begin();
        for(Robot robot : robots)
            robot.render(batch);
        batch.end();

        renderTickCount(gridCamera, fontCamera);
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
}
