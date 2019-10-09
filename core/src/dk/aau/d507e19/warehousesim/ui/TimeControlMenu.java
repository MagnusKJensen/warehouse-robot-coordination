package dk.aau.d507e19.warehousesim.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import dk.aau.d507e19.warehousesim.GraphicsManager;
import dk.aau.d507e19.warehousesim.SimulationApp;

import java.util.ArrayList;

public class TimeControlMenu {


    private Stage menuStage;
    private SimulationApp simulationApp;

    private Button playBtn, pauseBtn, globalStepForwardBtn, globalStepBackBtn, fastForwardBtn;
    private TextButton resetSimulationBtn;
    private ArrayList<Button> selectAbleButtons = new ArrayList<>();
    private Vector2 screenOffset;

    public TimeControlMenu(Stage menuStage, SimulationApp simulationApp, Vector2 screenOffset) {
        this.menuStage = menuStage;
        this.simulationApp = simulationApp;
        this.screenOffset = screenOffset;
        createButtons();
    }

    private void createButtons() {
        pauseBtn = new Button(GraphicsManager.getTextureRegionDrawable("icons/pause.png"));
        playBtn = new Button(GraphicsManager.getTextureRegionDrawable("icons/play.png"));
        fastForwardBtn = new Button(GraphicsManager.getTextureRegionDrawable("icons/fast_forward.png"));
        globalStepBackBtn = new Button(GraphicsManager.getTextureRegionDrawable("icons/global_step_back.png"));
        globalStepForwardBtn = new Button(GraphicsManager.getTextureRegionDrawable("icons/global_step_forward.png"));

        int screenXOffset = (int) screenOffset.x;
        int screenYOffset = (int) screenOffset.y;
        globalStepBackBtn.setPosition(screenXOffset, screenYOffset + 10);
        pauseBtn.setPosition(screenXOffset + 30, screenYOffset + 10);
        globalStepForwardBtn.setPosition(screenXOffset + 60, screenYOffset + 10);
        playBtn.setPosition(screenXOffset + 90, screenYOffset + 10);
        fastForwardBtn.setPosition(screenXOffset + 120, screenYOffset + 10);
        resetSimulationBtn.setPosition(screenXOffset, screenYOffset + 25);
        //fastestForwardBtn.setPosition(15, 15); // TODO: 30/09/2019 add fastest forward

        // TODO: 30/09/2019 Clean up
        playBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                simulationApp.play();
                setManualButtonsDisabled(true);
                selectButton(playBtn);
            }
        });

        pauseBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                simulationApp.pause();
                setManualButtonsDisabled(false);
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
                setManualButtonsDisabled(true);
                selectButton(fastForwardBtn);
            }
        });

        pauseBtn.setColor(SideMenu.selectedButtonColor);

        setSelectableButtons();
        addButtonsToStage();
    }

    private void setSelectableButtons() {
        selectAbleButtons.add(fastForwardBtn);
        selectAbleButtons.add(playBtn);
        selectAbleButtons.add(pauseBtn);
    }

    private void setManualButtonsDisabled(boolean disabled) {
        Touchable touchable;
        Color globalStepColor;
        if(disabled){
            touchable = Touchable.disabled;
            globalStepColor = SideMenu.disabledButtonColor;
        }else{
            touchable = Touchable.enabled;
            globalStepColor = SideMenu.defaultButtonColor;
        }

        globalStepForwardBtn.setTouchable(touchable);
        globalStepBackBtn.setTouchable(touchable);
        globalStepForwardBtn.setColor(globalStepColor);
        globalStepBackBtn.setColor(globalStepColor);
    }

    private void selectButton(Button selectedButton) {
        for(Button btn : selectAbleButtons){
            if(btn.equals(selectedButton))
                btn.setColor(SideMenu.selectedButtonColor);
            else
                btn.setColor(SideMenu.defaultButtonColor);
        }
    }

    private void addButtonsToStage() {
        menuStage.addActor(fastForwardBtn);
        menuStage.addActor(playBtn);
        menuStage.addActor(pauseBtn);
        menuStage.addActor(globalStepBackBtn);
        menuStage.addActor(globalStepForwardBtn);
    }




}
