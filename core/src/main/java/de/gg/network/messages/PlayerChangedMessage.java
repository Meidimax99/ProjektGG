package de.gg.network.messages;

import de.gg.network.LobbyPlayer;

/**
 * This message is sent when a player was changed.
 */
public class PlayerChangedMessage {

	/**
	 * The changed player's ID.
	 */
	private short id;
	/**
	 * The changed player.
	 */
	private LobbyPlayer player;

	public PlayerChangedMessage() {
		// default public constructor
	}

	public PlayerChangedMessage(short id, LobbyPlayer player) {
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