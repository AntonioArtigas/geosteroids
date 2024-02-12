package io.github.antonioartigas.geosteroids.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import io.github.antonioartigas.geosteroids.Assets;
import io.github.antonioartigas.geosteroids.GeosteroidsGame;
import io.github.antonioartigas.geosteroids.entities.Asteroid;
import io.github.antonioartigas.geosteroids.entities.Bullet;
import io.github.antonioartigas.geosteroids.entities.Ship;

public class World {
    public static final float RESPAWN_CIRCLE_RADIUS = 50f;

    private final Array<Sound> booms;
    private final Sound boowomp;
    private final Sound explosion;
    private final Sound pew;
    private final Sound put;
    private final Sound respawn;
    private final Sound thruster;

    final Array<Asteroid> asteroids;
    final Array<Bullet> bullets;
    final Ship player;
    final Circle respawnCircle = new Circle(GeosteroidsGame.WIDTH / 2f, GeosteroidsGame.HEIGHT / 2f, RESPAWN_CIRCLE_RADIUS);
    private boolean nukeAsteroidsInCircle = false;

    private WorldListener listener;

    private final Timer timer;
    private int lives = 3;
    private int score = 0;

    private long thrusterId = -1;

    private float difficulty = 0;

    private boolean gameOver = false;

    public World(Assets assets) {
        booms = new Array<>(3);
        booms.add(assets.getSound(Assets.BOOM1));
        booms.add(assets.getSound(Assets.BOOM2));
        booms.add(assets.getSound(Assets.BOOM3));

        boowomp = assets.getSound(Assets.BOOWOMP);
        explosion = assets.getSound(Assets.EXPLOSION);
        pew = assets.getSound(Assets.PEW);
        put = assets.getSound(Assets.PUT);
        respawn = assets.getSound(Assets.RESPAWN);
        thruster = assets.getSound(Assets.THRUSTER);

        asteroids = new Array<>();
        bullets = new Array<>();

        var center = new Vector2(GeosteroidsGame.WIDTH / 2f, GeosteroidsGame.HEIGHT / 2f);
        player = new Ship(center);
        timer = new Timer();
    }

    public void start() {
        var spawnTask = new Timer.Task() {
            @Override
            public void run() {
                int asteroidsToSpawn = MathUtils.random(2, 5);
                for (int i = 0; i < asteroidsToSpawn; i++) {
                    spawnMeteorsInBorder();
                }

                difficulty = Math.clamp(difficulty - 0.25f, 1f, 5f);

                timer.scheduleTask(this, 5 - difficulty);
            }
        };
        timer.scheduleTask(spawnTask, 5);
    }

    private void spawnMeteorsInBorder() {
        int side = MathUtils.random(3);

        int x, y;

        switch (side) {
            case 0 -> { // Left side.
                x = -30;
                y = MathUtils.random(0, GeosteroidsGame.HEIGHT);
            }
            case 1 -> { // Right side.
                x = GeosteroidsGame.WIDTH + 30;
                y = MathUtils.random(0, GeosteroidsGame.HEIGHT);
            }
            case 2 -> { // Top side.
                y = GeosteroidsGame.HEIGHT + 30;
                x = MathUtils.random(0, GeosteroidsGame.WIDTH);
            }
            case 3 -> { // Bottom side.
                y = -30;
                x = MathUtils.random(0, GeosteroidsGame.WIDTH);
            }

            default -> throw new IllegalStateException("How did this happen?");
        }

        var position = new Vector2(x, y);

        spawnRandomMeteor(position);
    }

    private void spawnRandomMeteor(Vector2 position) {
        int stage = MathUtils.random(1, 3);
        spawnRandomMeteor(position, stage);
    }

    private void spawnRandomMeteor(Vector2 position, Vector2 velocity) {
        int stage = MathUtils.random(1, 3);
        spawnMeteor(position, velocity, stage);
    }

    private void spawnRandomMeteor(Vector2 position, int stage) {
        var velocity = new Vector2(
                MathUtils.random(-50f, 50f),
                MathUtils.random(-50f, 50f)
        );
        spawnMeteor(position, velocity, stage);
    }

    private void spawnMeteor(Vector2 position, Vector2 velocity, int stage) {
        int sides = MathUtils.random(4, 8);
        float shapeRadius = stage * 15;
        float radius = shapeRadius * 0.9f;
        var asteroid = new Asteroid(position, velocity, sides, shapeRadius, radius, stage);
        asteroids.add(asteroid);
    }

    public void update(float delta) {
        // Summon an asteroid at the mouse when left-clicked. Debug purposes.
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            put.play();
            var position = new Vector2(Gdx.input.getX(), GeosteroidsGame.HEIGHT - Gdx.input.getY());
            spawnRandomMeteor(position);
        }

        // Spawn meteor that doesn't move.
        if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            put.play();
            var position = new Vector2(Gdx.input.getX(), GeosteroidsGame.HEIGHT - Gdx.input.getY());
            spawnRandomMeteor(position, new Vector2());
        }

        // Update bullets.
        var bulletsIter = bullets.iterator();
        while (bulletsIter.hasNext()) {
            var bullet = bulletsIter.next();
            bullet.update(delta);

            if (!bullet.isAlive()) {
                bulletsIter.remove();
            }
        }

        // Asteroid collisions with bullets.
        var asteroidsIter = asteroids.iterator();
        while (asteroidsIter.hasNext()) {
            var asteroid = asteroidsIter.next();
            asteroid.update(delta);

            bulletsIter.reset();
            while (bulletsIter.hasNext()) {
                var bullet = bulletsIter.next();
                if (asteroid.hitbox.overlaps(bullet.hitbox)) {
                    score += 100;
                    bulletsIter.remove();
                    asteroidsIter.remove();
                    booms.random().play();

                    for (int i = 0; i < asteroid.stage; i++) {
                        if (asteroid.stage > 1) {
                            spawnRandomMeteor(asteroid.position.cpy(), asteroid.stage - 1);
                        }
                    }
                }
            }
        }

        // Check for asteroid collisions with player OR circle.
        asteroidsIter.reset();
        while (asteroidsIter.hasNext()) {
            var asteroid = asteroidsIter.next();
            if (nukeAsteroidsInCircle && asteroid.hitbox.overlaps(respawnCircle)) {
                asteroidsIter.remove();
                continue;
            }

            if (player.isAlive() && asteroid.hitbox.overlaps(player.hitbox)) {
                player.setAlive(false);
                thruster.stop(thrusterId);
                explosion.play();
                lives--;
                if (listener != null) {
                    listener.playerDied(lives);
                }
                player.setPosition(
                        GeosteroidsGame.WIDTH / 2f,
                        GeosteroidsGame.HEIGHT / 2f
                );
                timer.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        if (lives <= 0) {
                            if (listener != null) {
                                boowomp.play();
                                listener.gameOver();
                                gameOver = true;
                            }
                            return;
                        }

                        // Otherwise, respawn!
                        nukeAsteroidsInCircle = true;

                        respawn.play(0.5f);
                        player.velocity.setZero();
                        player.setAlive(true);
                        if (listener != null) {
                            listener.playerRespawn();
                        }
                    }
                }, 2f);
            }
        }

        if (nukeAsteroidsInCircle) {
            nukeAsteroidsInCircle = false;
        }

        if (player.isAlive()) {
            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                if (thrusterId < 0) {
                    thrusterId = thruster.play(0.5f);
                    thruster.setLooping(thrusterId, true);
                }
            } else if (!Gdx.input.isKeyPressed(Input.Keys.W)) {
                thruster.stop(thrusterId);
                thrusterId = -1;
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                var front = new Vector2(player.position.x + 10, player.position.y).rotateAroundDeg(player.position, player.rotation);
                var bullet = new Bullet(front, player.velocity, 1f, player.rotation);
                bullets.add(bullet);
                pew.play(0.5f);
            }

            player.update(delta);
        }

        if (gameOver && Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }

    public void setListener(WorldListener listener) {
        this.listener = listener;
    }

    public int getLives() {
        return lives;
    }

    public int getScore() {
        return score;
    }
}
