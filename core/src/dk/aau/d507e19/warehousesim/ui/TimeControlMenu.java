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
import dk.aau.d507e19.warehousesim.UpdateMode;

import java.awt.event.KeyListener;
import java.util.ArrayList;

public class TimeControlMenu {


    private Stage menuStage;
    private SimulationApp simulationApp;

    private Button playBtn, pauseBtn, globalStepForwardBtn, fastForwardBtn, fastestForwardBtn;
    private TextButton resetSimulationBtn;
    private ArrayList<Button> selectAbleButtons = new ArrayList<>();
    private Vector2 screenOffset;
    private SideMenu sideMenu;

    public TimeControlMenu(Stage menuStage, SimulationApp simulationApp, Vector2 screenOffset, SideMenu sideMenu) {
        this.sideMenu = sideMenu;
        this.menuStage = menuStage;
        this.simulationApp = simulationApp;
        this.screenOffset = screenOffset;
        createButtons();
    }

    private void createButtons() {
        pauseBtn = new Button(GraphicsManager.getTextureRegionDrawable("icons/pause.png"));
        playBtn = new Button(GraphicsManager.getTextureRegionDrawable("icons/play.png"));
        fastForwardBtn = new Button(GraphicsManager.getTextureRegionDrawable("icons/fast_forward.png"));
        globalStepForwardBtn = new Button(GraphicsManager.getTextureRegionDrawable("icons/global_step_forward.png"));
        fastestForwardBtn = new Button(GraphicsManager.getTextureRegionDrawable("icons/fastest_forward.png"));
        resetSimulationBtn = new TextButton("Reset", sideMenu.textButtonStyle);

        int screenXOffset = (int) screenOffset.x + 10;
        int screenYOffset = (int) screenOffset.y;

        pauseBtn.setPosition(screenXOffset + 30, screenYOffset + 10);
        globalStepForwardBtn.setPosition(screenXOffset + 60, screenYOffset + 10);
        playBtn.setPosition(screenXOffset + 90, screenYOffset + 10);
        fastForwardBtn.setPosition(screenXOffset + 120, screenYOffset + 10);
        fastestForwardBtn.setPosition(screenXOffset + 150, screenYOffset + 10);
        resetSimulationBtn.setPosition(screenXOffset + 190, screenYOffset + 10);

        addButtonListeners();
        setSelectableButtons();
        addButtonsToStage();
    }

    public void updatePauseButton(){
        if(simulationApp.getUpdateMode() == UpdateMode.MANUAL) {
            setManualButtonsDisabled(false);
            selectButton(pauseBtn);
        }
        if(simulationApp.getUpdateMode() == UpdateMode.REAL_TIME){
            setManualButtonsDisabled(true);
            selectButton(playBtn);
        }
    }

    private void addButtonListeners(){
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

        fastestForwardBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                simulationApp.fastestForward();
                setManualButtonsDisabled(true);
                selectButton(fastestForwardBtn);
            }
        });


        resetSimulationBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                simulationApp.resetSimulation();
            }
        });

        pauseBtn.setColor(SideMenu.selectedButtonColor);
    }

    private void setSelectableButtons() {
        selectAbleButtons.add(fastestForwardBtn);
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
        globalStepForwardBtn.setColor(globalStepColor);
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
        menuStage.addActor(globalStepForwardBtn);
        menuStage.addActor(resetSimulationBtn);
        menuStage.addActor(fastestForwardBtn);
    }

    public void resetTimeControlButtons(){
        setManualButtonsDisabled(false);
        selectButton(pauseBtn);
    }

    public void changeOffset(Vector2 offSet){
        this.screenOffset = offSet;

        int screenXOffset = (int)offSet.x;
        int screenYOffset = (int)offSet.y;

        pauseBtn.setPosition(screenXOffset + 30, screenYOffset + 10);
        globalStepForwardBtn.setPosition(screenXOffset + 60, screenYOffset + 10);
        playBtn.setPosition(screenXOffset + 90, screenYOffset + 10);
        fastForwardBtn.setPosition(screenXOffset + 120, screenYOffset + 10);
        fastestForwardBtn.setPosition(screenXOffset + 150, screenYOffset + 10);
        resetSimulationBtn.setPosition(screenXOffset + 190, screenYOffset + 10);
    }

}
