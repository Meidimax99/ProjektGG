package de.gg.engine.network.message;

public final class SuccessfulHandshakeResponse {

	private short clientNetworkId;

	public SuccessfulHandshakeResponse() {
		// default public constructor
	}

	public SuccessfulHandshakeResponse(short clientNetworkId) {
		this.clientNetworkId = clientNetworkId;
	}

	public short getClientNetworkId() {
		return clientNetworkId;
	}

}
