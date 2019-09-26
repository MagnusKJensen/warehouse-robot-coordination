package dk.aau.d507e19.warehousesim;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
        font = new BitmapFont();
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }

    public void update(){
        tickCount += 1;
    }

    public void render(OrthographicCamera camera){
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(0,0, 1, 0.2f);
        shapeRenderer.end();


        Vector3 screenPos = camera.project(new Vector3(0.35f, 0.18f, 0));
        batch.begin();
        font.setColor(Color.RED);
        font.draw(batch, String.valueOf(tickCount), screenPos.x, screenPos.y);
        batch.end();
    }

}
