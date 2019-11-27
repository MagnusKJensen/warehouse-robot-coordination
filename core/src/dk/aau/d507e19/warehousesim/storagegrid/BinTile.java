package dk.aau.d507e19.warehousesim.storagegrid;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import dk.aau.d507e19.warehousesim.GraphicsManager;
import dk.aau.d507e19.warehousesim.storagegrid.product.Bin;

public class BinTile extends Tile {
    private Bin bin = null;

    public BinTile(int posX, int posY) {
        super(posX, posY);
    }

    public BinTile(int posX, int posY, Bin bin) {
        super(posX, posY);
        this.bin = bin;
    }

    @Override
    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch) {
        // If it has no bin, it should be rendered as such
        if(this.bin == null){
            batch.begin();
            batch.draw(GraphicsManager.getTexture("Simulation/tiles/binTileEmpty.png"), this.getPosX(), this.getPosY(), TILE_SIZE, TILE_SIZE);
            batch.end();
        } else {
            if(this.bin.isEmpty()){
                batch.begin();
                batch.draw(GraphicsManager.getTexture("Simulation/tiles/binEmpty.png"), this.getPosX(), this.getPosY(), TILE_SIZE, TILE_SIZE);
                batch.end();
            }else {
                batch.begin();
                batch.draw(GraphicsManager.getTexture("Simulation/tiles/Bin.png"), this.getPosX(), this.getPosY(), TILE_SIZE, TILE_SIZE);
                batch.end();
            }

        }
    }

    // 782 RRT extended, 764 dummy, 827 astar, custom h 680

    public void addBin(Bin bin){
        if(this.bin != null) throw new IllegalArgumentException("Tile at position(" + getPosX() + "," + getPosY() + ") already has a bin");
        this.bin = bin;
    }

    public Bin releaseBin(){
        if(this.bin == null) throw new RuntimeException("Cannot take bin at (" + getPosX() + "," + getPosY() + ") due to it being empty");
        Bin tempBin = this.bin;
        this.bin = null;
        return tempBin;
    }

    public boolean hasBin(){
        return bin != null;
    }


    public Bin getBin() {
        return bin;
    }

    @Override
    public String toString() {
        return "BinTile{" + "posX = " + getPosX() + ", posY = " + getPosY() +
                ", \nbin=" + bin +
                '}';
    }
}
