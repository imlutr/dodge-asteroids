package ro.luca.dodger.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.scenes.scene2d.Actor;

import ro.luca.dodger.DodgerGame;
import ro.luca.dodger.util.LinkedList;

public class Ship extends Actor
{
    private float speed, shootingDelay;
    private float score, scorePerSecond, scoreDelay, scoreTimer;

    private Animation<TextureRegion>[] animations;
    private float stateTime, tiltTimer;
    private int tilt = 2; // 0 = left, 2 = no tilt, 4 = right
    private LinkedList lasers = new LinkedList();
    private float shootingTimer = 0;
    private Polygon collisionBox = new Polygon();
    private float actualHealth, targetHealth;

    public Ship(float y)
    {
        setY(y);
        setSize(64, 96);
        reset();

        animations = new Animation[5];
        Texture shipSheet = new Texture("textures/ship.png");
        TextureRegion[][] tmp = TextureRegion.split(shipSheet, shipSheet.getWidth() / 2, shipSheet.getHeight() / 5);
        animations[0] = new Animation<TextureRegion>(0.1f, tmp[0]);
        animations[1] = new Animation<TextureRegion>(0.1f, tmp[1]);
        animations[3] = new Animation<TextureRegion>(0.1f, tmp[3]);
        animations[2] = new Animation<TextureRegion>(0.1f, tmp[2]);
        animations[4] = new Animation<TextureRegion>(0.1f, tmp[4]);
    }

    public void reset()
    {
        setX(Gdx.graphics.getWidth() / 2 - 32);
        tiltCollisionBox();
        actualHealth = targetHealth = 1;
        lasers.removeAll();
        Laser.speed = 500;
        speed = 275;
        shootingDelay = .75f;
        score = 0;
        scorePerSecond = 5;
        scoreDelay = .2f;
        scoreTimer = 0;
    }

    private void tiltCollisionBox()
    {
        int offset = getOffset();
        float vertices[];
        vertices = new float[]
                {
                        offset, 37,
                        64 - offset, 37,
                        64 - offset, 72,
                        39, 96,
                        25, 96,
                        offset, 72
                };
        collisionBox.setVertices(vertices);
    }

    private int getOffset()
    {
        switch (tilt)
        {
            case 0:
            case 4:
                return 8;
            case 1:
            case 3:
                return 4;
            case 2:
            default:
                return 0;
        }
    }

    public LinkedList getLasers()
    {
        return lasers;
    }

    @Override
    public void draw(Batch batch, float parentAlpha)
    {
        super.draw(batch, parentAlpha);
        batch.setColor(getColor().r, getColor().g, getColor().b, getColor().a * parentAlpha);

        TextureRegion currentFrame = animations[tilt].getKeyFrame(stateTime, true);
        batch.draw(currentFrame, getX(), getY());

        batch.end();
        if (DodgerGame.debug)
        {
            getCollisionBox();
            DodgerGame.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            DodgerGame.shapeRenderer.setColor(Color.GREEN);
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
        stateTime += delta;
        shootingTimer -= delta;
        handleInput(delta);
        removeOffScreenLasers();
        updateScore(delta);
        actualHealth = MathUtils.lerp(actualHealth, targetHealth, .15f);
    }

    public Polygon getCollisionBox()
    {
        collisionBox.setPosition(getX(), getY());
        return collisionBox;
    }

    private void handleInput(float delta)
    {
        // Show collision boxes
        if (Gdx.input.isKeyJustPressed(Input.Keys.F7))
            DodgerGame.debug = !DodgerGame.debug;

        // Movement to the left
        if ((Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) && !(Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)))
        {
            moveBy(-speed * delta, 0);
            setX(MathUtils.clamp(getX(), -getOffset(), Gdx.graphics.getWidth() - getWidth() + getOffset()));
            shoot();

            if ((Gdx.input.isKeyJustPressed(Input.Keys.LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.A)) && tilt > 0)
            {
                tiltTimer = 0;
                tilt--;
            }
            else
                tiltLeft(delta);

        }
        else if (tilt < 2)
            tiltRight(delta);

        // Movement to the right
        if ((Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) && !(Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)))
        {
            moveBy(speed * delta, 0);
            setX(MathUtils.clamp(getX(), -getOffset(), Gdx.graphics.getWidth() - getWidth() + getOffset()));
            shoot();

            if ((Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) || Gdx.input.isKeyJustPressed(Input.Keys.D)) && tilt < 4)
            {
                tiltTimer = 0;
                tilt++;
            }
            else
                tiltRight(delta);
        }
        else if (tilt > 2)
            tiltLeft(delta);

        // Both control keys are pressed
        if ((Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) && (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)))
            shoot();

        setX(MathUtils.clamp(getX(), -getOffset(), Gdx.graphics.getWidth() - getWidth() + getOffset()));
    }

    private void removeOffScreenLasers()
    {
        for (int j = 0; j < getLasers().getSize(); j++)
        {
            Laser laser = (Laser) getLasers().get(j);
            if (laser.isOffScreen())
            {
                laser.remove();
                getLasers().remove(laser);
            }
        }
    }

    private void updateScore(float delta)
    {
        scoreTimer -= delta;
        if (scoreTimer < 0)
        {
            scoreTimer = scoreDelay;
            score += scorePerSecond * scoreDelay;
        }
    }

    private void shoot()
    {
        if (shootingTimer < 0)
        {
            float[] pos = getLasersSpawnPos();
            Laser laser1 = new Laser(pos[0], pos[1]);
            getStage().addActor(laser1);
            lasers.add(laser1);

            Laser laser2 = new Laser(pos[2], pos[3]);
            getStage().addActor(laser2);
            lasers.add(laser2);

            shootingTimer = shootingDelay;
        }
    }

    private void tiltLeft(float delta)
    {
        tiltTimer -= delta;
        if (Math.abs(tiltTimer) > 0.2f && tilt > 0)
        {
            tiltTimer = 0;
            tilt--;
        }
        tiltCollisionBox();
    }

    private void tiltRight(float delta)
    {
        tiltTimer += delta;
        if (Math.abs(tiltTimer) > 0.2f && tilt < 4)
        {
            tiltTimer = 0;
            tilt++;
        }
        tiltCollisionBox();
    }

    public float getHealth()
    {
        return actualHealth;
    }

    public void decreaseHealthBy(float amount)
    {
        targetHealth -= amount;
    }

    public void addHealth(float amount)
    {
        targetHealth += amount;
        targetHealth = MathUtils.clamp(targetHealth, 0, 1);
    }

    public boolean isDead()
    {
        return actualHealth <= .01f;
    }

    public int getScore()
    {
        return (int) score;
    }

    public void addScore(float amount)
    {
        score += amount;
    }

    public void decreaseScoreBy(float amount)
    {
        score -= amount;
        if (score < 0)
            score = 0;
    }

    public float getScorePerSecond()
    {
        return scorePerSecond;
    }

    public void setScorePerSecond(float scorePerSecond)
    {
        this.scorePerSecond = scorePerSecond;
    }

    private float[] getLasersSpawnPos()
    {
        switch (tilt)
        {
            case 0:
                return new float[]{getX() + 11, getY() + 55, getX() + getWidth() - 10 - 14, getY() + 55};
            case 1:
                return new float[]{getX() + 10, getY() + 55, getX() + getWidth() - 10 - 11, getY() + 55};
            case 2:
                return new float[]{getX() + 11, getY() + 55, getX() + getWidth() - 10 - 11, getY() + 55};
            case 3:
                return new float[]{getX() + 14, getY() + 55, getX() + getWidth() - 10 - 12, getY() + 55};
            case 4:
                return new float[]{getX() + 17, getY() + 55, getX() + getWidth() - 10 - 12, getY() + 55};
            default:
                return new float[]{0, 0, 0, 0};
        }
    }

    public void increaseSpeedBy(int percent)
    {
        speed = speed + percent / 100f * speed;
    }

    public void increaseShootingSpeedBy(int percent)
    {
        shootingDelay = shootingDelay - percent / 100f * shootingDelay;
        Laser.speed = Laser.speed + percent / 100f * Laser.speed;
    }
}
