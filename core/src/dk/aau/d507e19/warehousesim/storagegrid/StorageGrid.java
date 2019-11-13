package dk.aau.d507e19.warehousesim.storagegrid;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import dk.aau.d507e19.warehousesim.Simulation;
import dk.aau.d507e19.warehousesim.WarehouseSpecs;
import dk.aau.d507e19.warehousesim.controller.path.Step;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.rrt.Node;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.server.HeatMap;
import dk.aau.d507e19.warehousesim.controller.server.Reservation;
import dk.aau.d507e19.warehousesim.storagegrid.product.Product;

import java.util.ArrayList;

public class StorageGrid {

    private final Tile[][] tiles;
    public final int width, height;

    private ArrayList<GridCoordinate> pickerPoints = new ArrayList<>(),chargerPoints = new ArrayList<>(),maintenancePoints = new ArrayList<>();
    private Simulation simulation;
    private ArrayList<Product> allProducts = new ArrayList<>();

    public StorageGrid(int width, int height, Simulation simulation){
        this.height = height;
        this.width = width;
        this.tiles = new Tile[width][height];
        this.simulation = simulation;
        generatePickerPoints();
        generateChargerPoints();
        generateMaintenancePoints();
        fillGrid();
    }

    public ArrayList<BinTile> tilesWithProduct(Product prod){
        ArrayList<BinTile> tilesWithProducts = new ArrayList<>();
        for(int x = 0; x < width; ++x){
            for(int y = 0; y < height; ++y){
                if(tiles[x][y] instanceof BinTile){
                    BinTile tile = (BinTile) tiles[x][y];
                    if(tile.hasBin() && tile.getBin().hasProduct(prod)){
                        tilesWithProducts.add(tile);
                    }
                }
            }
        }

        return tilesWithProducts;
    }

    private void generatePickerPoints() {
        ArrayList<GridCoordinate> gridCoordinates;
        gridCoordinates = Simulation.getWarehouseSpecs().pickerPlacementPattern.generatePattern(Simulation.getWarehouseSpecs().numberOfPickers,"picker");
        pickerPoints.addAll(gridCoordinates);
    }
    private void generateChargerPoints() {
        ArrayList<GridCoordinate> gridCoordinates;
        gridCoordinates = Simulation.getWarehouseSpecs().chargerPlacementPattern.generatePattern(Simulation.getWarehouseSpecs().numberOfChargers,"charger");
        chargerPoints.addAll(gridCoordinates);
    }
    private void generateMaintenancePoints() {
        ArrayList<GridCoordinate> gridCoordinates;
        gridCoordinates = Simulation.getWarehouseSpecs().maintenancePlacementPattern.generatePattern(Simulation.getWarehouseSpecs().numberOfMaintenanceTiles,"maintenance");
        maintenancePoints.addAll(gridCoordinates);
    }

    private void arePickerPointsOutsideGrid(int[][] pickers) {
        for(int i = 0; i < pickers.length; ++i){
            if(pickers[i][0] > Simulation.getWarehouseSpecs().wareHouseWidth - 1 || pickers[i][1] > Simulation.getWarehouseSpecs().wareHouseHeight - 1)
                throw new IllegalArgumentException("Picker point is outside grid at (" + pickers[i][0] + "," + pickers[i][1] + "). " +
                        "Gridsize (" + Simulation.getWarehouseSpecs().wareHouseWidth + ", " + Simulation.getWarehouseSpecs().wareHouseHeight + ")" +
                        " counting from 0 to " + (Simulation.getWarehouseSpecs().wareHouseWidth - 1) + ".");
        }
    }

    private void fillGrid(){
        for(int y = 0;  y < height; y++){
            for(int x = 0; x < width; x++){
                if(isAPickerPoint(x,y)) tiles[x][y] = new PickerTile(x,y);
                else if(isAChargerPoint(x,y)) tiles[x][y] = new ChargingTile(x,y);
                else if(isAMaintenancePoint(x,y)) tiles[x][y] = new MaintenanceTile(x,y);
                else tiles[x][y] = new BinTile(x, y);
            }
        }
    }

    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch){
        GridBounds renderedBounds = simulation.getRenderedBounds();
        for(int y = renderedBounds.startY;  y <= renderedBounds.endY; y++){
            for(int x = renderedBounds.startX; x <= renderedBounds.endX; x++){
                tiles[x][y].render(shapeRenderer, batch);
            }
        }
    }


    public void renderPathOverlay(ArrayList<Reservation> reservations, ShapeRenderer shapeRenderer){
        GridBounds renderedBounds = simulation.getRenderedBounds();
        for(Reservation reservation : reservations){
            int x = reservation.getGridCoordinate().getX(), y = reservation.getGridCoordinate().getY();
            if(renderedBounds.isWithinBounds(reservation.getGridCoordinate())){
                if(reservation.getTimeFrame().isWithinTimeFrame(simulation.getTimeInTicks()))
                    tiles[x][y].renderOverlay(shapeRenderer, Tile.overlayColor2);
                else
                    tiles[x][y].renderOverlay(shapeRenderer);
            }
        }
    }


    public Tile getTile(int x, int y){
        return tiles[x][y];
    }

    public boolean isAPickerPoint(int x, int y){
        for (GridCoordinate picker: pickerPoints) {
            if(picker.getX() == x && picker.getY() == y){
                return true;
            }
        }
        return false;
    }
    public boolean isAChargerPoint(int x, int y){
        for (GridCoordinate charger: chargerPoints) {
            if(charger.getX() == x && charger.getY() == y){
                return true;
            }
        }
        return false;
    }
    public boolean isAMaintenancePoint(int x, int y){
        for (GridCoordinate maintenance: maintenancePoints) {
            if(maintenance.getX() == x && maintenance.getY() == y){
                return true;
            }
        }
        return false;
    }
    protected void setAllProducts(ArrayList<Product> prods){
        allProducts = prods;
    }

    public ArrayList<Product> getAllProducts() {
        return allProducts;
    }

    public ArrayList<GridCoordinate> getPickerPoints() {
        return pickerPoints;
    }

    public void renderTreeOverlay(ArrayList<Node<GridCoordinate>> listOfNodes, ShapeRenderer shapeRenderer, ArrayList<Step> path) {
        for(Node<GridCoordinate> n :listOfNodes){
            if(doesPathContainCoordinate(path,n.getData())){
                tiles[n.getData().getX()][n.getData().getY()].renderTreeNode(n,shapeRenderer, Color.GREEN);
            }else{
                tiles[n.getData().getX()][n.getData().getY()].renderTreeNode(n,shapeRenderer, Color.RED);
            }
        }
    }

    private boolean doesPathContainCoordinate(ArrayList<Step> path, GridCoordinate data) {
        for(Step s : path){
            if(s.getGridCoordinate().equals(data)){
                return true;
            }
        }
        return false;
    }

    public ArrayList<BinTile> getAllBinTiles(){
        ArrayList<BinTile> binTiles = new ArrayList<>();
        for(int x = 0; x < Simulation.getWarehouseSpecs().wareHouseHeight; ++x){
            for(int y = 0; y < Simulation.getWarehouseSpecs().wareHouseWidth; ++y){
                if(getTile(x,y) instanceof BinTile) binTiles.add((BinTile) getTile(x,y));
            }
        }
        return binTiles;
    }

    public void renderHeatMap(int[][] heatMap, ShapeRenderer shapeRenderer) {
        GridBounds renderedBounds = simulation.getRenderedBounds();
        for(int x = renderedBounds.startX; x <= renderedBounds.endX; x++){
            for(int y = renderedBounds.startY; y <= renderedBounds.endY; y++) {
                Color color = HeatMap.heatColor(heatMap[x][y]);
                tiles[x][y].renderOverlay(shapeRenderer, color);
            }
        }
    }



}
