package ro.luca.dodger.actors.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;

import ro.luca.dodger.DodgerGame;
import ro.luca.dodger.actors.Asteroid;
import ro.luca.dodger.actors.Ship;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel;

public abstract class Effect extends Actor
{
    private Animation<TextureRegion> animation;
    private float stateTime;
    private Polygon collisionBox = new Polygon();

    Effect(Texture sheet, float x)
    {
        setSize(40, 40);
        setPosition(x, Gdx.graphics.getHeight());
        TextureRegion[][] tmp = TextureRegion.split(sheet, sheet.getWidth() / 2, sheet.getHeight());
        animation = new Animation<TextureRegion>(.5f, tmp[0]);

        float[] vertices = new float[]{
                12, 0,
                27, 0,
                40, 14,
                40, 28,
                27, 40,
                12, 40,
                0, 28,
                0, 14
        };
        collisionBox.setVertices(vertices);
    }

    void take(Ship ship, String text, Label.LabelStyle labelStyle)
    {
        Label label = new Label(text, labelStyle);
        label.setAlignment(Align.center, Align.center);
        label.setPosition(ship.getX() + ship.getWidth() / 2 - label.getPrefWidth() / 2, ship.getY() + ship.getHeight() / 2 + 40);
        label.setX(MathUtils.clamp(label.getX(), 0, 600 - label.getPrefWidth()));
        ship.getStage().addActor(label);
        label.addAction(parallel(Actions.moveBy(0, 80, 2f), fadeOut(2f)));
    }

    @Override
    public void draw(Batch batch, float parentAlpha)
    {
        super.draw(batch, parentAlpha);
        batch.setColor(getColor().r, getColor().g, getColor().b, getColor().a * parentAlpha);

        TextureRegion currentFrame = animation.getKeyFrame(stateTime, true);
        batch.draw(currentFrame, getX(), getY(), getWidth(), getHeight());

        batch.end();
        if (DodgerGame.debug)
        {
            getCollisionBox();
            DodgerGame.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            DodgerGame.shapeRenderer.setColor(Color.YELLOW);
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
        moveBy(0, -Asteroid.getSpeed() * delta);
    }

    public Polygon getCollisionBox()
    {
        collisionBox.setPosition(getX(), getY());
        return collisionBox;
    }

    public boolean isOffScreen()
    {
        return getY() + getHeight() < 0;
    }
}
