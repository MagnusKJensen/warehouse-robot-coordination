package dk.aau.d507e19.warehousesim.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import dk.aau.d507e19.warehousesim.SimulationApp;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.PathFinderEnum;
import dk.aau.d507e19.warehousesim.controller.server.taskAllocator.TaskAllocatorEnum;

public class TaskAllocationDropDown {
    private SideMenu sideMenu;
    private Stage menuStage;
    private SimulationApp simulationApp;
    private Skin skin;
    private Vector2 screenOffSet;
    private Text textAboveDropDown;
    private final String TEXT_ABOVE_DROP_DOWN = "Select task allocating algorithm";
    private SelectBox<String> selectBox;
    private int PADDING_LEFT_SIDE = 10;

    public TaskAllocationDropDown(Stage menuStage, SimulationApp simulationApp, Vector2 screenOffSet, SideMenu sideMenu) {
        this.sideMenu = sideMenu;
        this.menuStage = menuStage;
        this.simulationApp = simulationApp;
        this.screenOffSet = screenOffSet;
        this.skin = new Skin(Gdx.files.internal("data/uiskin.json"));

        // Create text above the dropdown menu
        this.textAboveDropDown = new Text(TEXT_ABOVE_DROP_DOWN, screenOffSet.x + PADDING_LEFT_SIDE, screenOffSet.y, Color.CORAL);
        menuStage.addActor(textAboveDropDown);

        createDropDown();
    }

    private void createDropDown() {
        selectBox = new SelectBox<>(skin);

        Array<String> taskAllocatorEnumNames = new Array<>();
        for(TaskAllocatorEnum taskAllEnum : TaskAllocatorEnum.values()){
            taskAllocatorEnumNames.add(taskAllEnum.getName());
        }
        selectBox.setItems(taskAllocatorEnumNames);

        selectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String selected = selectBox.getSelected();
                TaskAllocatorEnum taskAllEnum = getEnumFromString(selected);
                simulationApp.setTaskAllocatorSelected(taskAllEnum);
                simulationApp.resetSimulation();
            }
        });

        selectBox.sizeBy(260, 0);
        selectBox.setPosition(screenOffSet.x + PADDING_LEFT_SIDE, screenOffSet.y - 55);

        menuStage.addActor(selectBox);
    }

    public TaskAllocatorEnum getEnumFromString(String taskAllocator){
        for(TaskAllocatorEnum taskAllEnum : TaskAllocatorEnum.values()){
            if(taskAllEnum.getName().equals(taskAllocator)) return taskAllEnum;
        }
        throw new IllegalArgumentException("Could not match taskAllocator '" + taskAllocator + "' with any taskAllocator.");
    }

    public void changeOffSet(Vector2 offSet){
        this.screenOffSet = offSet;
        textAboveDropDown.changeOffSet(screenOffSet.x + PADDING_LEFT_SIDE, screenOffSet.y);
        selectBox.setPosition(screenOffSet.x + PADDING_LEFT_SIDE, screenOffSet.y - 55);
    }
}
