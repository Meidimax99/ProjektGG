package de.gg.screen;

import de.gg.input.BackInputProcessor;

/**
 * This screen is rendered, when the player is inside of a house.
 */
public class GameInHouseScreen extends BaseGameScreen {

	private short selectedHouseId;

	@Override
	protected void onInit() {
		super.onInit();
		// TODO Auto-generated method stub
	}

	@Override
	protected void initUI() {
	}

	@Override
	public void renderGame(float delta) {
		// TODO Auto-generated method stub

	}

	@Override
	public void show() {
		super.show();
		game.getInputMultiplexer().addProcessor(new BackInputProcessor() {
			@Override
			public void onBackAction() {
				game.pushScreen("map");
			}
		});
	}

	public void setSelectedHouseId(short selectedHouseId) {
		this.selectedHouseId = selectedHouseId;
	}

	@Override
	public void dispose() {
		super.dispose();

		// if (isLoaded())
		// dispose loaded stuff
	}

}
