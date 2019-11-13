package dk.aau.d507e19.warehousesim.storagegrid;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import dk.aau.d507e19.warehousesim.GraphicsManager;

public class MaintenanceTile extends Tile {
    private boolean reserved = false;
    public MaintenanceTile(int posX, int posY) {
        super(posX, posY);
    }
    @Override
    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch) {
        if(reserved){
            batch.begin();
            batch.draw(GraphicsManager.getTexture("Simulation/tiles/maintenanceTile_Reserved.png"), this.getPosX(), this.getPosY(), Tile.TILE_SIZE, Tile.TILE_SIZE);
            batch.end();
        }
        else {
            batch.begin();
            batch.draw(GraphicsManager.getTexture("Simulation/tiles/maintenanceTile.png"), this.getPosX(), this.getPosY(), Tile.TILE_SIZE, Tile.TILE_SIZE);
            batch.end();
        }

    }

    public boolean isReserved() {
        return reserved;
    }

    public void setReserved(boolean reserved) {
        this.reserved = reserved;
    }
}
