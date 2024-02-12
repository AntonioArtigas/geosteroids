package io.github.antonioartigas.geosteroids.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.antonioartigas.geosteroids.Assets;
import io.github.antonioartigas.geosteroids.GeosteroidsGame;
import io.github.antonioartigas.geosteroids.entities.Asteroid;
import io.github.antonioartigas.geosteroids.entities.Bullet;

public class Renderer implements Disposable, WorldListener {
    public static final boolean DEBUG = false;

    private final World world;
    private final BitmapFont smallFont;
    private final BitmapFont bigFont;

    private final OrthographicCamera camera;
    private final Viewport viewport;

    private final ShapeRenderer shapes;
    private final SpriteBatch batch;

    private float shake = 0;
    private final Vector2 center;
    private final Vector2 shakeOffset;

    private boolean showRespawnCircle = false;
    private float respawnCircleRadius = 0f;

    private boolean gameIsOver = false;
    private final GlyphLayout gameOverText;
    private final GlyphLayout quitText;

    public Renderer(Assets assets, World world) {
        this.world = world;
        smallFont = assets.getFont(Assets.BEDSTEAD30);
        bigFont = assets.getFont(Assets.BEDSTEAD60);

        camera = new OrthographicCamera();
        camera.setToOrtho(false);

        viewport = new StretchViewport(GeosteroidsGame.WIDTH, GeosteroidsGame.HEIGHT, camera);

        shapes = new ShapeRenderer();
        batch = new SpriteBatch();

        center = new Vector2(GeosteroidsGame.WIDTH / 2f, GeosteroidsGame.HEIGHT / 2f);
        shakeOffset = new Vector2();

        gameOverText = new GlyphLayout(bigFont, "GAME OVER");
        quitText = new GlyphLayout(smallFont, "ESC to quit");
    }

    @Override
    public void playerDied(int lives) {
        shake = 100f;
        if (lives > 0) {
            showRespawnCircle = true;
        }
    }

    @Override
    public void playerRespawn() {
        showRespawnCircle = false;
        respawnCircleRadius = 0f;
    }

    @Override
    public void gameOver() {
        gameIsOver = true;
    }

    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    private void drawShip(Vector2 position, float rotation, int flicker) {
        // Ship shape.
        Vector2 leftUpper = new Vector2(position.x - 10, position.y + 10).rotateAroundDeg(position, rotation);
        Vector2 front = new Vector2(position.x + 10, position.y).rotateAroundDeg(position, rotation);
        Vector2 leftLower = new Vector2(position.x - 10, position.y - 10).rotateAroundDeg(position, rotation);

        // Thruster shape.
        Vector2 thrustA = new Vector2(position.x - 10, position.y + 5).rotateAroundDeg(position, rotation);
        Vector2 thrustB = new Vector2(position.x - 15, position.y).rotateAroundDeg(position, rotation);
        Vector2 thrustC = new Vector2(position.x - 10, position.y - 5).rotateAroundDeg(position, rotation);

        // Draw both shapes.
        shapes.triangle(
                leftUpper.x, leftUpper.y,
                front.x, front.y,
                leftLower.x, leftLower.y,
                Color.WHITE, Color.WHITE, Color.WHITE
        );

        // Only draw the thruster sometimes. update() changes flicker variable.
        if (flicker > 1) {
            shapes.triangle(
                    thrustA.x, thrustA.y,
                    thrustB.x, thrustB.y,
                    thrustC.x, thrustC.y,
                    Color.WHITE, Color.WHITE, Color.WHITE
            );
        }
    }

    private void drawAsteroid(Asteroid asteroid) {
        float[] points = new float[asteroid.sides * 2];

        Vector2 forward = new Vector2(asteroid.shapeRadius, 0);
        float rotateBy = 360f / asteroid.sides;
        for (int i = 0; i < points.length; i += 2) {
            forward.setAngleDeg(rotateBy * (i / 2f) + asteroid.rotation);
            points[i] = forward.x + asteroid.position.x;
            points[i + 1] = forward.y + asteroid.position.y;
        }

        shapes.polygon(points);
        // Show the hitbox debug.
        if (DEBUG) {
            shapes.circle(asteroid.position.x, asteroid.position.y, asteroid.hitbox.radius);
        }
    }

    private void drawBullet(Bullet bullet) {
        shapes.circle(bullet.position.x, bullet.position.y, 1f);
    }

    public void render() {
        ScreenUtils.clear(0f, 0f, 0f, 1f);

        if (shake > 1f) {
            shakeOffset.set(MathUtils.random(-1f, 1f) * shake, MathUtils.random(-1f, 1f) * shake);
            camera.position.set(center, 0f).add(shakeOffset.x, shakeOffset.y, 0f);
            shake *= 0.9f;
        } else {
            camera.position.set(center, 0f);
        }

        viewport.apply();

        shapes.setProjectionMatrix(camera.combined);
        shapes.begin(ShapeType.Line);

        var player = world.player;
        if (player.isAlive()) {
            drawShip(player.position, player.rotation, player.flicker);
        }

        for (var asteroid : world.asteroids) {
            drawAsteroid(asteroid);
        }

        for (var bullet : world.bullets) {
            drawBullet(bullet);
        }

        // Draw lives.
        for (int i = 0; i < world.getLives(); i++) {
            int x = 18 + 30 * i;
            drawShip(new Vector2(x, GeosteroidsGame.HEIGHT - 50), 90, 0);
        }

        if (DEBUG) {
            shapes.circle(player.position.x, player.position.y, player.hitbox.radius);
        }

        if (showRespawnCircle) {
            var delta = Gdx.graphics.getDeltaTime();
            respawnCircleRadius = MathUtils.clamp(respawnCircleRadius + 50f * delta, 0, World.RESPAWN_CIRCLE_RADIUS);
            shapes.circle(center.x, center.y, respawnCircleRadius);
        }

        shapes.end();

        // Render text on top of the game.
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        smallFont.draw(batch, Integer.toString(world.getScore()), 10, GeosteroidsGame.HEIGHT - 10);

        if (gameIsOver) {
            bigFont.draw(batch, gameOverText, GeosteroidsGame.WIDTH / 2f - gameOverText.width / 2f, GeosteroidsGame.HEIGHT / 2f + gameOverText.height / 2f);
            smallFont.draw(batch, quitText, GeosteroidsGame.WIDTH / 2f - quitText.width / 2f, GeosteroidsGame.HEIGHT / 2f - 50);
        }
        batch.end();
    }

    @Override
    public void dispose() {
        shapes.dispose();
        batch.dispose();
    }
}
