package de.gg.input;

import java.util.List;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.collision.Ray;
import com.google.common.eventbus.EventBus;

import de.gg.entity.Building;
import de.gg.event.HouseEnterEvent;
import de.gg.event.HouseSelectionEvent;
import de.gg.setting.GameSettings;

public class MapSelectionInputController implements DefaultInputProcessor {

	private GameSettings settings;
	private EventBus eventBus;
	private PerspectiveCamera camera;

	private int clickedObjectId = -1;
	private int newSelectionId = -1;
	private int selectedObjectID = -1;

	private long lastClickTime = -1;
	private static final long DOUBLE_CLICK_TIME = 300;

	private List<Building> selectableObjects;

	private int clickX, clickY;

	public MapSelectionInputController(GameSettings settings, EventBus bus,
			PerspectiveCamera camera, List<Building> selectableObjects) {
		this.settings = settings;
		this.eventBus = bus;
		this.camera = camera;
		this.selectableObjects = selectableObjects;
	}

	public void update() {
		if (newSelectionId >= 0) {
			if (System.currentTimeMillis()
					- lastClickTime > DOUBLE_CLICK_TIME) {
				// Einzelklick
				onSingleSelection(newSelectionId);

				newSelectionId = -1;
			}
		}

	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer,
			int button) {
		if (button == Input.Buttons.LEFT) {
			clickedObjectId = getObjectAtPositon(screenX, screenY);
			this.clickX = screenX;
			this.clickY = screenY;
			return clickedObjectId >= 0;
		} else {
			return false;
		}
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return clickedObjectId >= 0;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (button == Input.Buttons.LEFT) {
			if (clickedObjectId >= 0) { // Wenn auf Objekt geklickt
				if (clickedObjectId == getObjectAtPositon(screenX, screenY)) {
					if (System.currentTimeMillis()
							- lastClickTime <= DOUBLE_CLICK_TIME) {
						if (clickedObjectId == newSelectionId) {
							// Doppelklick
							onDoubleSelection(newSelectionId);
							newSelectionId = -1;
						}
					} else {
						newSelectionId = clickedObjectId;
						lastClickTime = System.currentTimeMillis();
					}
				}

				clickedObjectId = -1;
				return true;
			} else { // Wenn neben Objekt geklickt
				onSingleSelection(-1); // Altes Objekt deselektieren
			}
		}

		return false;
	}

	private void onDoubleSelection(int value) {
		eventBus.post(new HouseEnterEvent((short) value));
	}

	public void resetSelection() {
		onSingleSelection(-1);
	}

	private void onSingleSelection(int value) {
		// Altes Objekt reseten
		if (selectedObjectID >= 0) {
			selectableObjects.get(selectedObjectID)
					.getRenderData().isSelected = false;
		}
		// Neues Objekt markieren
		selectedObjectID = value;
		if (selectedObjectID >= 0) {
			selectableObjects.get(selectedObjectID)
					.getRenderData().isSelected = true;
		}

		eventBus.post(new HouseSelectionEvent((short) selectedObjectID, clickX,
				clickY));
	}

	private int getObjectAtPositon(int screenX, int screenY) {
		Ray ray = camera.getPickRay(screenX, screenY);
		int result = -1;
		float distance = -1;
		for (int i = 0; i < selectableObjects.size(); ++i) {
			final float dist2 = selectableObjects.get(i).getRenderData()
					.intersects(ray);
			if (dist2 >= 0f && (distance < 0f || dist2 <= distance)) {
				result = i;
				distance = dist2;
			}
		}
		return result;
	}

}
