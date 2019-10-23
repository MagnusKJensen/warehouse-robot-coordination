package dk.aau.d507e19.warehousesim.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import dk.aau.d507e19.warehousesim.SimulationApp;
import dk.aau.d507e19.warehousesim.storagegrid.product.Product;

import java.util.ArrayList;

public class TileInfoMenu {
    private SideMenu sideMenu;
    private Stage menuStage;
    private SimulationApp simulationApp;
    private Table binContent;
    private Table robotBinContent;
    private Skin skin;
    private Text binContentText;
    private final String BIN_CONTENT_PRECURSOR = "Bin content ";
    private Text robotBinContentText;
    private final String ROBOT_BIN_CONTENT_PRECURSOR = "robot bin content ";
    private Vector2 screenOffSet;
    private Table binContentRoot = new Table();
    private Table robotBinContentRoot = new Table();

    private final int SCROLL_PANE_HEIGHT = 150;
    private final int TEXT_OFFSET = 30;

    public TileInfoMenu(Stage menuStage, SimulationApp simulationApp, Vector2 screenOffSet, SideMenu sideMenu) {
        this.sideMenu = sideMenu;
        this.menuStage = menuStage;
        this.simulationApp = simulationApp;
        this.screenOffSet = screenOffSet;

        // Text above bin content scroll pane
        this.binContentText = new Text(BIN_CONTENT_PRECURSOR, screenOffSet.x + 10, screenOffSet.y - 10, Color.CORAL);
        menuStage.addActor(binContentText);

        // Text above robot bin content scroll pane
        this.robotBinContentText = new Text(ROBOT_BIN_CONTENT_PRECURSOR, screenOffSet.x + 10, screenOffSet.y - SCROLL_PANE_HEIGHT - TEXT_OFFSET - 10, Color.CORAL);
        menuStage.addActor(robotBinContentText);

        // Add bin content scroll menu to sidebar
        binContent = addScrollMenu(SimulationApp.MENU_WIDTH_IN_PIXELS,SCROLL_PANE_HEIGHT,0,(int)screenOffSet.y - SCROLL_PANE_HEIGHT - TEXT_OFFSET, binContentRoot);
        // Add robot bin content scroll menu to sidebar
        robotBinContent = addScrollMenu(SimulationApp.MENU_WIDTH_IN_PIXELS,SCROLL_PANE_HEIGHT,0,(int)screenOffSet.y - 2 * SCROLL_PANE_HEIGHT - 2 * TEXT_OFFSET, robotBinContentRoot);
    }

    private Table addScrollMenu(int width, int height, int posX, int posY, Table root) {
        this.skin = new Skin(Gdx.files.internal("data/uiskin.json"));

        // Create root table
        root.setSize(width,height);
        root.setPosition(posX,posY);
        menuStage.addActor(root);

        // Create table of labels
        Table labels = new Table();
        // Set alignment to top and left
        labels.top();
        labels.align(Align.topLeft);
        labels.setSize(width,height);

        // Create the scroll pane and add it to root
        ScrollPane scrollPane = new ScrollPane(labels,skin);
        root.add(scrollPane).expand().fill();

        // Add scroll on hover and lose scroll on hover when exiting the area.
        scrollPane.addListener(getScrollOnHover(scrollPane));
        scrollPane.addListener(getLoseScrollOnHoverExit());

        return labels;
    }

    public void updateBinContent(ArrayList<Product> products, int x, int y){
        binContentText.setText(BIN_CONTENT_PRECURSOR + "for tile (" + x + "," + y + ")");
        binContent.clear();
        for (Product prod : products) {
            Label label = new Label(prod.toString(), this.skin);
            binContent.add(label).left();
            binContent.row();
        }
    }

    public void resetScrollPaneContent(){
        resetBinContent();
        resetRobotBinContent();
    }

    private void resetBinContent(){
        binContentText.setText(BIN_CONTENT_PRECURSOR);
        binContent.clear();
    }

    public void updateRobotBinContent(ArrayList<Product> products, int robotID){
        robotBinContentText.setText(ROBOT_BIN_CONTENT_PRECURSOR + "for robot ID: " + robotID);
        robotBinContent.clear();
        for (Product prod : products) {
            robotBinContent.add(new Label(prod.toString(), this.skin)).left();
            robotBinContent.row();
        }
    }

    private void resetRobotBinContent(){
        robotBinContentText.setText(ROBOT_BIN_CONTENT_PRECURSOR);
        robotBinContent.clear();
    }

    private EventListener getScrollOnHover(final Actor scrollPane) {
        return new EventListener() {
            @Override
            public boolean handle(Event event) {
                if(event instanceof InputEvent)
                    if(((InputEvent)event).getType() == InputEvent.Type.enter)
                        event.getStage().setScrollFocus(scrollPane);
                return false;
            }
        };
    }

    private EventListener getLoseScrollOnHoverExit() {
        return new EventListener() {
            @Override
            public boolean handle(Event event) {
                if(event instanceof InputEvent)
                    if(((InputEvent)event).getType() == InputEvent.Type.exit)
                        event.getStage().setScrollFocus(null);
                return false;
            }
        };
    }

    public void changeOffset(Vector2 offSet){
        this.screenOffSet = offSet;
        binContentRoot.setPosition(screenOffSet.x, screenOffSet.y - SCROLL_PANE_HEIGHT - TEXT_OFFSET);
        robotBinContentRoot.setPosition(screenOffSet.x, screenOffSet.y - 2 * SCROLL_PANE_HEIGHT - 2 * TEXT_OFFSET);
        binContentText.changeOffSet(screenOffSet.x + 10, screenOffSet.y - 10);
        robotBinContentText.changeOffSet(screenOffSet.x + 10, screenOffSet.y - SCROLL_PANE_HEIGHT - TEXT_OFFSET - 10);
    }
}
