package ro.luca.dodger.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.scenes.scene2d.Actor;

import ro.luca.dodger.DodgerGame;

public class Asteroid extends Actor
{
    public static float minSpawnDelay = .1f, maxSpawnDelay = .2f, damagePercent = 1;
    private static float minSize = 50, maxSize = 110;
    private static float speed = 200;
    private static Texture texture;
    private Polygon collisionBox = new Polygon();

    public Asteroid()
    {
        if (texture == null)
            texture = new Texture("textures/asteroid.png");
        float size = MathUtils.random(minSize, maxSize);
        setSize(size, (float) texture.getHeight() / texture.getWidth() * size);
        setPosition(MathUtils.random(-getWidth() / 2, Gdx.graphics.getWidth() - getWidth() / 2), Gdx.graphics.getHeight());

        float[] vertices = new float[]{
                35, 0,
                52, 0,
                80, 25,
                80, 32,
                63, 56,
                42, 67,
                29, 67,
                0, 43,
                0, 24
        };
        collisionBox.setVertices(vertices);
        collisionBox.setScale(getWidth() / 80f, getHeight() / 67f);
    }

    public static void dispose()
    {
        Asteroid.texture.dispose();
    }

    public static float getSpeed()
    {
        return Asteroid.speed;
    }

    public static void setSpeed(float speed)
    {
        Asteroid.speed = speed;
    }

    public static void makeAsteroidsSmallerBy(int percent)
    {
        if (minSize > 15)
            minSize = minSize - percent / 100f * Asteroid.minSize;
        if (maxSize > 30)
            maxSize = maxSize - percent / 100f * Asteroid.maxSize;
    }

    public static float getDamagePercent()
    {
        return damagePercent;
    }

    public static void setDamagePercent(float damagePercent)
    {
        if (damagePercent > .3f)
            Asteroid.damagePercent = damagePercent;
    }

    @Override
    public void draw(Batch batch, float parentAlpha)
    {
        super.draw(batch, parentAlpha);
        batch.setColor(getColor().r, getColor().g, getColor().b, getColor().a * parentAlpha);

        batch.draw(texture, getX(), getY(), getWidth(), getHeight());

        batch.end();
        if (DodgerGame.debug)
        {
            getCollisionBox();
            DodgerGame.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            DodgerGame.shapeRenderer.setColor(Color.RED);
            DodgerGame.shapeRenderer.polygon(collisionBox.getTransformedVertices());
            DodgerGame.shapeRenderer.setColor(Color.WHITE);
            DodgerGame.shapeRenderer.end();
        }
        batch.begin();
    }

    public Polygon getCollisionBox()
    {
        collisionBox.setPosition(getX(), getY());
        return collisionBox;
    }

    @Override
    public void act(float delta)
    {
        super.act(delta);
        if (getActions().size == 0)
            moveBy(0, -speed * delta);
    }

    public boolean isOffScreen()
    {
        return getY() + getHeight() < 0;
    }

    public float getDamage()
    {
        return (getWidth() / 300f - 20 / getWidth() + .4f) * damagePercent;
    }

    public float getScore()
    {
        float score = 8000 / getWidth() - 7 * getWidth() / 11;
        if (score < 0)
            score = 0;
        return score;
    }
}
