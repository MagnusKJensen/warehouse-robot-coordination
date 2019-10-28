package dk.aau.d507e19.warehousesim.storagegrid;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import dk.aau.d507e19.warehousesim.Simulation;
import dk.aau.d507e19.warehousesim.WarehouseSpecs;
import dk.aau.d507e19.warehousesim.controller.path.Step;
import dk.aau.d507e19.warehousesim.controller.pathAlgorithms.rrt.Node;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.controller.server.Reservation;
import dk.aau.d507e19.warehousesim.storagegrid.product.Product;

import java.util.ArrayList;

public class StorageGrid {

    private final Tile[][] tiles;
    private final int width, height;

    private ShapeRenderer shapeRenderer;
    private SpriteBatch spriteBatch;

    private ArrayList<GridCoordinate> pickerPoints = new ArrayList<>();
    private Simulation simulation;
    private ArrayList<Product> allProducts = new ArrayList<>();

    public StorageGrid(int width, int height, Simulation simulation){
        this.height = height;
        this.width = width;
        this.tiles = new Tile[width][height];
        this.shapeRenderer = new ShapeRenderer();
        this.spriteBatch = new SpriteBatch();
        this.simulation = simulation;
        generatePickerPoints();
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
        int[][] pickers = WarehouseSpecs.pickerPoints;

        // Check to see if all picker points are inside the grid
        arePickerPointsOutsideGrid(pickers);

        // Go through all picker points and add them, if one is not already present at a given tile.
        for(int i = 0; i < pickers.length; ++i){
            GridCoordinate cord = new GridCoordinate(pickers[i][0], pickers[i][1]);
            if(pickerPoints.contains(cord))
                throw new RuntimeException("Picker point already present at (" + cord.getX() + "," + cord.getY() +
                        "). Cannot have two picker points at the same tile");
            else pickerPoints.add(new GridCoordinate(pickers[i][0], pickers[i][1]));
        }
    }

    private void arePickerPointsOutsideGrid(int[][] pickers) {
        for(int i = 0; i < pickers.length; ++i){
            if(pickers[i][0] > WarehouseSpecs.wareHouseWidth - 1 || pickers[i][1] > WarehouseSpecs.wareHouseHeight - 1)
                throw new IllegalArgumentException("Picker point is outside grid at (" + pickers[i][0] + "," + pickers[i][1] + "). " +
                        "Gridsize (" + WarehouseSpecs.wareHouseWidth + ", " + WarehouseSpecs.wareHouseHeight + ")" +
                        " counting from 0 to " + (WarehouseSpecs.wareHouseWidth - 1) + ".");
        }
    }

    private void fillGrid(){
        for(int y = 0;  y < height; y++){
            for(int x = 0; x < width; x++){
                if(isAPickerPoint(x,y)) tiles[x][y] = new PickerTile(x,y);
                else tiles[x][y] = new BinTile(x, y);
            }
        }
    }

    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch){
        // TODO: 30/09/2019 Adapt so that it only renders tiles within view
        for(int y = 0;  y < height; y++){
            for(int x = 0; x < width; x++){
                tiles[x][y].render(shapeRenderer, batch);
            }
        }
    }


    public void renderPathOverlay(ArrayList<Reservation> reservations, ShapeRenderer shapeRenderer){
        for(Reservation reservation : reservations){
            int x = reservation.getGridCoordinate().getX(), y = reservation.getGridCoordinate().getY();
            if(reservation.getTimeFrame().isWithinTimeFrame(simulation.getTimeInTicks()))
                tiles[x][y].renderOverlay(shapeRenderer, Tile.overlayColor2);
            else
                tiles[x][y].renderOverlay(shapeRenderer);
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
}
