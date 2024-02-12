package io.github.antonioartigas.geosteroids.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Ship extends Entity {
    public static final float MAX_MOVE_SPEED = 250f;
    public static final float ACCEL = 8.5f;

    // In degrees!
    public float rotation = 0f;
    public int flicker = 0; // Counter to flicker thrust.

    private boolean alive = true;

    public Ship(Vector2 position, Vector2 velocity, float radius) {
        super(position, velocity, radius);
    }

    public Ship(Vector2 position) {
        this(position, new Vector2(), 10f);
    }

    @Override
    public void update(float delta) {
        // Rotation and movement.
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            rotation += 5f;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            rotation -= 5f;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            flicker++;
            if (flicker > 5) {
                flicker = -3;
            }

            velocity.add(
                    MathUtils.cosDeg(rotation) * Ship.ACCEL,
                    MathUtils.sinDeg(rotation) * Ship.ACCEL
            );

            velocity.clamp(0f, Ship.MAX_MOVE_SPEED);

        } else if (!Gdx.input.isKeyPressed(Input.Keys.W)) {
            flicker = 0;
        }

        super.update(delta);
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public boolean isAlive() {
        return alive;
    }

    public void die() {
        alive = false;
    }
}
