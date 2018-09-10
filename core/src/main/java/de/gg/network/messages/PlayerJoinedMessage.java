package de.gg.network.messages;

import de.gg.network.LobbyPlayer;

/**
 * This message is sent after a new player joins a game.
 */
public class PlayerJoinedMessage {

	/**
	 * The joining player's ID.
	 */
	private short id;
	/**
	 * The joining player.
	 */
	private LobbyPlayer player;

	public PlayerJoinedMessage() {
		// default public constructor
	}

	public PlayerJoinedMessage(short id, LobbyPlayer player) {
		this.id = id;
		this.player = player;
	}

	public short getId() {
		return id;
	}

	public LobbyPlayer getPlayer() {
		return player;
	}

}