package dk.aau.d507e19.warehousesim.storagegrid;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import dk.aau.d507e19.warehousesim.GraphicsManager;
import dk.aau.d507e19.warehousesim.controller.robot.GridCoordinate;

import java.util.ArrayList;

public class StorageGrid {

    private final Tile[][] tiles;
    private final int width, height;

    public StorageGrid(int width, int height){
        this.height = height;
        this.width = width;
        this.tiles = new Tile[width][height];
        fillGrid();
    }

    private void fillGrid(){
        for(int y = 0;  y < height; y++){
            for(int x = 0; x < width; x++){
                if(x == 1 && y == 1) tiles[x][y] = new PickerTile(x,y);
                else if(x == 2 && y == 2) tiles[x][y] = new BinTile(x,y);
                else tiles[x][y] = new Tile(x, y);
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


    public void renderPathOverlay(ArrayList<GridCoordinate> coordinates, ShapeRenderer shapeRenderer){
        for(GridCoordinate gridCoordinate : coordinates)
            tiles[gridCoordinate.getX()][gridCoordinate.getY()].renderOverlay(shapeRenderer);
    }


    public Tile getTile(int x, int y){
        return tiles[x][y];
    }

}
