package dk.aau.d507e19.warehousesim;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public interface Drawable {

    void render(ShapeRenderer shapeRenderer, SpriteBatch batch);

}
