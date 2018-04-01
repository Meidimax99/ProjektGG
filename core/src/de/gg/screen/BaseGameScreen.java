package de.gg.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.google.common.eventbus.Subscribe;

import de.gg.event.NewChatMessagEvent;
import de.gg.event.PlayerDisconnectedEvent;

public abstract class BaseGameScreen extends BaseUIScreen {

	private final boolean updateGame;

	// TODO dieser Screen bekommt eine Player-Hashmap, um sich um alle
	// Join/Leave und Chat-Events k�mmern zu k�nnen (d.h. er kann f�r die
	// Netzwerk-IDs den jeweiligen Namen ermitteln)

	public BaseGameScreen(boolean updateGame) {
		this.updateGame = updateGame;
	}

	public BaseGameScreen() {
		this(true);
	}

	@Subscribe
	public void onNewChatMessage(NewChatMessagEvent event) {
		// TODO
	}

	@Subscribe
	public void onPlayerDisconnect(PlayerDisconnectedEvent event) {
		// TODO
	}

	@Override
	public void render(float delta) {
		if (updateGame) {
			if (game.getCurrentSession().update()) {
				game.pushScreen("roundEnd");
			}

			game.getNetworkHandler().updateServer();
		}

		Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g,
				backgroundColor.b, backgroundColor.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if (backgroundTexture != null) {
			game.getSpriteBatch().begin();
			game.getSpriteBatch()
					.setProjectionMatrix(game.getUICamera().combined);
			game.getSpriteBatch().draw(this.backgroundTexture, 0, 0,
					game.getViewportWidth(), game.getViewportHeight());
			game.getSpriteBatch().end();
		}

		renderGame(delta);

		stage.getBatch().setProjectionMatrix(game.getUICamera().combined);
		stage.act(delta);
		stage.draw();
	}

	public abstract void renderGame(float delta);

}