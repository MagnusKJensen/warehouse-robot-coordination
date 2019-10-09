package dk.aau.d507e19.warehousesim;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.Astar;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.DummyPathFinder;
import dk.aau.d507e19.warehousesim.controller.robot.*;
import dk.aau.d507e19.warehousesim.storagegrid.StorageGrid;

import java.util.ArrayList;

public class Simulation {
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;

    private StorageGrid storageGrid;
    private ArrayList<Robot> robots = new ArrayList<>();
    private ArrayList<Robot> selectedRobots = new ArrayList<>();

    private long tickCount = 0L;

    public Simulation(){
        font = GraphicsManager.getFont();
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        storageGrid = new StorageGrid(WarehouseSpecs.wareHouseWidth, WarehouseSpecs.wareHouseHeight);
        initRobots();
    }

    private void initRobots() {
        // Auto generate robots
        for (int i = 0; i < WarehouseSpecs.numberOfRobots; i++) {
            robots.add(new Robot(new Position(i,0), new Astar(WarehouseSpecs.wareHouseWidth), this));
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
}
