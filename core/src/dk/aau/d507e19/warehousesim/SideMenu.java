package dk.aau.d507e19.warehousesim;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class SideMenu {

    private Button pauseButton;
    private ImageButton playButton;

    ShapeRenderer shapeRenderer;
    private Stage menuStage;

    public SideMenu(Viewport menuViewport){
        menuStage = new Stage(menuViewport);
        Gdx.input.setInputProcessor(menuStage);

        shapeRenderer = new ShapeRenderer();

        TextureRegion textRegion = new TextureRegion(new Texture(Gdx.files.internal("icons/play_icon.png")));
        pauseButton = new Button(new TextureRegionDrawable(textRegion));
        menuStage.addActor(pauseButton);
    }

    public void update(){
        menuStage.act();
    }

    public void render(OrthographicCamera camera){
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(0, 5, camera.viewportWidth - 5, camera.viewportHeight - 10);
        shapeRenderer.end();

        menuStage.draw();
    }

}
