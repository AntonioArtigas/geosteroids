package io.github.antonioartigas.geosteroids;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader.FreeTypeFontLoaderParameter;
import com.badlogic.gdx.utils.Disposable;

/**
 * NOTE: There should only be one instance of this class.
 */
public class Assets implements Disposable {
    private final AssetManager manager = new AssetManager();

    private static AssetDescriptor<Sound> sound(String path) {
        return new AssetDescriptor<>(path, Sound.class);
    }

    private static AssetDescriptor<BitmapFont> font(String name, String path, int size) {
        var fontParameter = new FreeTypeFontLoaderParameter();
        fontParameter.fontFileName = path;
        fontParameter.fontParameters.size = size;

        return new AssetDescriptor<>(name, BitmapFont.class, fontParameter);
    }

    public static final AssetDescriptor<Sound> BOOM1 = sound("boom1.wav");
    public static final AssetDescriptor<Sound> BOOM2 = sound("boom2.wav");
    public static final AssetDescriptor<Sound> BOOM3 = sound("boom3.wav");
    public static final AssetDescriptor<Sound> BOOWOMP = sound("boowomp.wav");
    public static final AssetDescriptor<Sound> EXPLOSION = sound("explosion.wav");
    public static final AssetDescriptor<Sound> PEW = sound("pew.wav");
    public static final AssetDescriptor<Sound> PUT = sound("put.wav");
    public static final AssetDescriptor<Sound> RESPAWN = sound("respawn.wav");
    public static final AssetDescriptor<Sound> SELECT = sound("select.wav");
    public static final AssetDescriptor<Sound> START = sound("start.wav");
    public static final AssetDescriptor<Sound> THRUSTER = sound("thruster.wav");
    public static final AssetDescriptor<BitmapFont> BEDSTEAD30 = font("bedstead30.otf", "bedstead.otf", 30);
    public static final AssetDescriptor<BitmapFont> BEDSTEAD60 = font("bedstead60.otf", "bedstead.otf", 60);

    public Assets() {
        var resolver = new InternalFileHandleResolver();
        manager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        manager.setLoader(BitmapFont.class, ".otf", new FreetypeFontLoader(resolver));


        manager.load(BOOM1);
        manager.load(BOOM2);
        manager.load(BOOM3);
        manager.load(BOOWOMP);
        manager.load(EXPLOSION);
        manager.load(PEW);
        manager.load(PUT);
        manager.load(RESPAWN);
        manager.load(SELECT);
        manager.load(START);
        manager.load(THRUSTER);

        manager.load(BEDSTEAD30);
        manager.load(BEDSTEAD60);

        manager.finishLoading();
    }

    public Sound getSound(AssetDescriptor<Sound> descriptor) {
        return manager.get(descriptor);
    }

    public BitmapFont getFont(AssetDescriptor<BitmapFont> descriptor) {
        return manager.get(descriptor);
    }

    @Override
    public void dispose() {
        manager.dispose();
    }
}
