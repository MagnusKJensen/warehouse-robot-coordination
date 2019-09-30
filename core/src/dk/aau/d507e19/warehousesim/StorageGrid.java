package dk.aau.d507e19.warehousesim;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class StorageGrid {

    private final Tile[][] tiles;
    private final int width, height;

    private ShapeRenderer shapeRenderer;

    public StorageGrid(int width, int height){
        this.height = height;
        this.width = width;
        this.tiles = new Tile[width][height];
        shapeRenderer = new ShapeRenderer();
        fillGrid();
    }

    private void fillGrid(){
        for(int y = 0;  y < height; y++){
            for(int x = 0; x < width; x++){
                tiles[x][y] = new Tile(x, y);
            }
        }
    }

    public void render(OrthographicCamera camera){
        // // TODO: 30/09/2019 Adapt so that it only renders tiles within view
        for(int y = 0;  y < height; y++){
            for(int x = 0; x < width; x++){
                shapeRenderer.setProjectionMatrix(camera.combined);
                tiles[x][y].render(shapeRenderer);
            }
        }
    }

}
