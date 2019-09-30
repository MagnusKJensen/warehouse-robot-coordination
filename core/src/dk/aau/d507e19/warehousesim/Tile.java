package dk.aau.d507e19.warehousesim;

import com.badlogic.gdx.graphics.OrthographicCamera;

public class Tile implements Renderable{

    private int posX, posY;

    public Tile(int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
    }

    @Override
    public void render(OrthographicCamera camera) {

    }

}
