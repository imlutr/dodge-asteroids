package ro.luca.dodger.actors.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;

import ro.luca.dodger.DodgerGame;
import ro.luca.dodger.actors.Asteroid;
import ro.luca.dodger.actors.Ship;
import ro.luca.dodger.screens.PlayScreen;

public class Debuff extends Effect
{
    private static Sound collectSound;
    private Ship ship;

    public Debuff(Ship ship)
    {
        super(PlayScreen.debuffTexture, ship.getX());
        this.ship = ship;
        if (collectSound == null)
            collectSound = Gdx.audio.newSound(Gdx.files.internal("audio/collect-debuff.mp3"));
    }

    public void take()
    {
        collectSound.play(1);
        String text = "";

        int random = MathUtils.random(1, 5);
        if (random == 1) // Decrease score
        {
            int amount = MathUtils.random(6, 12) * 100;
            ship.decreaseScoreBy(amount);
            text = "-" + amount + " SCORE";
        }
        else if (random == 2) // Make asteroids bigger
        {
            int percent = MathUtils.random(6, 12);
            Asteroid.makeAsteroidsSmallerBy(-percent);
            text = percent + "% BIGGER\nASTEROIDS";
        }
        else if (random == 3) // Decrease the ship's steering speed
        {
            int percent = MathUtils.random(2, 3) * 10;
            ship.increaseSpeedBy(-percent);
            text = "-" + percent + "% STEERING\nSPEED";
        }
        else if (random == 4) // Decrease the ship's shooting speed
        {
            int percent = MathUtils.random(5, 15);
            ship.increaseShootingSpeedBy(-percent);
            text = "-" + percent + "% SHOOTING\nSPEED";
        }
        else if (random == 5) // Increase the amount of damage taken
        {
            int percent = MathUtils.random(5, 15);
            Asteroid.setDamagePercent(Asteroid.getDamagePercent() + percent / 100f);
            text = "-" + percent + "% DEFENSE";
        }

        super.take(ship, text, DodgerGame.labelStyleSmallRed);
    }
}
