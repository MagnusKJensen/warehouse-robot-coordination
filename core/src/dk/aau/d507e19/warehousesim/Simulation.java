package dk.aau.d507e19.warehousesim;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import javafx.scene.Camera;

public class Simulation {

    private ShapeRenderer shapeRenderer;
    private Batch batch;
    private BitmapFont font = new BitmapFont();

    public Simulation(){
        shapeRenderer = new ShapeRenderer();
    }

    public void update(){

    }

    public void render(OrthographicCamera camera){
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        font.draw(batch, "test", 0, 0);
        batch.end();
    }

}
