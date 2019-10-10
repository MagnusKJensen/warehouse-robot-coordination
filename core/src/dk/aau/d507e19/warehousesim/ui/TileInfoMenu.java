package dk.aau.d507e19.warehousesim.ui;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
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

    public TileInfoMenu(Stage menuStage, SimulationApp simulationApp, Vector2 screenOffSet, SideMenu sideMenu) {
        this.sideMenu = sideMenu;
        this.menuStage = menuStage;
        this.simulationApp = simulationApp;
        this.screenOffset = screenOffSet;
        batch = new SpriteBatch();
        font = GraphicsManager.getFont();
        this.text = new Text("Current tile selected: \n", screenOffset.x, screenOffset.y);
        menuStage.addActor(this.text);
    }

    public void changeText(String msg){
        text.setText("Current tile selected: \n" + msg);
    }
}
