package ro.luca.dodger.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import ro.luca.dodger.DodgerGame;

class BaseScreen implements Screen
{
    DodgerGame game;
    Stage stage;

    BaseScreen(DodgerGame game)
    {
        this.game = game;
        this.stage = new Stage(new ScreenViewport(), game.spriteBatch);
    }

    @Override
    public void show()
    {

    }

    @Override
    public void render(float delta)
    {

    }

    @Override
    public void resize(int width, int height)
    {

    }

    @Override
    public void pause()
    {

    }

    @Override
    public void resume()
    {

    }

    @Override
    public void hide()
    {

    }

    @Override
    public void dispose()
    {

    }
}
