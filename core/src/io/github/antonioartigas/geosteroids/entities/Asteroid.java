package io.github.antonioartigas.geosteroids.entities;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 * The antagonist of the game...
 * <p>
 * We'll just draw these as regular shapes. Like pentagons, hexagons, heptagons, and uh other -agons.
 * Also by regular I mean the symmetrical shapes.
 * <p>
 * BUT their collisions are circles! (Makes life easier)
 * Usually smaller than the polygon to be nicer.
 */
public class Asteroid extends Entity {
    public final int sides;
    public final float shapeRadius;
    public final float rotation;

    public int stage;

    public Asteroid(Vector2 position, Vector2 velocity, int sides, float shapeRadius, float radius, int stage) {
        super(position, velocity, radius);

        this.sides = sides;
        this.shapeRadius = shapeRadius;
        rotation = MathUtils.random() * 360f;
        this.stage = stage;
    }
}
