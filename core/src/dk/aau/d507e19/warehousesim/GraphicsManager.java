package dk.aau.d507e19.warehousesim;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class GraphicsManager {
    private static AssetManager assetManager = new AssetManager();
    private static BitmapFont defaultFont;

    public static void addTexture(String path){
        assetManager.load(path, Texture.class);
    }

    public static Texture getTexture(String path){
        return assetManager.get(path, Texture.class);
    }

    public static void disposeAssetManager(){
        assetManager.dispose();
    }

    public static void finishLoading(){
        assetManager.finishLoading();
    }

    public static TextureRegionDrawable getTextureRegionDrawable(String path){
        return new TextureRegionDrawable((Texture) assetManager.get(path));
    }

    public static BitmapFont getFont(){
        if(defaultFont == null)
            defaultFont = loadFont();
        return defaultFont;
    }

    private static BitmapFont loadFont(){
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/OpenSans.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 12;
        parameter.minFilter = Texture.TextureFilter.Linear;
        parameter.magFilter = Texture.TextureFilter.Linear;
        BitmapFont font = generator.generateFont(parameter); // font size 12 pixels
        generator.dispose();
        return font;
    }

    static void loadAssets() {
        // Robots
        GraphicsManager.addTexture("Simulation/Robots/robotTaskAssigned.png");
        GraphicsManager.addTexture("Simulation/Robots/robotAvailable.png");
        GraphicsManager.addTexture("Simulation/Robots/robotTaskAssignedCarrying.png");

        // Bins
        addTexture("Simulation/tiles/Bin.png");
        addTexture("Simulation/tiles/pickerPoint.png");

        // Icons
        GraphicsManager.addTexture("icons/fast_forward.png");
        GraphicsManager.addTexture("icons/fastest_forward.png");
        GraphicsManager.addTexture("icons/global_step_back.png");
        GraphicsManager.addTexture("icons/global_step_forward.png");
        GraphicsManager.addTexture("icons/pause.png");
        GraphicsManager.addTexture("icons/play.png");

        // finish
        GraphicsManager.finishLoading();

        defaultFont = loadFont();
    }

}
