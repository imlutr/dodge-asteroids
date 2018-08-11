package ro.luca.dodger.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;

import ro.luca.dodger.DodgerGame;

public class DesktopLauncher
{
    public static void main(String[] arg)
    {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 600;
        config.height = 600;
        config.resizable = false;
        config.title = "Dodge Asteroids";
        config.initialBackgroundColor = new Color(136 / 255f, 104 / 255f, 63 / 255f, 1);
        config.addIcon("textures/icon.png", Files.FileType.Internal);
        new LwjglApplication(new DodgerGame(), config);
    }
}
