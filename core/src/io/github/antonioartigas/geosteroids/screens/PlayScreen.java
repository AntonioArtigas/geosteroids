package io.github.antonioartigas.geosteroids.screens;

import com.badlogic.gdx.ScreenAdapter;
import io.github.antonioartigas.geosteroids.GeosteroidsGame;
import io.github.antonioartigas.geosteroids.game.Renderer;
import io.github.antonioartigas.geosteroids.game.World;

public class PlayScreen extends ScreenAdapter {
    private final GeosteroidsGame game;

    private final World world;
    private final Renderer renderer;

    public PlayScreen(GeosteroidsGame game) {
        this.game = game;

        world = new World(game.getAssets());
        renderer = new Renderer(game.getAssets(), world);
        world.setListener(renderer);
    }

    @Override
    public void show() {
        world.start();
    }

    @Override
    public void render(float delta) {
        world.update(delta);

        renderer.render();
    }

    @Override
    public void resize(int width, int height) {
        renderer.resize(width, height);
    }
}
