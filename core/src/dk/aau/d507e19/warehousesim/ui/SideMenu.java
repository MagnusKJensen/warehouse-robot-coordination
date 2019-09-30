package dk.aau.d507e19.warehousesim.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.Viewport;
import dk.aau.d507e19.warehousesim.SimulationApp;

public class SideMenu {

    private Button playBtn, pauseBtn, globalStepForwardBtn, globalStepBackBtn, fastForwardBtn;

    ShapeRenderer shapeRenderer;
    private Stage menuStage;
    private SimulationApp simulationApp;

    private static final String ICONS_PATH = "icons/";

    private Color menuBGColor = new Color(75f / 255f, 75f / 255f, 75f / 255f,  1);

    public SideMenu(Viewport menuViewport, final SimulationApp simApp){
        this.simulationApp = simApp;
        shapeRenderer = new ShapeRenderer();
        menuStage = new Stage(menuViewport);
        Gdx.input.setInputProcessor(menuStage);

        createButtons();
    }

    private void createButtons() {
        pauseBtn = new Button(loadDrawableIcon("pause.png"));
        playBtn = new Button(loadDrawableIcon("play.png"));
        fastForwardBtn = new Button(loadDrawableIcon("fast_forward.png"));
        globalStepBackBtn = new Button(loadDrawableIcon("global_step_back.png"));
        globalStepForwardBtn = new Button(loadDrawableIcon("global_step_forward.png"));

        int offset = 60;
        globalStepBackBtn.setPosition(offset, 10);
        pauseBtn.setPosition(offset + 30, 10);
        globalStepForwardBtn.setPosition(offset + 60, 10);
        playBtn.setPosition(offset + 90, 10);
        fastForwardBtn.setPosition(offset + 120, 10);
        //fastestForwardBtn.setPosition(15, 15); // TODO: 30/09/2019 add fastest forward

        // TODO: 30/09/2019 Clean the fuck up
        playBtn.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                simulationApp.play();
            }
        });
        pauseBtn.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                simulationApp.pause();
            }
        });
        globalStepBackBtn.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                simulationApp.globalStepBackWard();
            }
        });
        globalStepForwardBtn.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                simulationApp.globalStepForward();
            }
        });
        fastForwardBtn.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                simulationApp.fastForward();
            }
        });

        menuStage.addActor(fastForwardBtn);
        menuStage.addActor(playBtn);
        menuStage.addActor(pauseBtn);
        menuStage.addActor(globalStepBackBtn);
        menuStage.addActor(globalStepForwardBtn);
    }

    private TextureRegionDrawable loadDrawableIcon(String iconName) {
        return new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal(ICONS_PATH + iconName))));
    }

    public void update(){
        menuStage.act();
    }

    public void render(OrthographicCamera camera){
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.setColor(menuBGColor);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(0, 0, camera.viewportWidth, camera.viewportHeight);
        shapeRenderer.end();

        menuStage.draw();
    }

}
