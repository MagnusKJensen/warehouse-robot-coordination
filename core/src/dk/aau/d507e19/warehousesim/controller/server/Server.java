package dk.aau.d507e19.warehousesim.controller.server;

import dk.aau.d507e19.warehousesim.Simulation;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.robot.Robot;
import dk.aau.d507e19.warehousesim.storagegrid.BinTile;
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

    private ArrayList<Product> productsAvailable = new ArrayList<>();

    private ArrayList<GridCoordinate> pickerPoints;

    public Server(Simulation simulation, StorageGrid grid) {
        this.simulation = simulation;
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

    public long getTimeInSeconds() {
        return simulation.getSimulatedTimeInMS();
    }

    public long getTimeInTicks(){
        return simulation.getTimeInTicks();
    }

    public ReservationManager getReservationManager(){
        return reservationManager;
    }

    public OrderGenerator getOrderGenerator() {
        return orderGenerator;
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
    }

    public ArrayList<GridCoordinate> getPickerPoints() {
        return pickerPoints;
    }

    public Simulation getSimulation() {
        return simulation;
    }

    public OrderManager getOrderManager() {
        return orderManager;
    }
}
