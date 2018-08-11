package ro.luca.dodger.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.removeActor;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

public class Explosion extends Actor
{
    private static Sound explosion;
    private Animation<TextureRegion> animation;
    private float stateTime;
    private float speed;

    public Explosion(float x, float y, float width, float height, float speed)
    {
        if (explosion == null)
            explosion = Gdx.audio.newSound(Gdx.files.internal("audio/explosion.mp3"));
        setSize(width, height);
        setPosition(x, y);

        Texture explosionSheet = new Texture("textures/explosion.png");
        TextureRegion[][] tmp = TextureRegion.split(explosionSheet, explosionSheet.getWidth() / 5, explosionSheet.getHeight());
        animation = new Animation<TextureRegion>(.075f, tmp[0]);
        addAction(sequence(delay(animation.getAnimationDuration()), removeActor()));
        this.speed = speed;

        explosion.play(1);
    }

    @Override
    public void draw(Batch batch, float parentAlpha)
    {
        super.draw(batch, parentAlpha);
        batch.setColor(getColor().r, getColor().g, getColor().b, getColor().a * parentAlpha);

        TextureRegion currentFrame = animation.getKeyFrame(stateTime, false);
        batch.draw(currentFrame, getX(), getY(), getWidth(), getHeight());
    }

    @Override
    public void act(float delta)
    {
        super.act(delta);
        stateTime += delta;
        moveBy(0, -speed * delta);
    }
}
