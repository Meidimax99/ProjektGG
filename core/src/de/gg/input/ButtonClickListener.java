package de.gg.input;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

import net.dermetfan.gdx.assets.AnnotationAssetManager.Asset;

/**
 * This class takes care of a button click. It especially plays the click sound.
 */
public abstract class ButtonClickListener extends InputListener {

	@Asset(Sound.class)
	private static final String CLICK_SOUND = "audio/button-tick.mp3";

	private Sound clickSound;

	/**
	 * @param assetManager
	 *            The game's asset manager. Is used to retrieve the click sound.
	 */
	public ButtonClickListener(AssetManager assetManager) {
		clickSound = assetManager.get(CLICK_SOUND);
	}

	@Override
	public boolean touchDown(InputEvent event, float x, float y, int pointer,
			int button) {
		if (arePreconditionsMet()) {
			clickSound.play(1F);
			onClick();

			return true;
		}
		return false;
	}

	/**
	 * If the button click should only trigger in certain circumstances this can
	 * be handled by overriding this method.
	 * 
	 * @return Whether the preconditions for this button click are met. Per
	 *         default this is always <code>true</code>.
	 */
	protected boolean arePreconditionsMet() {
		return true;
	}

	/**
	 * This method is responsible for taking care of the input event.
	 */
	protected abstract void onClick();

}
