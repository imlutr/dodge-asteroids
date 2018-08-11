package ro.luca.dodger.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import ro.luca.dodger.DodgerGame;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;

public class GameOverScreen extends BaseScreen
{
    private Label scoreLabel;
    private Music bgMusic;
    private int score;

    public GameOverScreen(DodgerGame game)
    {
        super(game);

        Label gameOverLabel = new Label("GAME OVER", game.labelStyleBig);
        gameOverLabel.setPosition(142, 450);
        stage.addActor(gameOverLabel);

        scoreLabel = new Label("", game.labelStyleMedium);
        scoreLabel.setPosition(68, 325);
        stage.addActor(scoreLabel);

        Label infoLabel = new Label(
                "PRESS SPACE TO GO BACK TO THE\n\n" +
                        "MAIN MENU", game.labelStyleMedium);
        infoLabel.setPosition(68, scoreLabel.getY() - infoLabel.getHeight() - scoreLabel.getPrefHeight() * 2);
        stage.addActor(infoLabel);

        bgMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/game-over.mp3"));
    }

    @Override
    public void show()
    {
        super.show();

        scoreLabel.setText("SCORE: " + score);
        int highscore = game.preferences.getInteger("highscore");
        if (score > highscore)
        {
            scoreLabel.setText(scoreLabel.getText() + " (NEW BEST)");
            game.preferences.putInteger("highscore", score);
            game.preferences.flush();
        }

        stage.addAction(fadeOut(0));
        stage.addAction(fadeIn(1));

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
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
        {
            bgMusic.stop();
            game.tapSound.play();
            game.setScreen(game.menuScreen);
        }
    }

    void setScore(int score)
    {
        this.score = score;
    }
}
