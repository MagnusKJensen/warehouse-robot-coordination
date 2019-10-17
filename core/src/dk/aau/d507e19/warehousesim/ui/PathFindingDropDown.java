package dk.aau.d507e19.warehousesim.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import dk.aau.d507e19.warehousesim.SimulationApp;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.PathFinder;

public class PathFindingDropDown {
    private SideMenu sideMenu;
    private Stage menuStage;
    private SimulationApp simulationApp;
    private Skin skin;
    private Vector2 screenOffSet;
    private Text textAboveDropDown;
    private final String TEXT_ABOVE_DROP_DOWN = "Select pathfinder for robots";

    public PathFindingDropDown(Stage menuStage, SimulationApp simulationApp, Vector2 screenOffSet, SideMenu sideMenu) {
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

        selectBox.setItems("Astar","DummyPathFinder", "RRT", "RRT*");

        selectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String selected = selectBox.getSelected();
                simulationApp.resetSimulation();
                simulationApp.getSimulation().setPathFinderSelected(selected);
            }
        });

        selectBox.sizeBy(260, 0);
        selectBox.setPosition(screenOffSet.x, screenOffSet.y - 55);

        menuStage.addActor(selectBox);
    }
}
