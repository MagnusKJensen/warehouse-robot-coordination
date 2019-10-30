package dk.aau.d507e19.warehousesim.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
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

    private Skin skin = new Skin(Gdx.files.internal("data/uiskin.json"));

    // Positions of all items in side menu
    private Vector2 timeControlOffset;
    private Vector2 tileInfoScrollPaneOffset;
    private Vector2 pathFindingDropDownOffset;
    private Vector2 taskAllocationDropDownOffset;
    private Vector2 performanceMetricsOffset;
    private Vector2 tickStopperOffset;

    // Performance Metrics
    private Text performanceMetricsTitle;
    private Text ordersProcessed;
    private Text tasksInQueue;
    private Text ordersPerMinute;
    private Text productsLeftInGrid;
    private Text goalReachedText;
    private Text ordersInQueue;
    private Color performanceMetricColor = Color.WHITE;

    private long msSinceStart;
    private double ordersPerMinuteCount;

    // TickStopper
    private TextField tickStopperTextField;
    private TextButton tickStopperButton;
    private Text tickStopperText;

    public SideMenu(Viewport menuViewport, final SimulationApp simApp) {
        updateOffSetsToWindowSize();
        textButtonStyle = new TextButtonStyle();
        textButtonStyle.font = GraphicsManager.getFont();

        this.simulationApp = simApp;
        shapeRenderer = new ShapeRenderer();
        menuStage = new Stage(menuViewport);
        simApp.getInputMultiplexer().addProcessor(menuStage);
        timeControlMenu = new TimeControlMenu(menuStage, simulationApp, timeControlOffset, this);
        binContentScrollPanes = new TileInfoMenu(menuStage, simulationApp, tileInfoScrollPaneOffset, this);
        pathFindingDropDown = new PathFindingDropDown(menuStage, simulationApp, pathFindingDropDownOffset, this);
        taskAllocationDropDown = new TaskAllocationDropDown(menuStage, simulationApp, taskAllocationDropDownOffset, this);
        addTickStopper();
        addPerformanceMetrics();

    }

    private void addTickStopper() {
        tickStopperTextField = new TextField("", skin);
        tickStopperTextField.setMessageText("Stop simulation at tick....");
        tickStopperTextField.setSize(180,40);

        tickStopperButton = new TextButton("Activate", skin);
        tickStopperButton.setSize(90, 40);
        tickStopperButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String input = tickStopperTextField.getText();
                try{
                    long tickStopGoal = Long.parseLong(input);
                    simulationApp.getSimulation().setTickStopperGoal(tickStopGoal);
                } catch (NumberFormatException e){
                    tickStopperTextField.setText("");
                }
            }
        });

        tickStopperText = new Text("TickStopper 2000", tickStopperOffset.x, tickStopperOffset.y, Color.CORAL);

        menuStage.addActor(tickStopperText);
        menuStage.addActor(tickStopperButton);
        menuStage.addActor(tickStopperTextField);
    }

    private void addPerformanceMetrics() {
        this.performanceMetricsTitle = new Text("Performance Metrics", performanceMetricsOffset.x, performanceMetricsOffset.y, Color.CORAL);
        this.productsLeftInGrid = new Text ("Products left: ", performanceMetricsOffset.x, performanceMetricsOffset.y - 25, performanceMetricColor);
        this.ordersInQueue = new Text("Orders in queue: ", performanceMetricsOffset.x, performanceMetricsOffset.y - 50, performanceMetricColor);
        this.ordersProcessed = new Text("Orders Processed: ", performanceMetricsOffset.x, performanceMetricsOffset.y - 75, performanceMetricColor);
        this.ordersPerMinute = new Text("Orders / minute: ", performanceMetricsOffset.x, performanceMetricsOffset.y - 100, performanceMetricColor);
        this.tasksInQueue = new Text("Tasks processing: ", performanceMetricsOffset.x, performanceMetricsOffset.y - 125, performanceMetricColor);
        this.goalReachedText = new Text("Goal not yet finished", performanceMetricsOffset.x, performanceMetricsOffset.y - 150, performanceMetricColor);
        menuStage.addActor(performanceMetricsTitle);
        menuStage.addActor(productsLeftInGrid);
        menuStage.addActor(ordersInQueue);
        menuStage.addActor(ordersProcessed);
        menuStage.addActor(ordersPerMinute);
        menuStage.addActor(tasksInQueue);
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
        tasksInQueue.setText("Tasks in queue: " + simulationApp.getSimulation().getServer().getOrderManager().tasksInQueue());
        updateOrdersPerMinute();
        updateOrdersProcessed();
    }

    private void updateOrdersProcessed() {
        String str = "Orders finished: " + simulationApp.getSimulation().getServer().getOrderManager().ordersFinished();
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

    public void resize(){
        updateOffSetsToWindowSize();
        // Performance metrics
        performanceMetricsTitle.changeOffSet(performanceMetricsOffset.x, performanceMetricsOffset.y);
        productsLeftInGrid.changeOffSet(performanceMetricsOffset.x, performanceMetricsOffset.y - 25);
        ordersInQueue.changeOffSet(performanceMetricsOffset.x, performanceMetricsOffset.y - 50);
        ordersProcessed.changeOffSet(performanceMetricsOffset.x, performanceMetricsOffset.y - 75);
        ordersPerMinute.changeOffSet(performanceMetricsOffset.x, performanceMetricsOffset.y - 100);
        tasksInQueue.changeOffSet(performanceMetricsOffset.x, performanceMetricsOffset.y - 125);
        goalReachedText.changeOffSet(performanceMetricsOffset.x, performanceMetricsOffset.y - 150);

        // Menus
        pathFindingDropDown.changeOffSet(pathFindingDropDownOffset);
        timeControlMenu.changeOffset(timeControlOffset);
        binContentScrollPanes.changeOffset(tileInfoScrollPaneOffset);
        taskAllocationDropDown.changeOffSet(taskAllocationDropDownOffset);

        // TickStopper
        tickStopperTextField.setPosition(tickStopperOffset.x, tickStopperOffset.y - 60);
        tickStopperButton.setPosition(tickStopperOffset.x + 190, tickStopperOffset.y - 60);
        tickStopperText.changeOffSet(tickStopperOffset.x, tickStopperOffset.y);
    }

    private void updateOffSetsToWindowSize() {
        timeControlOffset = new Vector2(0, 0);
        tileInfoScrollPaneOffset = new Vector2(0, Gdx.graphics.getHeight());
        pathFindingDropDownOffset = new Vector2(0, Gdx.graphics.getHeight() - 370);
        taskAllocationDropDownOffset = new Vector2(0, Gdx.graphics.getHeight() - 440);
        performanceMetricsOffset = new Vector2(10, Gdx.graphics.getHeight() - 510);
        tickStopperOffset = new Vector2(10, Gdx.graphics.getHeight() - 710);
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

    public void updatePlayPauseButtons(){
        timeControlMenu.updatePauseButton();
    }
}
