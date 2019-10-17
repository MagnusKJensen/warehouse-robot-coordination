package dk.aau.d507e19.warehousesim.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import dk.aau.d507e19.warehousesim.SimulationApp;

public class TaskAllocationDropDown {
    private SideMenu sideMenu;
    private Stage menuStage;
    private SimulationApp simulationApp;
    private Skin skin;
    private Vector2 screenOffSet;
    private Text textAboveDropDown;
    private final String TEXT_ABOVE_DROP_DOWN = "Select task allocating algorithm";

    public TaskAllocationDropDown(Stage menuStage, SimulationApp simulationApp, Vector2 screenOffSet, SideMenu sideMenu) {
        this.sideMenu = sideMenu;
        this.menuStage = menuStage;
        this.simulationApp = simulationApp;
        this.screenOffSet = screenOffSet;
        this.skin = new Skin(Gdx.files.internal("data/uiskin.json"));

        // Create text above the dropdown menu
        this.textAboveDropDown = new Text(TEXT_ABOVE_DROP_DOWN, screenOffSet.x, screenOffSet.y, Color.CORAL);
        menuStage.addActor(textAboveDropDown);

        createDropDown();
    }

    private void createDropDown() {
        final SelectBox<String> selectBox = new SelectBox<>(skin);
        selectBox.setItems("Smart Task Allocator", "Dumb Task Allocator");

        selectBox.sizeBy(260, 0);
        selectBox.setPosition(screenOffSet.x, screenOffSet.y - 55);

        menuStage.addActor(selectBox);
    }


}
