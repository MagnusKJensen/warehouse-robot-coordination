package dk.aau.d507e19.warehousesim.storagegrid;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import dk.aau.d507e19.warehousesim.GraphicsManager;

public class BinTile extends Tile {
    public BinTile(int posX, int posY) {
        super(posX, posY);
    }

    @Override
    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch) {
        batch.draw(GraphicsManager.getTexture("Simulation/tiles/Bin.png"), this.getPosX(), this.getPosY(), TILE_SIZE, TILE_SIZE);
    }
}
