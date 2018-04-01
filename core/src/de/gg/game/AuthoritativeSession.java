package de.gg.game;

import java.util.HashMap;

import de.gg.data.GameSessionSetup;
import de.gg.data.RoundEndData;
import de.gg.network.LobbyPlayer;
import de.gg.util.Log;
import de.gg.util.PlayerUtils;

/**
 * This class takes care of simulating the game session on the server side and
 * implements the {@linkplain SlaveActionHandler interface} used in the RMI for
 * the client.
 */
public class AuthoritativeSession extends GameSession
		implements
			SlaveActionListener {

	/**
	 * The network ID of the local player.
	 */
	private short localId;
	/**
	 * Set to true when a game round is over. The next round should start, when
	 * all players issued a ready message.
	 * 
	 * @see GameSession#setupNewRound(RoundEndData) Method to start the next
	 *      round
	 */
	private HashMap<Short, LobbyPlayer> players;
	private HashMap<Short, AuthoritativeResultListener> resultListeners;

	/**
	 * Creates a new multiplayer session.
	 * 
	 * @param sessionSetup
	 *            The settings of the game session.
	 * @param players
	 *            A hashmap containing the players.
	 */
	public AuthoritativeSession(GameSessionSetup sessionSetup,
			HashMap<Short, LobbyPlayer> players) {
		super(sessionSetup);

		this.players = players;

		// TODO Alle Spieler �ber die players-Liste in #city
		// aufsetzen

	}

	/**
	 * Starts the game session. {@link #update()} has to get called to update
	 * the session. To resume the game after a round ended
	 * {@link #setupNewRound(RoundEndData)} has to get called.
	 */
	public void startGame(
			HashMap<Short, AuthoritativeResultListener> resultListeners) {
		for (LobbyPlayer player : players.values()) {
			player.setReady(false);
		}

		this.resultListeners = resultListeners;
	}

	public void stopGame() {
		// TODO save the game
	}

	@Override
	public boolean readyUp(short networkId) {
		if (players.get(networkId).isReady()) {
			return false;
		}

		players.get(networkId).setReady(true);

		Log.info("Server", "Spieler %d ist f�r n�chste Runde bereit",
				networkId);

		if (waitingForNextRound) {
			tryToStartNextRoundForEveryone();
		}

		return true;
	}

	public void tryToStartNextRoundForEveryone() {
		if (PlayerUtils.areAllPlayersReady(players.values())) {
			Log.info("Server", "Alle Spieler sind f�r die Runde bereit");

			for (LobbyPlayer player : players.values()) {
				player.setReady(false);
			}

			// RoundEndData generieren
			RoundEndData data = new RoundEndData();

			// Alle Clienten informieren
			(new AuthoritativeResultListenerThread() {
				@Override
				protected void doStuff(
						AuthoritativeResultListener resultListener) {
					resultListener.onAllPlayersReadied(data);
				}
			}).start();

			// TODO Auch auf dem Server die neue Runde aufsetzen -> RoundEndData
			// anweden

			startNextRound();
		}
	}

	/**
	 * This thread takes care of informing every
	 * {@linkplain AuthoritativeSession#resultListeners result listener} on a
	 * thread separate of the one updating the server.
	 */
	abstract class AuthoritativeResultListenerThread extends Thread {
		@Override
		public void run() {
			for (AuthoritativeResultListener resultListener : resultListeners
					.values()) {
				doStuff(resultListener);
			}
		}

		protected abstract void doStuff(
				AuthoritativeResultListener resultListener);
	}

}
