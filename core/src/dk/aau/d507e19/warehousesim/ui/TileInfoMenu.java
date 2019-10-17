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

    private final int MENU_HEIGHT = 900;
    private final int SCROLL_PANE_HEIGHT = 200;
    private final int TEXT_OFFSET = 30;

    public TileInfoMenu(Stage menuStage, SimulationApp simulationApp, Vector2 screenOffSet, SideMenu sideMenu) {
        this.sideMenu = sideMenu;
        this.menuStage = menuStage;
        this.simulationApp = simulationApp;

        // Text above bin content scroll pane
        this.binContentText = new Text(BIN_CONTENT_PRECURSOR, screenOffSet.x, screenOffSet.y, Color.CORAL);
        menuStage.addActor(binContentText);

        // Text above robot bin content scroll pane
        this.robotBinContentText = new Text(ROBOT_BIN_CONTENT_PRECURSOR, screenOffSet.x, screenOffSet.y - SCROLL_PANE_HEIGHT - TEXT_OFFSET, Color.CORAL);
        menuStage.addActor(robotBinContentText);

        // Add bin content scroll menu to sidebar
        binContent = addScrollMenu(SimulationApp.MENU_WIDTH_IN_PIXELS,SCROLL_PANE_HEIGHT,0,MENU_HEIGHT - SCROLL_PANE_HEIGHT - TEXT_OFFSET);
        // Add robot bin content scroll menu to sidebar
        robotBinContent = addScrollMenu(SimulationApp.MENU_WIDTH_IN_PIXELS,SCROLL_PANE_HEIGHT,0,MENU_HEIGHT - 2 * SCROLL_PANE_HEIGHT - 2 * TEXT_OFFSET);
    }

    private Table addScrollMenu(int width, int height, int posX, int posY) {
        this.skin = new Skin(Gdx.files.internal("data/uiskin.json"));

        // Create root table
        final Table root = new Table();
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

    public void updateBinContent(ArrayList<Product> products){
        binContentText.setText(BIN_CONTENT_PRECURSOR);
        binContent.clear();
        for (Product prod : products) {
            Label label = new Label(prod.toString(), this.skin);
            binContent.add(label).left();
            binContent.row();
        }
    }

    public void updateRobotBinContent(ArrayList<Product> products, int robotID){
        robotBinContentText.setText(ROBOT_BIN_CONTENT_PRECURSOR + "for robot ID: " + robotID);
        robotBinContent.clear();
        for (Product prod : products) {
            robotBinContent.add(new Label(prod.toString(), this.skin)).left();
            robotBinContent.row();
        }
    }

    public void updateRobotBinContent(ArrayList<Product> products){
        robotBinContentText.setText(ROBOT_BIN_CONTENT_PRECURSOR);
        robotBinContent.clear();
        for (Product prod : products) {
            robotBinContent.add(new Label(prod.toString(), this.skin)).left();
            robotBinContent.row();
        }
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
}
