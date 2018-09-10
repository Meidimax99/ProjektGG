package de.gg.game.systems.client;

import java.util.Map.Entry;

import com.google.common.eventbus.EventBus;

import de.gg.events.NewNotificationEvent;
import de.gg.game.data.NotificationData;
import de.gg.game.entities.Character;
import de.gg.game.entities.Player;
import de.gg.game.entities.Position;
import de.gg.game.systems.ProcessingSystem;
import de.gg.game.types.PositionType;
import de.gg.game.types.SocialStatus;
import de.gg.game.world.City;
import de.gg.lang.Lang;

/**
 * This system processes after 60 seconds and takes care of the first wave of
 * events on the client side.
 */
public class FirstEventWaveClientSystem extends ProcessingSystem<Player> {

	private City city;
	private EventBus eventBus;
	private short localPlayerId;

	public FirstEventWaveClientSystem(EventBus eventBus, short localPlayerId) {
		this.eventBus = eventBus;
		this.localPlayerId = localPlayerId;
	}

	@Override
	public void init(City city, long seed) {
		this.city = city;
	}

	@Override
	public void process(short id, Player p) {
		if (id == localPlayerId) {
			Character c = p.getCurrentlyPlayedCharacter(city);

			// Inform about open positions
			for (Entry<PositionType, Position> e : city.getPositions()
					.entrySet()) {
				if (!e.getValue().isHeld()) {
					if (e.getKey().getData().getLevel() - 1 <= c
							.getHighestPositionLevel()) {
						if (e.getKey().getData().getStatusRequirement() == null
								|| e.getKey().getData().getStatusRequirement()
										.getData().getLevel() <= c.getStatus()
												.getData().getLevel()) {

							NotificationData not = new NotificationData(
									Lang.get(
											"notification.pos_available.title"),
									Lang.get("notification.pos_available.text",
											e.getKey()),
									null);

							eventBus.post(new NewNotificationEvent(not));
						}
					}
				}
			}

			if (c.getStatus() == SocialStatus.NON_CITIZEN) {
				if (p.getFortune(city) >= SocialStatus.NON_CITIZEN.getData()
						.getFortuneRequirement()) {
					// TODO inform about possibility to buy citizen status
				}
			}
		}
	}

	@Override
	public boolean isProcessedContinuously() {
		return false;
	}

	@Override
	public int getTickRate() {
		return 600;
	}

}