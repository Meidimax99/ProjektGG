package de.gg.data;

/**
 * Holds all the information about the settings of a game session. The
 * information should stay constant over the whole match.
 */
public class GameSessionSetup {

	/**
	 * The random seed used by the game. Needed to synchronize the random events
	 * of all clients. Should not change after it is set.
	 */
	private long seed;
	/**
	 * The game difficulty. Should not change after it is set.
	 */
	private GameDifficulty difficulty;
	/**
	 * The map id. Should not change after it is set.
	 */
	private int mapId;

	public GameSessionSetup() {
	}

	public GameSessionSetup(GameDifficulty difficulty, int mapId, long seed) {
		this.difficulty = difficulty;
		this.mapId = mapId;
		this.seed = seed;
	}

	public long getSeed() {
		return seed;
	}

	public GameDifficulty getDifficulty() {
		return difficulty;
	}

	/**
	 * @return The index of the used map.
	 */
	public int getMapId() {
		return mapId;
	}

	/**
	 * An enum describing the game difficulty.
	 */
	public enum GameDifficulty {
		EASY, NORMAL, HARD;

		private float startingGoldMoifier;
		private float actionModifer;

		/**
		 * @return This modifier is multiplied with the starting gold.
		 */
		public float getStartingGoldMoifier() {
			return startingGoldMoifier;
		}

		/**
		 * @return This value influences the probability a npc does something in
		 *         favor of the player.
		 */
		public float getActionModifer() {
			return actionModifer;
		}

	}

}
