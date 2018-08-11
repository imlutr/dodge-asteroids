package ro.luca.dodger;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;

import ro.luca.dodger.screens.GameOverScreen;
import ro.luca.dodger.screens.HighScoreScreen;
import ro.luca.dodger.screens.MenuScreen;
import ro.luca.dodger.screens.PlayScreen;

public class DodgerGame extends Game
{
    public static boolean debug = false;
    public static ShapeRenderer shapeRenderer;
    public static LabelStyle labelStyleSmallGreen, labelStyleSmallRed;

    public SpriteBatch spriteBatch;
    public Color bgColor, fontColor;
    public LabelStyle labelStyleMedium, labelStyleBig;
    public Preferences preferences;
    public Sound tapSound;

    public MenuScreen menuScreen;
    public HighScoreScreen highScoreScreen;
    public PlayScreen playScreen;
    public GameOverScreen gameOverScreen;

    @Override
    public void create()
    {
        shapeRenderer = new ShapeRenderer();
        DodgerGame.labelStyleSmallGreen = new LabelStyle(new BitmapFont(Gdx.files.internal("fonts/font-small.fnt")), new Color(12 / 255f, 250 / 255f, 23 / 255f, 1));
        DodgerGame.labelStyleSmallRed = new LabelStyle(labelStyleSmallGreen.font, new Color(255 / 255f, 0 / 255f, 0 / 255f, 1));

        spriteBatch = new SpriteBatch();
        bgColor = new Color(136 / 255f, 104 / 255f, 63 / 255f, 1);
        fontColor = new Color(222 / 255f, 199 / 255f, 170 / 255f, 1);
        labelStyleMedium = new LabelStyle(new BitmapFont(Gdx.files.internal("fonts/font-medium.fnt")), fontColor);
        labelStyleBig = new LabelStyle(new BitmapFont(Gdx.files.internal("fonts/font-big.fnt")), fontColor);
        preferences = Gdx.app.getPreferences("Dodge Asteroids");
        tapSound = Gdx.audio.newSound(Gdx.files.internal("audio/tap.mp3"));

        menuScreen = new MenuScreen(this);
        highScoreScreen = new HighScoreScreen(this);
        playScreen = new PlayScreen(this);
        gameOverScreen = new GameOverScreen(this);

        setScreen(menuScreen);
    }

    @Override
    public void dispose()
    {
        labelStyleMedium.font.dispose();
    }
}
