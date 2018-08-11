package ro.luca.dodger.actors.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;

import ro.luca.dodger.DodgerGame;
import ro.luca.dodger.actors.Asteroid;
import ro.luca.dodger.actors.Ship;
import ro.luca.dodger.screens.PlayScreen;

public class Buff extends Effect
{
    private static Sound collectSound;
    private Ship ship;

    public Buff(Ship ship)
    {
        super(PlayScreen.buffTexture, MathUtils.random(-20, 580));
        this.ship = ship;
        if (collectSound == null)
            collectSound = Gdx.audio.newSound(Gdx.files.internal("audio/collect-buff.mp3"));
    }

    public void take()
    {
        collectSound.play(1);
        String text = "";

        int random = MathUtils.random(1, 7);
        if (random == 1) // Add HP
        {
            int healthToAdd = MathUtils.random(2, 4) * 10;
            ship.addHealth(healthToAdd / 100f);
            text = "+" + healthToAdd + " HP";
        }
        else if (random == 2) // Make asteroids smaller
        {
            int percent = MathUtils.random(3, 6);
            Asteroid.makeAsteroidsSmallerBy(percent);
            text = percent + "% SMALLER\nASTEROIDS";
        }
        else if (random == 3) // Add score
        {
            int scoreToAdd = MathUtils.random(7, 15) * 100;
            ship.addScore(scoreToAdd);
            text = "+" + scoreToAdd + " SCORE";
        }
        else if (random == 4) // Increase the score per second
        {
            int percent = MathUtils.random(2, 4) * 10;
            ship.setScorePerSecond(ship.getScorePerSecond() + percent / 100f * ship.getScorePerSecond());
            text = "+" + percent + "% SCORE\nPER SECOND";
        }
        else if (random == 5) // Increase the ship's steering speed
        {
            int percent = MathUtils.random(2, 3) * 10;
            ship.increaseSpeedBy(percent);
            text = "+" + percent + "% STEERING\nSPEED";
        }
        else if (random == 6) // Increase the ship's shooting speed
        {
            int percent = MathUtils.random(5, 10);
            ship.increaseShootingSpeedBy(percent);
            text = "+" + percent + "% SHOOTING\nSPEED";
        }
        else if (random == 7) // Decrease the amount of damage taken
        {
            int percent = MathUtils.random(5, 10);
            Asteroid.setDamagePercent(Asteroid.getDamagePercent() - percent / 100f);
            text = "+" + percent + "% DEFENSE";
        }

        super.take(ship, text, DodgerGame.labelStyleSmallGreen);
    }
}