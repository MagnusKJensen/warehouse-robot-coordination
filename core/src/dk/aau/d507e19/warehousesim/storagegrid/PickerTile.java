package dk.aau.d507e19.warehousesim.storagegrid;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import dk.aau.d507e19.warehousesim.GraphicsManager;
import dk.aau.d507e19.warehousesim.controller.server.order.Order;
import dk.aau.d507e19.warehousesim.exception.CouldNotFinishOrderException;
import dk.aau.d507e19.warehousesim.storagegrid.product.Product;

import java.util.ArrayList;

public class PickerTile extends Tile {
    private Order currentOrder = null;
    private ArrayList<Product> holdingProducts = new ArrayList<>();

    public PickerTile(int posX, int posY) {
        super(posX, posY);
    }

    @Override
    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch) {
        if(hasOrder()){
            batch.begin();
            batch.draw(GraphicsManager.getTexture("Simulation/tiles/pickerTileReserved.png"), this.getPosX(), this.getPosY(), Tile.TILE_SIZE, Tile.TILE_SIZE);
            batch.end();
        }
        else {
            batch.begin();
            batch.draw(GraphicsManager.getTexture("Simulation/tiles/pickerPoint.png"), this.getPosX(), this.getPosY(), Tile.TILE_SIZE, Tile.TILE_SIZE);
            batch.end();
        }

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

    public void finishOrder(){
        // Check that all picker is holding all the correct orders
        for(Product product : currentOrder.getAllProductsInOrder()){
            if(!holdingProducts.contains(product)) throw new CouldNotFinishOrderException(currentOrder, currentOrder.getAllProductsInOrder(), holdingProducts);
        }
        holdingProducts.clear();
        currentOrder = null;
    }

    public void removeOrder(){
        currentOrder = null;
    }

    public void acceptProducts(ArrayList<Product> products){
        holdingProducts.addAll(products);
    }

    public ArrayList<Product> getHoldingProducts() {
        return holdingProducts;
    }
}
