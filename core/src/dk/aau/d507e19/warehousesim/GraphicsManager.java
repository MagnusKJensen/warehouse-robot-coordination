package dk.aau.d507e19.warehousesim;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class GraphicsManager {
    private static AssetManager assetManager = new AssetManager();

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
}
