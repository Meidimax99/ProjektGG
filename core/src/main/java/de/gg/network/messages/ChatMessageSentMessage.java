package de.gg.network.messages;

/**
 * This message is sent after a chat message got written.
 */
public class ChatMessageSentMessage {

	/**
	 * The sending player's ID.
	 */
	private short senderId;
	/**
	 * The actual message.
	 */
	private String message;

	public ChatMessageSentMessage() {
		// default public constructor
	}

	public ChatMessageSentMessage(short senderId, String message) {
		this.senderId = senderId;
		this.message = message;
	}

	public short getSenderId() {
		return senderId;
	}

	public String getMessage() {
		return message;
	}

}