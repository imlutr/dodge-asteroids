package ro.luca.dodger.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import ro.luca.dodger.DodgerGame;
import ro.luca.dodger.actors.Asteroid;
import ro.luca.dodger.actors.Explosion;
import ro.luca.dodger.actors.Laser;
import ro.luca.dodger.actors.Ship;
import ro.luca.dodger.actors.effects.Buff;
import ro.luca.dodger.actors.effects.Debuff;
import ro.luca.dodger.actors.effects.Effect;
import ro.luca.dodger.util.LinkedList;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.after;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

public class PlayScreen extends BaseScreen
{
    public static Texture buffTexture, debuffTexture;

    private float bgSpeed, cloudSpeed;
    private float asteroidsSpawnTimer, increaseDifficultyDelay, increaseDifficultyTimer, effectDelay, effectsSpawnTimer;

    private Texture pixelTexture;
    private Image bg1, bg2, cloud1, cloud2, healthBar;
    private Ship ship;
    private LinkedList asteroids, effects;
    private Label controlsLabel, healthLabel, scoreLabel, fpsLabel;

    private Music bgMusic;

    public PlayScreen(DodgerGame game)
    {
        super(game);

        PlayScreen.buffTexture = new Texture("textures/buff.png");
        PlayScreen.debuffTexture = new Texture("textures/debuff.png");

        Texture bgTexture = new Texture("textures/background.png");
        bg1 = new Image(bgTexture);
        bg2 = new Image(bgTexture);

        Texture cloudTexture = new Texture("textures/clouds.png");
        cloud1 = new Image(cloudTexture);
        cloud2 = new Image(cloudTexture);

        ship = new Ship(40);
        asteroids = new LinkedList();
        effects = new LinkedList();

        pixelTexture = new Texture("textures/pixel.png");
        healthBar = new Image(pixelTexture);
        healthBar.setPosition(0, 0);
        healthBar.setColor(game.fontColor);

        controlsLabel = new Label("MOVE WITH LEFT-RIGHT / A-D", game.labelStyleMedium);
        controlsLabel.setPosition(92, 465);
        healthLabel = new Label("HEALTH", game.labelStyleMedium);
        healthLabel.setPosition(3, 13);
        scoreLabel = new Label("0", game.labelStyleBig);
        scoreLabel.setY(500);
        fpsLabel = new Label("", game.labelStyleMedium);
        fpsLabel.setY(591);

        bgMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/play.mp3"));
        bgMusic.setLooping(true);
    }

    @Override
    public void show()
    {
        super.show();

        stage.clear();

        bgSpeed = 50;
        bg1.setY(0);
        stage.addActor(bg1);
        bg2.setY(bg1.getY() + bg1.getHeight());
        stage.addActor(bg2);

        cloudSpeed = 25;
        cloud1.setY(100);
        stage.addActor(cloud1);
        cloud2.setY(cloud1.getY() + cloud1.getHeight() + 300);
        stage.addActor(cloud2);

        effects.removeAll();
        effectsSpawnTimer = 7;
        effectDelay = 4;

        asteroids.removeAll();
        Asteroid.setSpeed(200);
        Asteroid.minSpawnDelay = .1f;
        Asteroid.maxSpawnDelay = .2f;
        asteroidsSpawnTimer = 2.5f;

        ship.reset();
        stage.addActor(ship);

        increaseDifficultyTimer = 0;
        increaseDifficultyDelay = 5;

        healthBar.setSize(Gdx.graphics.getWidth(), 10);
        stage.addActor(healthBar);
        stage.addActor(healthLabel);

        stage.addActor(scoreLabel);

        stage.addActor(fpsLabel);
        stage.addActor(controlsLabel);

        bgMusic.setVolume(2);
        bgMusic.play();

        stage.addAction(fadeOut(0));
        stage.addAction(fadeIn(1));
        controlsLabel.addAction(after(sequence(delay(5), fadeOut(1))));
        healthLabel.addAction(after(sequence(delay(5), fadeOut(1))));
    }

    @Override
    public void render(float delta)
    {
        update(delta);
        Gdx.gl.glClearColor(game.bgColor.r, game.bgColor.g, game.bgColor.b, game.bgColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
    }

    private void update(float delta)
    {
        stage.act(delta);
        updateBackground(delta);
        updateClouds(delta);
        spawnEffects(delta);
        spawnAsteroids(delta);
        removeOffScreenAsteroids();
        removeOffScreenEffects();
        handleCollisions();
        increaseDifficulty(delta);
        updateHealth();
        updateScoreLabel();
        updateFpsLabel();
    }

    private void updateBackground(float delta)
    {
        bg1.moveBy(0, -bgSpeed * delta);
        bg2.moveBy(0, -bgSpeed * delta);
        if (bg1.getY() + bg1.getHeight() < 0)
            bg1.setY(bg2.getY() + bg2.getHeight());
        if (bg2.getY() + bg2.getHeight() < 0)
            bg2.setY(bg1.getY() + bg1.getHeight());
    }

    private void updateClouds(float delta)
    {
        cloud1.toFront();
        cloud2.toFront();
        cloud1.addAction(moveBy(0, -cloudSpeed * delta));
        cloud2.addAction(moveBy(0, -cloudSpeed * delta));
        if (cloud1.getY() + cloud1.getHeight() < 0)
            cloud1.setY(cloud2.getY() + cloud2.getHeight() + 300);
        if (cloud2.getY() + cloud2.getHeight() < 0)
            cloud2.setY(cloud1.getY() + cloud1.getHeight() + 300);
    }

    private void spawnEffects(float delta)
    {
        effectsSpawnTimer -= delta;
        if (effectsSpawnTimer < 0)
        {
            effectsSpawnTimer = effectDelay;
            int random = MathUtils.random(1, 3);
            if (random == 1)
            {
                Debuff debuff = new Debuff(ship);
                effects.add(debuff);
                stage.addActor(debuff);
            }
            else
            {
                Buff buff = new Buff(ship);
                effects.add(buff);
                stage.addActor(buff);
            }
            healthBar.toFront();
            scoreLabel.toFront();
            controlsLabel.toFront();
            fpsLabel.toFront();
        }
    }

    private void spawnAsteroids(float delta)
    {
        asteroidsSpawnTimer -= delta;
        if (asteroidsSpawnTimer < 0)
        {
            Asteroid asteroid = new Asteroid();
            asteroids.add(asteroid);
            stage.addActor(asteroid);

            // Put further asteroids under closer asteroids
            asteroid.toBack();
            ship.toBack();
            bg1.toBack();
            bg2.toBack();

            asteroidsSpawnTimer = MathUtils.random(Asteroid.minSpawnDelay, Asteroid.maxSpawnDelay);
        }
    }

    private void removeOffScreenAsteroids()
    {
        for (int i = 0; i < asteroids.getSize(); i++)
        {
            Asteroid asteroid = (Asteroid) asteroids.get(i);
            if (asteroid.isOffScreen())
            {
                asteroid.remove();
                asteroids.remove(asteroid);
            }
        }
    }

    private void removeOffScreenEffects()
    {
        for (int i = 0; i < effects.getSize(); i++)
        {
            Effect effect = (Effect) effects.get(i);
            if (effect.isOffScreen())
            {
                effect.remove();
                effects.remove(effect);
            }
        }
    }

    private void handleCollisions()
    {
        for (int i = 0; i < asteroids.getSize(); i++)
        {
            Asteroid asteroid = (Asteroid) asteroids.get(i);

            // Ship collides with asteroid
            if (Intersector.overlapConvexPolygons(ship.getCollisionBox(), asteroid.getCollisionBox()))
            {
                Explosion explosion = new Explosion(asteroid.getX(), asteroid.getY(), asteroid.getWidth(), asteroid.getHeight(), Asteroid.getSpeed());
                stage.addActor(explosion);
                ship.decreaseHealthBy(asteroid.getDamage());
                asteroid.remove();
                asteroids.remove(asteroid);
            }
            else
                for (int j = 0; j < ship.getLasers().getSize(); j++)
                {
                    Laser laser = (Laser) ship.getLasers().get(j);

                    // Laser collides with asteroid
                    if (Intersector.overlapConvexPolygons(laser.getCollisionBox(), asteroid.getCollisionBox()))
                    {
                        Explosion explosion = new Explosion(asteroid.getX(), asteroid.getY(), asteroid.getWidth(), asteroid.getHeight(), Asteroid.getSpeed());
                        stage.addActor(explosion);

                        ship.addScore(asteroid.getScore());
                        Label scoreObtained = new Label("+" + (int) asteroid.getScore() + "", game.labelStyleMedium);
                        scoreObtained.setPosition(asteroid.getX() + asteroid.getWidth() / 2 - scoreObtained.getPrefWidth() / 2, asteroid.getY() + asteroid.getHeight() / 2);
                        stage.addActor(scoreObtained);
                        scoreObtained.addAction(parallel(moveBy(0, -Asteroid.getSpeed() + 20, 1.5f), fadeOut(1.5f)));

                        asteroid.remove();
                        asteroids.remove(asteroid);
                        laser.remove();
                        ship.getLasers().remove(laser);
                        break;
                    }
                }
        }

        for (int i = 0; i < effects.getSize(); i++)
        {
            Effect effect = (Effect) effects.get(i);

            // Ship collides with effect
            if (Intersector.overlapConvexPolygons(ship.getCollisionBox(), effect.getCollisionBox()))
            {
                if (effect.getClass() == Buff.class)
                    ((Buff) effect).take();
                else if (effect.getClass() == Debuff.class)
                    ((Debuff) effect).take();
                effects.remove(effect);
                effect.remove();
            }
        }
    }

    private void increaseDifficulty(float delta)
    {
        increaseDifficultyTimer -= delta;
        if (increaseDifficultyTimer < 0)
        {
            bgSpeed += 10;
            cloudSpeed += 10;
            Asteroid.setSpeed(Asteroid.getSpeed() + 10);
            if (Asteroid.minSpawnDelay > .003f)
            {
                Asteroid.minSpawnDelay -= .003f;
                Asteroid.maxSpawnDelay -= .003f;
            }

            increaseDifficultyTimer = increaseDifficultyDelay;
        }
    }

    private void updateHealth()
    {
        healthBar.setWidth(Gdx.graphics.getWidth() * ship.getHealth());
        if (ship.isDead())
        {
            bgMusic.stop();
            game.gameOverScreen.setScore(ship.getScore());
            game.setScreen(game.gameOverScreen);
        }
    }

    private void updateScoreLabel()
    {
        scoreLabel.toFront();
        scoreLabel.setText(ship.getScore() + "");
        scoreLabel.setX(600 / 2 - scoreLabel.getPrefWidth() / 2);
    }

    private void updateFpsLabel()
    {
        fpsLabel.setVisible(DodgerGame.debug);
        fpsLabel.setText(Gdx.graphics.getFramesPerSecond() + "");
        fpsLabel.setX(600 - fpsLabel.getPrefWidth());
    }

    @Override
    public void dispose()
    {
        super.dispose();
        Asteroid.dispose();
        Laser.dispose();
        pixelTexture.dispose();
    }
}
