package ro.luca.dodger.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.scenes.scene2d.Actor;

import ro.luca.dodger.DodgerGame;

public class Laser extends Actor
{
    public static float speed = 500;
    private static Texture texture;
    private static Sound shootSound;
    private Polygon collisionBox = new Polygon();

    Laser(float x, float y)
    {
        if (texture == null)
            texture = new Texture("textures/laser.png");
        if (shootSound == null)
            shootSound = Gdx.audio.newSound(Gdx.files.internal("audio/shoot.mp3"));
        setPosition(x, y);
        setSize(texture.getWidth(), texture.getHeight());

        float[] vertices = new float[]{
                0, 0,
                getWidth(), 0,
                getWidth(), getHeight(),
                0, getHeight()
        };
        collisionBox.setVertices(vertices);

        shootSound.play(1);
    }

    public static void dispose()
    {
        Laser.texture.dispose();
    }

    boolean isOffScreen()
    {
        return getY() + getHeight() > Gdx.graphics.getHeight();
    }

    @Override
    public void draw(Batch batch, float parentAlpha)
    {
        super.draw(batch, parentAlpha);
        batch.setColor(getColor().r, getColor().g, getColor().b, getColor().a * parentAlpha);
        batch.draw(texture, getX(), getY());

        batch.end();
        if (DodgerGame.debug)
        {
            getCollisionBox();
            DodgerGame.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            DodgerGame.shapeRenderer.setColor(Color.CYAN);
            DodgerGame.shapeRenderer.polygon(collisionBox.getTransformedVertices());
            DodgerGame.shapeRenderer.setColor(Color.WHITE);
            DodgerGame.shapeRenderer.end();
        }
        batch.begin();
    }

    @Override
    public void act(float delta)
    {
        super.act(delta);
        moveBy(0, speed * delta);
    }

    public Polygon getCollisionBox()
    {
        collisionBox.setPosition(getX(), getY());
        return collisionBox;
    }
}
