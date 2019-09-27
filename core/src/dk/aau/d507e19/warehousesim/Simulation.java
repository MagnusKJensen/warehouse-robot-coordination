package dk.aau.d507e19.warehousesim;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import javafx.scene.Camera;

import java.awt.*;

public class Simulation {

    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private BitmapFont font;
    private OrthographicCamera fontCamera;

    private long tickCount = 0L;

    public Simulation(){
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        font = generateFont();
    }

    private BitmapFont generateFont(){
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/OpenSans.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 12;
        parameter.borderColor = Color.RED;
        parameter.minFilter = Texture.TextureFilter.Linear;
        parameter.magFilter = Texture.TextureFilter.Linear;
        BitmapFont font = generator.generateFont(parameter); // font size 12 pixels
        generator.dispose();
        return font;
    }

    public void update(){
        tickCount += 1;
    }

    public void render(OrthographicCamera camera){
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(0,0, 1, 0.2f);
        shapeRenderer.rect(5,5, 5, 5);
        shapeRenderer.end();

        batch.begin();
        font.setColor(Color.RED);
        font.draw(batch, String.valueOf(tickCount), 15, 10);
        batch.end();
    }

}
