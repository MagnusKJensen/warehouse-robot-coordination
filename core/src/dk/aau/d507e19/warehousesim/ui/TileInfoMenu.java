package dk.aau.d507e19.warehousesim.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import dk.aau.d507e19.warehousesim.GraphicsManager;
import dk.aau.d507e19.warehousesim.SimulationApp;

public class TileInfoMenu {
    private SideMenu sideMenu;
    private Stage menuStage;
    private SimulationApp simulationApp;
    private Vector2 screenOffset;
    private Button pauseBtn;
    private SpriteBatch batch;
    private BitmapFont font;
    private Text text;
    private final String PRECURSOR = "Current tile selected: \n";

    public TileInfoMenu(Stage menuStage, SimulationApp simulationApp, Vector2 screenOffSet, SideMenu sideMenu) {
        this.sideMenu = sideMenu;
        this.menuStage = menuStage;
        this.simulationApp = simulationApp;
        this.screenOffset = screenOffSet;
        batch = new SpriteBatch();
        font = GraphicsManager.getFont();
        font.setColor(Color.WHITE);
        this.text = new Text(PRECURSOR, screenOffset.x, screenOffset.y);
        menuStage.addActor(this.text);
        // addScrollPane();
    }

    private void addScrollPane() {
        Skin skin = new Skin(Gdx.files.internal("data/uiskin.json"));

        final Table scrollTable = new Table();
        Label text1 = new Label("text", skin);
        scrollTable.row();

        ScrollPane scroller = new ScrollPane(scrollTable);

        scrollTable.add(text1);

        Table table = new Table();
        table.setFillParent(true);
        table.add(scroller).fill().expand();
        menuStage.addActor(table);
    }

    public void changeText(String msg){
        text.setText(PRECURSOR + msg);
    }
}
