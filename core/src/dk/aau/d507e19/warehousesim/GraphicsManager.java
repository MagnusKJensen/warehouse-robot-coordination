package dk.aau.d507e19.warehousesim;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import java.nio.file.NoSuchFileException;

public class GraphicsManager {
    private static AssetManager assetManager = new AssetManager();
    private static BitmapFont defaultFont;

    public static void addTexture(String path){
        assetManager.load(path, Texture.class);
    }

    public static Texture getTexture(String path){
        if(!assetManager.contains(path)) try {
            throw new NoSuchFileException("File with path '" + path + "' not found.");
        } catch (NoSuchFileException e) {
            e.printStackTrace();
        }
        return assetManager.get(path, Texture.class);
    }

    public static void disposeAssetManager(){
        assetManager.dispose();
        defaultFont.dispose();
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
        parameter.size = 14;
        parameter.minFilter = Texture.TextureFilter.Linear;
        parameter.magFilter = Texture.TextureFilter.Linear;
        BitmapFont font = generator.generateFont(parameter); // font size 12 pixels
        generator.dispose();
        return font;
    }

    static void loadAssets() {
        // Robots
        addTexture("Simulation/Robots/robotTaskAssigned.png");
        addTexture("Simulation/Robots/robotAvailable.png");
        addTexture("Simulation/Robots/robotTaskAssignedCarrying.png");
        addTexture("Simulation/Robots/robotMovingOutOfWay.png");
        addTexture("Simulation/Robots/robotMovingOutOfWayBusy.png");
        addTexture("Simulation/Robots/robotMovingOutOfWayCarrying.png");

        // Tiles
        addTexture("Simulation/tiles/Bin.png");
        addTexture("Simulation/tiles/pickerPoint.png");
        addTexture("Simulation/tiles/binTileEmpty.png");
        addTexture("Simulation/tiles/binEmpty.png");
        addTexture("Simulation/tiles/pickerTileReserved.png");

        // Icons
        addTexture("icons/fast_forward.png");
        addTexture("icons/fastest_forward.png");
        addTexture("icons/global_step_back.png");
        addTexture("icons/global_step_forward.png");
        addTexture("icons/pause.png");
        addTexture("icons/play.png");

        // finish
        finishLoading();

        defaultFont = loadFont();
    }

}
