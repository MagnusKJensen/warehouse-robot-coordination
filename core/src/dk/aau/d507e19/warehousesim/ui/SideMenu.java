package dk.aau.d507e19.warehousesim.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.Viewport;
import dk.aau.d507e19.warehousesim.GraphicsManager;
import dk.aau.d507e19.warehousesim.SimulationApp;

import java.util.ArrayList;

public class SideMenu {

    private static final String ICONS_PATH = "icons/";

    private ShapeRenderer shapeRenderer;
    private Stage menuStage;
    private SimulationApp simulationApp;

    private TimeControlMenu timeControlMenu;

    static final Color disabledButtonColor = new Color(140f / 255f, 140f / 255f, 140f / 255f, 1f);
    static final Color defaultButtonColor = new Color(245f / 255f, 245f / 255f, 245f / 255f, 1f);
    static final Color selectedButtonColor = new Color(60f / 255f, 175f / 255f, 75f / 255f, 1f);

    public final TextButtonStyle textButtonStyle;

    private Color menuBGColor = new Color(75f / 255f, 75f / 255f, 75f / 255f, 1);
    private final Vector2 timeControlOffset = new Vector2(70, 0);

    public SideMenu(Viewport menuViewport, final SimulationApp simApp) {
        textButtonStyle = new TextButtonStyle();
        textButtonStyle.font = GraphicsManager.getFont();

        this.simulationApp = simApp;
        shapeRenderer = new ShapeRenderer();
        menuStage = new Stage(menuViewport);
        simApp.getInputMultiplexer().addProcessor(menuStage);
        timeControlMenu = new TimeControlMenu(menuStage, simulationApp, timeControlOffset);
    }

    public void update() {
        menuStage.act();
    }

    public void render(OrthographicCamera camera) {
        renderBackground(camera);
        menuStage.draw();
    }

    private void renderBackground(OrthographicCamera camera){
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.setColor(menuBGColor);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(0, 0, camera.viewportWidth, camera.viewportHeight);
        shapeRenderer.end();
    }

}
