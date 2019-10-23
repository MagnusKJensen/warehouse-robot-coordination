package dk.aau.d507e19.warehousesim.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.utils.viewport.Viewport;
import dk.aau.d507e19.warehousesim.GraphicsManager;
import dk.aau.d507e19.warehousesim.SimulationApp;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class SideMenu {
    private ShapeRenderer shapeRenderer;
    private Stage menuStage;
    private SimulationApp simulationApp;

    private TimeControlMenu timeControlMenu;
    private TileInfoMenu binContentScrollPanes;
    private PathFindingDropDown pathFindingDropDown;
    private TaskAllocationDropDown taskAllocationDropDown;

    static final Color disabledButtonColor = new Color(140f / 255f, 140f / 255f, 140f / 255f, 1f);
    static final Color defaultButtonColor = new Color(245f / 255f, 245f / 255f, 245f / 255f, 1f);
    static final Color selectedButtonColor = new Color(60f / 255f, 175f / 255f, 75f / 255f, 1f);

    public final TextButtonStyle textButtonStyle;

    private Color menuBGColor = new Color(75f / 255f, 75f / 255f, 75f / 255f, 1);
    private final Vector2 timeControlOffset = new Vector2(10, 0);
    private final Vector2 tileMenuOffset = new Vector2(10, 890);
    private final Vector2 pathFindingDropDownOffset = new Vector2(10, 430);
    private final Vector2 taskAllocationDropDownOffset = new Vector2(10, 360);
    private final Vector2 performanceMetricsOffset = new Vector2(10, 290);

    private Text ordersProcessed;
    private Text ordersPerMinute;
    private Text productsLeftInGrid;
    private Text goalReachedText;
    private Text ordersInQueue;
    private Color performanceMetricColor = Color.WHITE;

    private long msSinceStart;
    private double ordersPerMinuteCount;

    public SideMenu(Viewport menuViewport, final SimulationApp simApp) {
        textButtonStyle = new TextButtonStyle();
        textButtonStyle.font = GraphicsManager.getFont();

        this.simulationApp = simApp;
        shapeRenderer = new ShapeRenderer();
        menuStage = new Stage(menuViewport);
        simApp.getInputMultiplexer().addProcessor(menuStage);
        timeControlMenu = new TimeControlMenu(menuStage, simulationApp, timeControlOffset, this);
        binContentScrollPanes = new TileInfoMenu(menuStage, simulationApp, tileMenuOffset, this);
        pathFindingDropDown = new PathFindingDropDown(menuStage, simulationApp, pathFindingDropDownOffset, this);
        taskAllocationDropDown = new TaskAllocationDropDown(menuStage, simulationApp, taskAllocationDropDownOffset, this);
        addPerformanceMetrics();
    }

    private void addPerformanceMetrics() {
        this.productsLeftInGrid = new Text ("Products left: ", performanceMetricsOffset.x, performanceMetricsOffset.y, performanceMetricColor);
        this.ordersInQueue = new Text("Orders in queue: ", performanceMetricsOffset.x, performanceMetricsOffset.y - 25, performanceMetricColor);
        this.ordersProcessed = new Text("Orders Processed: ", performanceMetricsOffset.x, performanceMetricsOffset.y - 50, performanceMetricColor);
        this.ordersPerMinute = new Text("Orders / minute: ", performanceMetricsOffset.x, performanceMetricsOffset.y - 75, performanceMetricColor);
        this.goalReachedText = new Text("Goal not yet finished", performanceMetricsOffset.x, performanceMetricsOffset.y - 100, performanceMetricColor);
        menuStage.addActor(productsLeftInGrid);
        menuStage.addActor(ordersInQueue);
        menuStage.addActor(ordersProcessed);
        menuStage.addActor(ordersPerMinute);
        menuStage.addActor(goalReachedText);
    }

    public void update() {
        updatePerformanceMetrics();
        menuStage.act();
    }

    private void updatePerformanceMetrics(){
        productsLeftInGrid.setText("Available products left: " + simulationApp.getSimulation().getServer().getProductsAvailable().size());
        goalReachedText.setText(simulationApp.getSimulation().getGoal().toString());
        ordersInQueue.setText("Orders in queue: " + simulationApp.getSimulation().getServer().getOrderManager().ordersInQueue());
        updateOrdersPerMinute();
        updateOrdersProcessed();
    }

    private void updateOrdersProcessed() {
        String str = "Orders Processed: " + simulationApp.getSimulation().getOrdersProcessed();
        ordersProcessed.setText(str);
    }

    private void updateOrdersPerMinute() {
        this.msSinceStart = simulationApp.getSimulation().getSimulatedTimeInMS();

        if(simulationApp.getSimulation().getOrdersProcessed() == 0) ordersPerMinute.setText("Orders / minute: 0");
        else {
            this.ordersPerMinuteCount =  simulationApp.getSimulation().getOrdersProcessed() / ((double) msSinceStart / 1000 / 60);
            DecimalFormat df = new DecimalFormat("0.000");
            df.setRoundingMode(RoundingMode.HALF_UP);
            String orderPerSecondString = "Orders / minute: " + df.format(ordersPerMinuteCount);
            ordersPerMinute.setText(orderPerSecondString);
        }
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

    public void resetSideMenu(){
        binContentScrollPanes.resetScrollPaneContent();
        timeControlMenu.resetTimeControlButtons();

    }

    public TileInfoMenu getBinContentScrollPanes() {
        return binContentScrollPanes;
    }
}
