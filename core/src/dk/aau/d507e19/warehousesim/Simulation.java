package dk.aau.d507e19.warehousesim;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector3;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;

import java.util.ArrayList;

public class Simulation {

    private SpriteBatch batch;
    private BitmapFont font;

    private StorageGrid storageGrid;
    private ArrayList<Robot> robots = new ArrayList<>();

    private long tickCount = 0L;

    private static final int STORAGE_WIDTH = 40, STORAGE_HEIGHT = 15;

    public Simulation(){
        font = generateFont();
        batch = new SpriteBatch();
        storageGrid = new StorageGrid(STORAGE_WIDTH, STORAGE_HEIGHT);
        initRobots();
    }

    private void initRobots() {

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
        for(Robot robot : robots){
            robot.render(batch);
        }

        Vector3 textPos = gridCamera.project(new Vector3(0.3f, 0.15f, 0));

        batch.setProjectionMatrix(fontCamera.combined);
        batch.begin();
        font.setColor(Color.RED);
        font.draw(batch, String.valueOf(tickCount), textPos.x, textPos.y);
        batch.end();
    }
}
