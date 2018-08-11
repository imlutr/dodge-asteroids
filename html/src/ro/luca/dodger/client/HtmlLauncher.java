package ro.luca.dodger.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;

import ro.luca.dodger.DodgerGame;

public class HtmlLauncher extends GwtApplication
{

    @Override
    public GwtApplicationConfiguration getConfig()
    {
        return new GwtApplicationConfiguration(600, 600);
    }

    @Override
    public ApplicationListener createApplicationListener()
    {
        return new DodgerGame();
    }
}