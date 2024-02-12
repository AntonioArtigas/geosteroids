package io.github.antonioartigas.geosteroids.entities;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Bullet extends Entity {
    public static final float MOVE_SPEED = 400f;
    public static final float MAX_LIFETIME = 2f;

    private float lifetime = 0f;

    public Bullet(Vector2 position, Vector2 velocity, float radius, float rotation) {
        super(
                position, new Vector2(velocity)
                        .add(
                                MathUtils.cosDeg(rotation) * MOVE_SPEED,
                                MathUtils.sinDeg(rotation) * MOVE_SPEED
                        ).clamp(MOVE_SPEED, MOVE_SPEED * 1.5f),
                radius
        );
    }

    public void update(float delta) {
        lifetime += delta;

        super.update(delta);
    }

    public boolean isAlive() {
        return lifetime <= MAX_LIFETIME;
    }
}
