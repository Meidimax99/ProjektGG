package de.gg.game.ui.components;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import de.eskalon.commons.audio.ISoundManager;
import de.eskalon.commons.lang.Lang;
import de.eskalon.commons.settings.EskalonSettings;
import de.gg.game.input.ButtonClickListener;

/**
 * An input field with which {@link Keys} can be queried.
 */
public class KeySelectionInputField extends ImageTextButton {

	public KeySelectionInputField(EskalonSettings settings,
			String keybindName, Skin skin, Stage stage,
			ISoundManager soundManager) {
		super(settings.getKeybind(keybindName).toString(), skin);

		addListener(new ButtonClickListener(soundManager) {
			@Override
			protected void onClick() {
				SimpleTextDialog dialog = new SimpleTextDialog(
						Lang.get("dialog.key_selection.select"), skin);
				dialog.text(Lang.get("dialog.key_selection.press_key"))
						.button(Lang.get("ui.generic.back"), false)
						.key(Keys.ESCAPE, false);
				dialog.addListener(new InputListener() {
					@Override
					public boolean keyDown(InputEvent event, int keycode) {
						settings.setKeybind(keybindName, keycode);

						setText(Keys.toString(keycode));
						dialog.hide();

						return true;
					}
				});
				dialog.show(stage);
			}
		});
	}

}
