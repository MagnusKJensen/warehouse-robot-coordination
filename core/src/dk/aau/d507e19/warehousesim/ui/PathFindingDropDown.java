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
import com.badlogic.gdx.utils.Array;
import dk.aau.d507e19.warehousesim.SimulationApp;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.PathFinder;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.PathFinderEnum;

import java.util.ArrayList;

public class PathFindingDropDown {
    private SideMenu sideMenu;
    private Stage menuStage;
    private SimulationApp simulationApp;
    private Skin skin;
    private Vector2 screenOffSet;
    private Text textAboveDropDown;
    private final String TEXT_ABOVE_DROP_DOWN = "Select pathfinder for robots";
    private SelectBox<String> selectBox;
    private float LEFT_SIDE_PADDING = 10;

    public PathFindingDropDown(Stage menuStage, SimulationApp simulationApp, Vector2 screenOffSet, SideMenu sideMenu) {
        this.sideMenu = sideMenu;
        this.menuStage = menuStage;
        this.simulationApp = simulationApp;
        this.screenOffSet = screenOffSet;
        this.skin = new Skin(Gdx.files.internal("data/uiskin.json"));

        // Create text above the dropdown menu
        this.textAboveDropDown = new Text(TEXT_ABOVE_DROP_DOWN, screenOffSet.x + LEFT_SIDE_PADDING, screenOffSet.y, Color.CORAL);
        menuStage.addActor(textAboveDropDown);

        createDropDown();
    }

    private void createDropDown() {
        selectBox = new SelectBox<>(skin);


        Array<String> pfNames = new Array<>();
        for(PathFinderEnum pf : PathFinderEnum.values()){
            pfNames.add(pf.getName());
        }
        selectBox.setItems(pfNames);

        selectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String selected = selectBox.getSelected();
                PathFinderEnum selectedPathFinder = findSelectedPathFinderFromString(selected);
                simulationApp.setPathFinderSelected(selectedPathFinder);
                simulationApp.resetSimulation();
            }
        });

        selectBox.sizeBy(260, 0);
        selectBox.setPosition(screenOffSet.x + LEFT_SIDE_PADDING, screenOffSet.y - 55);

        menuStage.addActor(selectBox);
    }

    private PathFinderEnum findSelectedPathFinderFromString(String selected) {
        for(PathFinderEnum pf : PathFinderEnum.values()){
            if(selected.equals(pf.getName())) return pf;
        }
        throw new IllegalArgumentException("Could not match pathfinder '" + selected + "' with any pathfinder.");
    }

    public void changeOffSet(Vector2 offSet){
        this.screenOffSet = offSet;
        textAboveDropDown.changeOffSet(screenOffSet.x + LEFT_SIDE_PADDING, screenOffSet.y);
        selectBox.setPosition(screenOffSet.x + LEFT_SIDE_PADDING, screenOffSet.y - 55);
    }
}
