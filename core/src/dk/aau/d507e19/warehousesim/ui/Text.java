package dk.aau.d507e19.warehousesim.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import dk.aau.d507e19.warehousesim.GraphicsManager;

import java.util.Objects;

public class Text extends Actor {
    private BitmapFont font;
    private String myString;
    private float xOffSet;
    private float yOffSet;
    private Color color;


    public Text(String str, float xOffSet, float yOffSet, Color color){
        this.font = GraphicsManager.getFont();
        this.myString = str;
        this.xOffSet = xOffSet;
        this.yOffSet = yOffSet;
        this.color = color;
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        font.setColor(color);
        font.draw(batch, myString, xOffSet, yOffSet);
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        return super.hit(x, y, touchable);
    }

    public void setText(String myString) {
        this.myString = myString;
    }
}