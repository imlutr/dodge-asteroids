package ro.luca.dodger.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import ro.luca.dodger.DodgerGame;

public class HighScoreScreen extends BaseScreen
{

    public HighScoreScreen(DodgerGame game)
    {
        super(game);
    }

    @Override
    public void show()
    {
        super.show();

        int highscore = game.preferences.getInteger("highscore");
        Label label = new Label("HIGHSCORE: " + highscore + "\n\n\n" +
                "PRESS SPACE TO GO BACK TO THE" + "\n\n" +
                "MAIN MENU", game.labelStyleMedium);
        label.setPosition(68, 600 / 2 - label.getHeight() / 2);
        stage.addActor(label);
    }

    @Override
    public void render(float delta)
    {
        update(delta);
        Gdx.gl.glClearColor(game.bgColor.r, game.bgColor.g, game.bgColor.b, game.bgColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        super.render(delta);
        stage.draw();
    }

    private void update(float delta)
    {
        stage.act(delta);
        handleInput();
    }

    private void handleInput()
    {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
        {
            game.tapSound.play();
            game.setScreen(game.menuScreen);
        }
    }
}
