package dk.aau.d507e19.warehousesim.storagegrid;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import dk.aau.d507e19.warehousesim.GraphicsManager;
import dk.aau.d507e19.warehousesim.controller.server.order.Order;

public class PickerTile extends Tile {
    private Order currentOrder = null;

    public PickerTile(int posX, int posY) {
        super(posX, posY);
    }

    @Override
    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch) {
        batch.begin();
        batch.draw(GraphicsManager.getTexture("Simulation/tiles/pickerPoint.png"), this.getPosX(), this.getPosY(), Tile.TILE_SIZE, Tile.TILE_SIZE);
        batch.end();
    }

    @Override
    public String toString() {
        return "PickerTile{posX=" + getPosX() +
                ", posY=" + getPosY() + "}";
    }

    public void assignOrder(Order order){
        currentOrder = order;
    }

    public boolean hasOrder(){
        return currentOrder != null;
    }

    public void setAvailable(){
        currentOrder = null;
    }
}
