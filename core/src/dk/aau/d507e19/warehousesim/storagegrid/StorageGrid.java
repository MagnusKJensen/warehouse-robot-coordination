package dk.aau.d507e19.warehousesim.storagegrid;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import dk.aau.d507e19.warehousesim.GraphicsManager;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;
import dk.aau.d507e19.warehousesim.storagegrid.product.Bin;
import dk.aau.d507e19.warehousesim.storagegrid.product.Product;
import dk.aau.d507e19.warehousesim.storagegrid.product.SKU;

import java.util.ArrayList;

public class StorageGrid {

    private final Tile[][] tiles;
    private final int width, height;

    private ShapeRenderer shapeRenderer;
    private SpriteBatch spriteBatch;
    private ArrayList<GridCoordinate> pickerPoints;

    public StorageGrid(int width, int height){
        this.height = height;
        this.width = width;
        this.tiles = new Tile[width][height];
        this.shapeRenderer = new ShapeRenderer();
        this.spriteBatch = new SpriteBatch();
        fillGrid();
    }

    public StorageGrid(int width, int height, ArrayList<GridCoordinate> pickerPoints){
        this.height = height;
        this.width = width;
        this.tiles = new Tile[width][height];
        this.shapeRenderer = new ShapeRenderer();
        this.spriteBatch = new SpriteBatch();
        this.pickerPoints = pickerPoints;
        fillGrid();
    }

    private void fillGrid(){
        for(int y = 0;  y < height; y++){
            for(int x = 0; x < width; x++){
                //if(x == 0 && y == 0) tiles[x][y] = new PickerTile(x,y);
                if(isAPickerPoint(x,y)) tiles[x][y] = new PickerTile(x,y);
                else if(x == 1 && y == 1){
                    Bin bin = new Bin();
                    bin.addProduct(new Product(new SKU("mySKU"), 123));
                    tiles[x][y] = new BinTile(x,y, bin);
                }
                else tiles[x][y] = new BinTile(x, y);
            }
        }
    }

    public void render(OrthographicCamera camera){
        // TODO: 30/09/2019 Adapt so that it only renders tiles within view
        spriteBatch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);
        for(int y = 0;  y < height; y++){
            for(int x = 0; x < width; x++){
                tiles[x][y].render(shapeRenderer, spriteBatch);
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
}
