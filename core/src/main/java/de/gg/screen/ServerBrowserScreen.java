package de.gg.screen;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.google.common.eventbus.Subscribe;

import de.gg.event.ConnectionEstablishedEvent;
import de.gg.event.ConnectionFailedEvent;
import de.gg.input.ButtonClickListener;
import de.gg.network.GameClient;
import de.gg.network.GameServer;
import de.gg.network.ServerDiscoveryHandler;
import de.gg.network.ServerDiscoveryHandler.HostDiscoveryListener;
import de.gg.network.message.DiscoveryResponsePacket;
import de.gg.ui.AnimationlessDialog;
import de.gg.ui.OffsetableTextField;
import net.dermetfan.gdx.assets.AnnotationAssetManager.Asset;

public class ServerBrowserScreen extends BaseUIScreen {

	@Asset(Texture.class)
	private final String BACKGROUND_IMAGE_PATH = "ui/backgrounds/town.jpg";
	@Asset(Texture.class)
	private final String TICK_IMAGE_PATH = "ui/icons/ready.png";
	private Dialog connectingDialog;
	private Table serverTable;
	/**
	 * This list holds all local LAN servers that were discovered.
	 */
	private List<String> dicoveredServers = new ArrayList<>();
	private Runnable discoveryThread;

	@Override
	protected void initUI() {
		backgroundTexture = assetManager.get(BACKGROUND_IMAGE_PATH);

		serverTable = new Table();

		ScrollPane pane = new ScrollPane(serverTable);

		ImageTextButton backButton = new ImageTextButton("Zurück", skin,
				"small");
		backButton.addListener(
				new ButtonClickListener(assetManager, game.getSettings()) {
					@Override
					protected void onClick() {
						game.pushScreen("mainMenu");
					}
				});

		ImageTextButton createLobbyButton = new ImageTextButton(
				"Spiel erstellen", skin, "small");
		createLobbyButton.addListener(
				new ButtonClickListener(assetManager, game.getSettings()) {
					@Override
					protected void onClick() {
						game.pushScreen("lobbyCreation");
					}
				});

		ImageTextButton directConnectButton = new ImageTextButton(
				"Direkt verbinden", skin, "small");
		directConnectButton.addListener(
				new ButtonClickListener(assetManager, game.getSettings()) {
					@Override
					protected void onClick() {
						OffsetableTextField portInputField = new OffsetableTextField(
								String.valueOf(GameServer.DEFAULT_PORT), skin,
								5);
						portInputField.setTextFieldFilter(
								new TextField.TextFieldFilter.DigitsOnlyFilter());
						OffsetableTextField ipInputField = new OffsetableTextField(
								"127.0.0.1", skin, 5);

						AnimationlessDialog dialog = new AnimationlessDialog(
								"Direkt verbinden", skin) {
							public void result(Object obj) {
								if ((Boolean) obj) {
									// Connect to client
									game.setClient(new GameClient(
											game.getEventBus(),
											game.getVersion(),
											ipInputField.getText(),
											Integer.valueOf(
													portInputField.getText())));

									connectingDialog = showInfoDialog(
											"Verbinden...",
											"Spiel beitreten...", false);
								}
							}
						};
						dialog.text("IP: ").button("Zurück", false)
								.button("Verbinden", true).key(Keys.ENTER, true)
								.key(Keys.ESCAPE, false);
						dialog.getContentTable().add(ipInputField).width(170)
								.row();
						dialog.getContentTable().add(new Label("Port:", skin));
						dialog.getContentTable().add(portInputField).width(90)
								.left();
						dialog.show(stage);
					}
				});

		Table buttonTable = new Table();
		buttonTable.add(backButton);
		buttonTable.add(createLobbyButton).width(132).padLeft(47);
		buttonTable.add(directConnectButton).width(152).padLeft(47);

		discoverLanServers();

		Table mTable = new Table();
		mTable.setWidth(615);
		mTable.setHeight(475);
		mTable.setBackground(skin.getDrawable("parchment2"));
		mTable.add(pane).width(580).height(405).row();
		mTable.add(buttonTable).height(50).bottom();

		mainTable.add(mTable);
	}

	/**
	 * Discovers available servers in the local network and adds them to the ui.
	 */
	private void discoverLanServers() {
		serverTable.clear();
		dicoveredServers.clear();
		discoveryThread = new Runnable() {
			@Override
			public void run() {
				ServerDiscoveryHandler serverDiscoveryHandler = new ServerDiscoveryHandler();
				serverDiscoveryHandler.discoverHosts(
						GameServer.UDP_DISCOVER_PORT,
						new HostDiscoveryListener() {
							@Override
							public void onHostDiscovered(String address,
									DiscoveryResponsePacket datagramPacket) {
								if (!dicoveredServers
										.contains(datagramPacket.getGameName()
												+ datagramPacket.getPort())) {
									dicoveredServers
											.add(datagramPacket.getGameName()
													+ datagramPacket.getPort());
									addServerToUI(serverTable, address,
											datagramPacket);
								}
							}
						});
			}
		};
		(new Thread(discoveryThread)).start();
	}

	private void addServerToUI(Table serverTable, String address,
			DiscoveryResponsePacket packet) {
		ImageTextButton joinButton = new ImageTextButton("Beitreten", skin,
				"small");
		joinButton.addListener(
				new ButtonClickListener(assetManager, game.getSettings()) {
					@Override
					protected void onClick() {
						game.setClient(new GameClient(game.getEventBus(),
								game.getVersion(), address, packet.getPort()));
						connectingDialog = showInfoDialog("Verbinden...",
								"Spiel beitreten...", false);
					}
				});

		serverTable.left().top()
				.add(new Image((Texture) assetManager.get(TICK_IMAGE_PATH)))
				.padRight(15).padLeft(12);
		String serverTitle = String.format("%s (%d/%d)", packet.getGameName(),
				packet.getPlayerCount(), packet.getMaxPlayerCount());
		serverTable.add(new Label(serverTitle, skin)).expandX();
		serverTable.add(joinButton).padRight(12);
		serverTable.row().padTop(20);
	}

	@Subscribe
	public void onClientConnected(ConnectionEstablishedEvent event) {
		((LobbyScreen) game.getScreen("lobby")).setupLobby(event);
		game.pushScreen("lobby");
	}

	@Subscribe
	public void onConnectionFailed(ConnectionFailedEvent event) {
		connectingDialog.setVisible(false);
		game.setClient(null);

		if (event.getException() != null)
			showInfoDialog("Fehler", event.getException().getMessage(), true);
		else
			showInfoDialog("Fehler",
					event.getServerRejectionMessage().getMessage(), true);
	}

}