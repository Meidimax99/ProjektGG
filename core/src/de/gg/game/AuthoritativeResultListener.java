package de.gg.game;

import de.gg.data.RoundEndData;

/**
 * Classes that implement this interface take care of the
 * {@linkplain SlaveActionHandler player actions} that actually get executed.
 * The client informs the server of their action via the
 * {@linkplain SlaveActionHandlers} and after the server approves an action this
 * interface takes care of executing it.
 */
public interface AuthoritativeResultListener {

	/**
	 * Called after all players readied up.
	 */
	public void onAllPlayersReadied();

	/**
	 * Called after a round ended to inform about the changes.
	 * 
	 * @param data
	 *            This data contains all calculations done after a round i.e. a
	 *            salary costs, tuition effects, etc.
	 */
	public void onRoundEnd(RoundEndData data);

}
