package dk.aau.d507e19.warehousesim.storagegrid;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import dk.aau.d507e19.warehousesim.GraphicsManager;

public class PickerTile extends Tile {

    public PickerTile(int posX, int posY) {
        super(posX, posY);
    }

    public void render(SpriteBatch batch){
        batch.draw(GraphicsManager.getTexture("Simulation/bin/Bin.png"), this.getPosX(), this.getPosY(), Tile.TILE_SIZE, Tile.TILE_SIZE);
    }

    @Override
    public void render(ShapeRenderer shapeRenderer) {
        super.render(shapeRenderer);
    }

    @Override
    public void render(ShapeRenderer shapeRenderer, Color centerColor) {
        super.render(shapeRenderer, centerColor);
    }
}
