package io.github.antonioartigas.geosteroids.entities;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import io.github.antonioartigas.geosteroids.GeosteroidsGame;

public abstract class Entity {
    public final Vector2 position;
    public final Vector2 velocity;

    public final Circle hitbox;

    public Entity(Vector2 position, Vector2 velocity, float radius) {
        this.position = position;
        this.velocity = velocity;
        this.hitbox = new Circle(position, radius);
    }

    public void update(float delta) {
        position.mulAdd(velocity, delta);
        hitbox.setPosition(position);

        // Left to right.
        if (position.x < -(hitbox.radius * 2)) {
            position.x = GeosteroidsGame.WIDTH + hitbox.radius * 2;
        }

        // Right to left.
        if (position.x > GeosteroidsGame.WIDTH + hitbox.radius * 2) {
            position.x = -(hitbox.radius * 2);
        }

        // Bottom to top.
        if (position.y < -(hitbox.radius * 2)) {
            position.y = GeosteroidsGame.HEIGHT + hitbox.radius * 2;
        }

        // Top to bottom.
        if (position.y > GeosteroidsGame.HEIGHT + hitbox.radius * 2) {
            position.y = -(hitbox.radius * 2);
        }
    }

    public void setPosition(float x, float y) {
        position.set(x, y);
        hitbox.setPosition(x, y);
    }
}
