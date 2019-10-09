package dk.aau.d507e19.warehousesim.storagegrid;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import dk.aau.d507e19.warehousesim.GraphicsManager;

public class PickerTile extends Tile {

    public PickerTile(int posX, int posY) {
        super(posX, posY);
    }

    @Override
    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch) {
        batch.draw(GraphicsManager.getTexture("Simulation/tiles/pickerPoint.png"), this.getPosX(), this.getPosY(), Tile.TILE_SIZE, Tile.TILE_SIZE);
    }
}
