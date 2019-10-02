package dk.aau.d507e19.warehousesim;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector3;
import dk.aau.d507e19.warehousesim.controller.robot.*;

import java.util.ArrayList;

public class Simulation {

    private SpriteBatch batch;
    private BitmapFont font;

    private StorageGrid storageGrid;
    private ArrayList<Robot> robots = new ArrayList<>();

    private long tickCount = 0L;

    public Simulation(){
        font = generateFont();
        batch = new SpriteBatch();
        storageGrid = new StorageGrid(WarehouseSpecs.wareHouseWidth, WarehouseSpecs.wareHouseHeight);
        initRobots();
    }

    private void initRobots() {
        // Auto generate robots
        for (int i = 0; i < WarehouseSpecs.numberOfRobots; i++) {
            robots.add(new Robot(new Position(i,0)));
        }

        // Assign test task to first robot
        robots.get(0).assignTask(new Task(new GridCoordinate(0,5), Action.PICK_UP));
        ArrayList<GridCoordinate> pathToTarget = new ArrayList<>();
        pathToTarget.add(new GridCoordinate(0,0));
        pathToTarget.add(new GridCoordinate(0,1));
        pathToTarget.add(new GridCoordinate(1,1));
        pathToTarget.add(new GridCoordinate(2,1));
        pathToTarget.add(new GridCoordinate(3,1));
        pathToTarget.add(new GridCoordinate(2,1));
        pathToTarget.add(new GridCoordinate(1,1));
        pathToTarget.add(new GridCoordinate(0,1));
        Path path = new Path(pathToTarget);
        robots.get(0).setPathToTarget(path);
    }

    private BitmapFont generateFont(){
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/OpenSans.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 12;
        parameter.minFilter = Texture.TextureFilter.Linear;
        parameter.magFilter = Texture.TextureFilter.Linear;
        BitmapFont font = generator.generateFont(parameter); // font size 12 pixels
        generator.dispose();
        return font;
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
        for(Robot robot : robots){
            robot.render(batch);
        }
        batch.end();

        Vector3 textPos = gridCamera.project(new Vector3(0.3f, 0.15f, 0));

        batch.setProjectionMatrix(fontCamera.combined);
        batch.begin();
        font.setColor(Color.RED);
        font.draw(batch, String.valueOf(tickCount), textPos.x, textPos.y);
        batch.end();
    }
}
