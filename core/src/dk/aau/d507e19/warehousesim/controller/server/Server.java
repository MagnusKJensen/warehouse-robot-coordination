package dk.aau.d507e19.warehousesim.controller.server;

import dk.aau.d507e19.warehousesim.Simulation;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.controller.robot.Status;
import dk.aau.d507e19.warehousesim.controller.server.order.OrderGenerator;
import dk.aau.d507e19.warehousesim.controller.server.order.OrderManager;
import dk.aau.d507e19.warehousesim.storagegrid.BinTile;
import dk.aau.d507e19.warehousesim.storagegrid.GridBounds;
import dk.aau.d507e19.warehousesim.storagegrid.PickerTile;
import dk.aau.d507e19.warehousesim.storagegrid.StorageGrid;
import dk.aau.d507e19.warehousesim.storagegrid.product.Product;
import dk.aau.d507e19.warehousesim.storagegrid.product.SKU;

import java.util.ArrayList;
import java.util.HashMap;

public class Server {
    private Simulation simulation;
    private ReservationManager reservationManager;
    private HashMap<SKU, ArrayList<BinTile>> productMap = new HashMap<>();
    private OrderManager orderManager;
    private OrderGenerator orderGenerator;
    private StorageGrid grid;

    private ArrayList<Product> productsAvailable = new ArrayList<>();

    private ArrayList<GridCoordinate> pickerPoints;

    public Server(Simulation simulation, StorageGrid grid) {
        this.simulation = simulation;
        this.grid = grid;
        this.reservationManager = new ReservationManager(simulation.getGridWidth(), simulation.getGridHeight(), this);
        this.orderManager = new OrderManager(this);
        this.orderGenerator = new OrderGenerator(orderManager, this);
        this.productsAvailable = grid.getAllProducts();

        pickerPoints = grid.getPickerPoints();

        generateProductMap(grid);
    }

    public Server(Simulation simulation){
        this.simulation = simulation;
        this.reservationManager = new ReservationManager(simulation.getGridWidth(), simulation.getGridHeight(), this);
        this.orderManager = new OrderManager(this);
    }

    public ArrayList<BinTile> getTilesContaining (SKU sku){
        return productMap.get(sku);
    }

    public ArrayList<Robot> getAllRobots(){
        return simulation.getAllRobots();
    }

    public int getGridHeight() {
        return simulation.getGridHeight();
    }

    public int getGridWidth() {
        return simulation.getGridWidth();
    }

    public long getTimeInMS() {
        return simulation.getSimulatedTimeInMS();
    }

    public long getTimeInTicks(){
        return simulation.getTimeInTicks();
    }

    public ReservationManager getReservationManager(){
        return reservationManager;
    }

    private void generateProductMap(StorageGrid grid) {
        for(int x = 0; x < simulation.getGridWidth(); ++x){
            for(int y = 0; y < simulation.getGridHeight(); ++y){
                if(grid.getTile(x,y) instanceof BinTile){
                    BinTile tile = (BinTile)grid.getTile(x,y);
                    for(Product prod : tile.getBin().getProducts()){
                        ArrayList<BinTile> current;
                        if(productMap.get(prod.getSKU()) == null) current = new ArrayList<>();
                        else current = productMap.get(prod.getSKU());
                        if(!current.contains(tile)) {
                            current.add(tile);
                            productMap.put(prod.getSKU(), current);
                        }
                    }
                }
            }
        }
    }

    public ArrayList<Product> getProductsAvailable() {
        return productsAvailable;
    }

    public void update(){
        orderGenerator.update();
        orderManager.update();
        reservationManager.removeOutdatedReservations();
    }

    public ArrayList<GridCoordinate> getPickerCoordinates() {
        return pickerPoints;
    }

    public ArrayList<PickerTile> getPickerTiles() {
        return grid.getPickerTiles();
    }

    public Simulation getSimulation() {
        return simulation;
    }

    public OrderManager getOrderManager() {
        return orderManager;
    }

    public boolean hasAvailableRobot(){
        for (Robot robot : simulation.getAllRobots()){
            if(robot.getCurrentStatus() == Status.AVAILABLE) return true;
        }

        return false;
    }

    public ArrayList<Robot> getAvailableRobots(){
        ArrayList<Robot> availableRobots = new ArrayList<>();
        for (Robot robot : simulation.getAllRobots()){
            if(robot.getCurrentStatus() == Status.AVAILABLE) availableRobots.add(robot);
        }

        return availableRobots;
    }

    public GridBounds getGridBounds() {
        return new GridBounds(getGridWidth() - 1, getGridHeight() - 1);
    }

    public ArrayList<PickerTile> getAvailablePickers() {
        ArrayList<PickerTile> availablePickers = new ArrayList<>();
        for(GridCoordinate picker : pickerPoints){
            PickerTile tile = (PickerTile) simulation.getStorageGrid().getTile(picker.getX(), picker.getY());
            if(!tile.hasOrder()){
                availablePickers.add(tile);
            }
        }

        return availablePickers;
    }

    public int getProductSKUsRemaining() {
        ArrayList<SKU> SKUs = new ArrayList<>();
        for (Product prod : productsAvailable) {
            if(!SKUs.contains(prod.getSKU())) SKUs.add(prod.getSKU());
        }

        return SKUs.size();
    }

    public int getAmountLeftOfProduct(Product prod) {
        int amountLeft = 0;
        for (Product product : productsAvailable){
            if(prod.equals(product)) amountLeft++;
        }

        return amountLeft;
    }

    public GridCoordinate getOptimalIdleRobotPosition() {
        GridCoordinate optimalCoord = HeatMap.getLeastCrowdedCoordinate(this);
        return optimalCoord;
    }

    public int[][] getHeatMap(){
        return HeatMap.getHeatMap(this);
    }


    public Robot getHighestPriority(Robot r1, Robot r2) {
        if(r1.getRobotController().hasOrderAssigned()){
            if(!r2.getRobotController().hasOrderAssigned())
                return r1;

            if(r1.getRobotController().getTicksSinceOrderAssigned() >= r2.getRobotController().getTicksSinceOrderAssigned())
                return r1;
            else
                return r2;
        }
        return r1;
    }
}
