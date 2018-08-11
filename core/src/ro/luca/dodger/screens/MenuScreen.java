package ro.luca.dodger.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import ro.luca.dodger.DodgerGame;

public class MenuScreen extends BaseScreen
{
    private Music bgMusic;

    public MenuScreen(DodgerGame game)
    {
        super(game);

        bgMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/menu.mp3"));
        bgMusic.setLooping(true);

        Label keybindsLabel = new Label(
                "PRESS SPACE TO START THE GAME\n\n\n" +
                        "PRESS H TO SEE THE CURRENT\n\n" +
                        "HIGH SCORE", game.labelStyleMedium);
        keybindsLabel.setPosition(68, 600 / 2 - keybindsLabel.getHeight() / 2);
        stage.addActor(keybindsLabel);
    }

    @Override
    public void show()
    {
        super.show();
        bgMusic.play();
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
        handleInput();
    }

    private void handleInput()
    {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
        {
            bgMusic.stop();
            game.tapSound.play();
            game.setScreen(game.playScreen);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.H))
        {
            game.tapSound.play();
            game.setScreen(game.highScoreScreen);
        }
    }

    @Override
    public void dispose()
    {
        super.dispose();
        bgMusic.dispose();
    }
}
