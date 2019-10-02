package dk.aau.d507e19.warehousesim.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.Viewport;
import dk.aau.d507e19.warehousesim.SimulationApp;

import java.util.ArrayList;

public class SideMenu {

    private Button playBtn, pauseBtn, globalStepForwardBtn, globalStepBackBtn, fastForwardBtn;

    ShapeRenderer shapeRenderer;
    private Stage menuStage;
    private SimulationApp simulationApp;

    private static final String ICONS_PATH = "icons/";

    private static final Color disabledButtonColor = new Color(140f / 255f, 140f / 255f, 140f / 255f, 1f);
    private static final Color defaultButtonColor = new Color(245f / 255f, 245f / 255f, 245f / 255f, 1f);
    private static final Color selectedButtonColor = new Color(60f / 255f, 175f / 255f, 75f / 255f, 1f);

    private ArrayList<Button> selectAbleButtons = new ArrayList<>();
    private Color menuBGColor = new Color(75f / 255f, 75f / 255f, 75f / 255f, 1);

    public SideMenu(Viewport menuViewport, final SimulationApp simApp) {
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
        playBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                simulationApp.play();
                disableManualButtons();
                selectButton(playBtn);
            }
        });
        pauseBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                simulationApp.pause();
                enableManualButtons();
                selectButton(pauseBtn);
            }
        });
        globalStepBackBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                simulationApp.globalStepBackWard();
            }
        });
        globalStepForwardBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                simulationApp.globalStepForward();
            }
        });
        fastForwardBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                simulationApp.fastForward();
                disableManualButtons();
                selectButton(fastForwardBtn);
            }
        });

        pauseBtn.setColor(selectedButtonColor);

        setSelectableButtons();
        addButtonsToStage();
    }

    private void addButtonsToStage() {
        menuStage.addActor(fastForwardBtn);
        menuStage.addActor(playBtn);
        menuStage.addActor(pauseBtn);
        menuStage.addActor(globalStepBackBtn);
        menuStage.addActor(globalStepForwardBtn);
    }

    private void setSelectableButtons() {
        selectAbleButtons.add(fastForwardBtn);
        selectAbleButtons.add(playBtn);
        selectAbleButtons.add(pauseBtn);
    }

    private void selectButton(Button selectedButton) {
        for(Button btn : selectAbleButtons){
            if(btn.equals(selectedButton))
                btn.setColor(selectedButtonColor);
            else
                btn.setColor(defaultButtonColor);
        }
    }

    private void disableManualButtons() {
        globalStepForwardBtn.setTouchable(Touchable.disabled);
        globalStepBackBtn.setTouchable(Touchable.disabled);
        globalStepForwardBtn.setColor(disabledButtonColor);
        globalStepBackBtn.setColor(disabledButtonColor);
    }

    private void enableManualButtons() {
        globalStepForwardBtn.setTouchable(Touchable.enabled);
        globalStepBackBtn.setTouchable(Touchable.enabled);
        globalStepForwardBtn.setColor(defaultButtonColor);
        globalStepBackBtn.setColor(defaultButtonColor);
    }

    private TextureRegionDrawable loadDrawableIcon(String iconName) {
        SimulationApp.assetManager.load(ICONS_PATH + iconName, Texture.class);
        SimulationApp.assetManager.finishLoading();
        return new TextureRegionDrawable((Texture) SimulationApp.assetManager.get(ICONS_PATH + iconName));
    }

    public void update() {
        menuStage.act();
    }

    public void render(OrthographicCamera camera) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.setColor(menuBGColor);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(0, 0, camera.viewportWidth, camera.viewportHeight);
        shapeRenderer.end();

        menuStage.draw();
    }

}
