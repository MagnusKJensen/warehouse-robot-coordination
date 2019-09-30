package dk.aau.d507e19.warehousesim;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.Random;

public class Tile implements Renderable{

    private int posX, posY;
    private Color color;
    private ShapeRenderer shapeRenderer;
    public static final int TILE_SIZE = 1;

    public Tile(int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
        Random random = new Random();
        this.color = new Color(random.nextFloat(), random.nextFloat(), random.nextFloat(), 1);
    }

    public void render(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(color);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(posX, posY, TILE_SIZE, TILE_SIZE);
        shapeRenderer.end();
    }

    @Override
    public void render(OrthographicCamera camera) {

    }
}
