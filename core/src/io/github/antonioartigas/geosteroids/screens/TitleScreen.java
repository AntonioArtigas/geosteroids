package io.github.antonioartigas.geosteroids.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.antonioartigas.geosteroids.Assets;
import io.github.antonioartigas.geosteroids.GeosteroidsGame;

public class TitleScreen extends ScreenAdapter {
    private final GeosteroidsGame game;

    private final SpriteBatch batch;
    private final BitmapFont font;

    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final Sound select;
    private final Sound start;

    private int selected = 0;

    private final Vector2 center;

    private final GlyphLayout title;
    private final GlyphLayout play;
    private final GlyphLayout quit;

    public TitleScreen(GeosteroidsGame game) {
        this.game = game;
        var assets = game.getAssets();

        batch = new SpriteBatch();
        font = assets.getFont(Assets.BEDSTEAD30);

        camera = new OrthographicCamera();
        camera.setToOrtho(false);

        viewport = new StretchViewport(GeosteroidsGame.WIDTH, GeosteroidsGame.HEIGHT, camera);

        select = assets.getSound(Assets.SELECT);
        start = assets.getSound(Assets.START);

        center = new Vector2();

        title = new GlyphLayout(font, "GEOSTEROIDS");
        play = new GlyphLayout(font, "Enter to play");
        quit = new GlyphLayout(font, "ESC to quit");
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0f, 0f, 0f, 1f);

        viewport.apply();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        font.draw(batch, title, GeosteroidsGame.WIDTH / 2f - title.width / 2f, GeosteroidsGame.HEIGHT / 2f + 150);
        font.draw(batch, play, GeosteroidsGame.WIDTH / 2f - play.width / 2f, GeosteroidsGame.HEIGHT / 2f + 50);
        font.draw(batch, quit, GeosteroidsGame.WIDTH / 2f - quit.width / 2f, GeosteroidsGame.HEIGHT / 2f - 50);
        batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            start.play();
            game.setScreen(game.playScreen);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
