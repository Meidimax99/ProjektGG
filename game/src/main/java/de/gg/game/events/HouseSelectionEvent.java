package de.gg.game.events;

/**
 * Is posted when the local player selects a house.
 */
public class HouseSelectionEvent {

	/**
	 * The id of the selected object. Can be -1 to denote a click not on an
	 * object.
	 */
	private short id;
	private int clickX, clickY;

	public HouseSelectionEvent(short id, int clickX, int clickY) {
		this.id = id;
		this.clickX = clickX;
		this.clickY = clickY;
	}

	/**
	 * @return the id of the selected object. Can be -1 to denote a click not on
	 *         an object.
	 */
	public short getId() {
		return id;
	}

	/**
	 * @return the x position of the user's click on the screen.
	 */
	public int getClickX() {
		return clickX;
	}

	/**
	 * @return the y position of the user's click on the screen.
	 */
	public int getClickY() {
		return clickY;
	}

}
