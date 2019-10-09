package dk.aau.d507e19.warehousesim.storagegrid;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class StorageGrid {

    private final Tile[][] tiles;
    private final int width, height;

    private ShapeRenderer shapeRenderer;
    private SpriteBatch spriteBatch;

    public StorageGrid(int width, int height){
        this.height = height;
        this.width = width;
        this.tiles = new Tile[width][height];
        shapeRenderer = new ShapeRenderer();
        spriteBatch = new SpriteBatch();
        spriteBatch.begin();
        fillGrid();
    }

    private void fillGrid(){
        for(int y = 0;  y < height; y++){
            for(int x = 0; x < width; x++){
                if(x == 1 && y == 1) tiles[x][y] = new PickerTile(x,y);
                else tiles[x][y] = new Tile(x, y);
            }
        }
    }

    public void render(OrthographicCamera camera){
        // // TODO: 30/09/2019 Adapt so that it only renders tiles within view
        shapeRenderer.setProjectionMatrix(camera.combined);
        spriteBatch.setProjectionMatrix(camera.combined);
        for(int y = 0;  y < height; y++){
            for(int x = 0; x < width; x++){
                if(tiles[x][y] instanceof PickerTile )((PickerTile)tiles[x][y]).render(spriteBatch);
                else if(tiles[x][y] instanceof BinTile) ((BinTile)tiles[x][y]).render(spriteBatch);
                else tiles[x][y].render(shapeRenderer);
            }
        }
    }

    public Tile getTile(int x, int y){
        return tiles[x][y];
    }
}
