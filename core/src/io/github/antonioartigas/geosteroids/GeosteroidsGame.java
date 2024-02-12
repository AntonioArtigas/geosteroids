package io.github.antonioartigas.geosteroids;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import io.github.antonioartigas.geosteroids.screens.PlayScreen;
import io.github.antonioartigas.geosteroids.screens.TitleScreen;

public class GeosteroidsGame extends Game {
    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;

    private Assets assets;

    public Screen titleScreen;
    public Screen playScreen;

    @Override
    public void create() {
        assets = new Assets();

        titleScreen = new TitleScreen(this);
        playScreen = new PlayScreen(this);
        setScreen(titleScreen);
//        setScreen(playScreen);
    }

    public Assets getAssets() {
        return assets;
    }

    @Override
    public void dispose() {
        titleScreen.dispose();
        playScreen.dispose();
    }
}
