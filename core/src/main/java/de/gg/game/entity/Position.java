package de.gg.game.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a position.
 */
public class Position {

	/**
	 * The id of the character this position is held by.
	 */
	private short currentHolder;
	private List<Short> applicants = new ArrayList<>();

	public Position() {
		// default public constructor
	}

	public Position(short currentHolder) {
		this.currentHolder = currentHolder;
	}

	/**
	 * @return whether this position is currently held.
	 */
	public boolean isHeld() {
		return currentHolder != -1;
	}

	public short getCurrentHolder() {
		return currentHolder;
	}

	public void setCurrentHolder(short currentHolder) {
		this.currentHolder = currentHolder;
	}

	public List<Short> getApplicants() {
		return applicants;
	}

	public boolean hasApplicants() {
		return !applicants.isEmpty();
	}

}
