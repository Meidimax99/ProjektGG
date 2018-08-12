package de.gg.game.system.server;

import java.util.Random;

import de.gg.game.entity.Character;
import de.gg.game.entity.Player;
import de.gg.game.world.City;
import de.gg.network.rmi.AuthoritativeResultListener;

/**
 * This system processes after 60 seconds and takes care of the first wave of
 * events on the server side for characters.
 */
public class FirstCharacterEventWaveServerSystem
		extends ServerProcessingSystem<Character> {

	private Random random;
	private City city;

	public FirstCharacterEventWaveServerSystem(
			AuthoritativeResultListener resultListener) {
		super(resultListener);
	}

	@Override
	public void init(City city, long seed) {
		this.random = new Random(seed);
		this.city = city;
	}

	@Override
	public void process(short id, Character c) {
		// DEATH
		if (c.getHp() <= 0) {
			// Player characters:
			Player p = city.getPlayerByCharacterId(id);
			if (p != null) {
				// TODO Charakter tauschen, Erbe, Illness-Reset, Family-Reset
			}

			// Player siblings:
			// TODO Erbe

			// Other characters:
			// TODO replace character by random new one

			// For everyone:
			// TODO Ämter resetten, Character aus Liste entfernen

			resultListener.onCharacterDeath(id);

			return;
		}
	}

	@Override
	public boolean isProcessedContinuously() {
		return false;
	}

	@Override
	public int getTickRate() {
		return 600;
	}

}
