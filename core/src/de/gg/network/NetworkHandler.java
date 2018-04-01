package de.gg.network;

import java.io.IOException;
import java.net.DatagramPacket;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.ClientDiscoveryHandler;
import com.esotericsoftware.kryonet.Listener.TypeListener;
import com.esotericsoftware.kryonet.rmi.ObjectSpace;
import com.google.common.base.Preconditions;
import com.google.common.eventbus.EventBus;

import de.gg.core.ProjektGG;
import de.gg.data.GameSessionSetup;
import de.gg.event.ConnectionEstablishedEvent;
import de.gg.event.NewChatMessagEvent;
import de.gg.event.PlayerChangedEvent;
import de.gg.event.PlayerConnectedEvent;
import de.gg.event.PlayerDisconnectedEvent;
import de.gg.game.AuthoritativeResultListener;
import de.gg.game.SlaveActionListener;
import de.gg.network.GameServer.IHostCallback;
import de.gg.network.message.ChatMessageSentMessage;
import de.gg.network.message.DiscoveryResponsePacket;
import de.gg.network.message.GameSetupMessage;
import de.gg.network.message.PlayerChangedMessage;
import de.gg.network.message.PlayerJoinedMessage;
import de.gg.network.message.PlayerLeftMessage;
import de.gg.util.Log;

/**
 * This class takes care of handling the networking part of the game. It holds
 * an instance of the used kryonet {@linkplain #client client} and the
 * {@linkplain #server game server} (if the client is also the
 * {@linkplain #isHost() host}).
 * <p>
 * It is further responsible for relaying all user actions to the server.
 */
public class NetworkHandler {

	public static final int DEFAULT_PORT = 55678;
	public static final int UDP_DISCOVER_PORT = 54678;
	private EventBus eventBus;
	private Client client;
	private GameServer server;
	/**
	 * The network ID of the local player.
	 */
	private short localClientId;
	private SlaveActionListener actionListener;

	public NetworkHandler(EventBus eventBus) {
		Preconditions.checkNotNull(eventBus, "Event handler cannot be null.");

		this.eventBus = eventBus;
	}

	/**
	 * Connects the client to the server. After it is finished a
	 * {@link ConnectionEstablishedEvent} is posted on the
	 * {@linkplain ProjektGG#getEventBus() event bus}.
	 * 
	 * @param ip
	 *            The server's ip.
	 * @param port
	 *            The server's port.
	 */
	public void setUpConnectionAsClient(String ip, int port) {
		client = new Client();
		client.start();

		NetworkRegisterer.registerClasses(client.getKryo());

		TypeListener listener = new TypeListener();
		// GAME SETUP MESSAGE (ON CLIENT CONNECT)
		listener.addTypeHandler(GameSetupMessage.class, (con, msg) -> {
			eventBus.post(new ConnectionEstablishedEvent(msg.getPlayers(),
					msg.getId(), msg.getSettings()));
			localClientId = msg.getId();
			Log.info("Client", "Netzwerk ID: %d", localClientId);
		});
		// NEW CHAT MESSAGE
		listener.addTypeHandler(ChatMessageSentMessage.class, (con, msg) -> {
			eventBus.post(new NewChatMessagEvent(msg.getSenderId(),
					msg.getMessage()));
		});
		// PLAYER CHANGED
		listener.addTypeHandler(PlayerChangedMessage.class, (con, msg) -> {
			eventBus.post(new PlayerChangedEvent(msg.getId(), msg.getPlayer()));
		});
		// PLAYER JOINED
		listener.addTypeHandler(PlayerJoinedMessage.class, (con, msg) -> {
			eventBus.post(
					new PlayerConnectedEvent(msg.getId(), msg.getPlayer()));
		});
		// PLAYER LEFT
		listener.addTypeHandler(PlayerLeftMessage.class, (con, msg) -> {
			eventBus.post(new PlayerDisconnectedEvent(msg.getId()));
		});
		client.addListener(listener);

		final Thread connectingThread = new Thread(new Runnable() {
			public void run() {
				try {
					client.connect(6000, ip, port);
					Log.info("Client", "Lobby beigetreten");
					// Das Event hierf�r wird beim Empfangen des Game Setups
					// gepostet
				} catch (IOException e) {
					Log.error("Client", "Fehler beim Betreten der Lobby: ", e);
					eventBus.post(new ConnectionEstablishedEvent(e));
				}
			}
		});
		connectingThread.start();
	}

	/**
	 * Sets up a server and a client asynchronously. After it is finished a
	 * {@link ConnectionEstablishedEvent} is posted on the
	 * {@linkplain ProjektGG#getEventBus() event bus}.
	 * 
	 * @param port
	 *            The used port.
	 * @param gameName
	 *            The name of the game.
	 * @see ClientNetworkHandler#setUpConnectionAsClient(String, int)
	 */
	public void setUpConnectionAsHost(int port, String gameName,
			GameSessionSetup setup) {
		server = new GameServer(port, gameName, setup, new IHostCallback() {
			@Override
			public void onHostStarted(IOException e) {
				if (e == null) {
					setUpConnectionAsClient("localhost", port);
				} else {
					eventBus.post(new ConnectionEstablishedEvent(e));
				}
			}
		});
	}

	public void updateServer() {
		if (isHost())
			server.update();
	}

	/**
	 * @return Whether this player is also hosting the server.
	 */
	public boolean isHost() {
		return server != null;
	}

	/**
	 * Disconnects the client.
	 */
	public void disconnect() {
		client.close();
		if (isHost())
			server.stop();
	}

	/**
	 * Sends an object to the server.
	 * 
	 * @param obj
	 *            The object.
	 */
	public void sendObject(Object obj) {
		client.sendTCP(obj);
	}

	/**
	 * Sends a chat message to the server.
	 * 
	 * @param message
	 */
	public void sendChatMessage(String message) {
		sendObject(new ChatMessageSentMessage(localClientId, message));
	}

	/**
	 * Updates the player on the server.
	 */
	public void onLocalPlayerChange(LobbyPlayer player) {
		sendObject(new PlayerChangedMessage(localClientId, player));
	}

	/**
	 * ...
	 */
	public void startGame(AuthoritativeResultListener resultListener) {
		ObjectSpace.registerClasses(client.getKryo());
		ObjectSpace objectSpace = new ObjectSpace();
		objectSpace.register(localClientId, resultListener);
		objectSpace.addConnection(client);

		SlaveActionListener actionListener = ObjectSpace.getRemoteObject(client,
				254, SlaveActionListener.class);

		if (actionListener == null)
			Log.error("Client", "Der actionListener des Spielers %d ist null",
					localClientId);

		this.actionListener = actionListener;
	}

	public boolean readyUp() {
		return actionListener.readyUp(localClientId);
	}

	public void discoverHosts(HostDiscoveryListener listener) {
		Client c = new Client();
		c.getKryo().register(DiscoveryResponsePacket.class);
		c.setDiscoveryHandler(new ClientDiscoveryHandler() {
			@Override
			public DatagramPacket onRequestNewDatagramPacket() {
				byte[] buffer = new byte[1024];
				return new DatagramPacket(buffer, buffer.length);
			}

			@Override
			public void onDiscoveredHost(DatagramPacket datagramPacket) {
				DiscoveryResponsePacket packet = (DiscoveryResponsePacket) c
						.getKryo().readClassAndObject(
								new Input(datagramPacket.getData()));
				listener.onHostDiscovered(
						datagramPacket.getAddress().getHostAddress(), packet);
			}

			@Override
			public void onFinally() {
			}
		});
		c.discoverHosts(UDP_DISCOVER_PORT, 4500);
		c.close();
	}

	public interface HostDiscoveryListener {
		public void onHostDiscovered(String address,
				DiscoveryResponsePacket datagramPacket);
	}

}