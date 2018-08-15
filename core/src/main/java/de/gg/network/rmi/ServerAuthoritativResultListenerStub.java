package de.gg.network.rmi;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.gg.game.AuthoritativeSession;
import de.gg.game.data.vote.VoteResults;
import de.gg.game.type.PositionTypes.PositionType;

/**
 * A result listener stub for the server to conveniently distribute an event to
 * all connected clients.
 * <p>
 * Takes care of calling the appropriate listener methods of every
 * {@linkplain AuthoritativeSession#getResultListeners() registered result
 * listener}.
 */
public class ServerAuthoritativResultListenerStub
		implements AuthoritativeResultListener {

	private AuthoritativeSession serverSession;
	private ExecutorService executor;

	public ServerAuthoritativResultListenerStub(
			AuthoritativeSession serverSession) {
		this.serverSession = serverSession;

		// only a single thread is used so results are distributed one after
		// another
		this.executor = Executors.newSingleThreadExecutor();
	}

	@Override
	public void onAllPlayersReadied() {
		informClients(new ResultTask() {
			@Override
			protected void informClient(
					AuthoritativeResultListener resultListener) {
				resultListener.onAllPlayersReadied();
			}
		});
	}

	@Override
	public void onServerReady() {
		informClients(new ResultTask() {
			@Override
			protected void informClient(
					AuthoritativeResultListener resultListener) {
				resultListener.onServerReady();
			}
		});
	}

	@Override
	public void onCharacterDeath(short characterId) {
		informClients(new ResultTask() {
			@Override
			protected void informClient(
					AuthoritativeResultListener resultListener) {
				resultListener.onCharacterDeath(characterId);
			}
		});
	}

	@Override
	public void onCharacterDamage(short characterId, short damage) {
		informClients(new ResultTask() {
			@Override
			protected void informClient(
					AuthoritativeResultListener resultListener) {
				resultListener.onCharacterDamage(characterId, damage);
			}
		});
	}

	@Override
	public void onPlayerIllnessChange(short playerId, boolean isIll) {
		informClients(new ResultTask() {
			@Override
			protected void informClient(
					AuthoritativeResultListener resultListener) {
				resultListener.onPlayerIllnessChange(playerId, isIll);
			}
		});
	}

	@Override
	public void setGameSpeed(int index) {
		informClients(new ResultTask() {
			@Override
			protected void informClient(
					AuthoritativeResultListener resultListener) {
				resultListener.setGameSpeed(index);
			}
		});
	}

	@Override
	public void onVoteFinished(VoteResults results) {
		informClients(new ResultTask() {
			@Override
			protected void informClient(
					AuthoritativeResultListener resultListener) {
				resultListener.onVoteFinished(results);
			}
		});
	}

	@Override
	public void onAppliedForPosition(short playerId, PositionType type) {
		informClients(new ResultTask() {
			@Override
			protected void informClient(
					AuthoritativeResultListener resultListener) {
				resultListener.onAppliedForPosition(playerId, type);
			}
		});
	}

	@Override
	public void onImpeachmentVoteArranged(short targetCharacterId,
			short callerCharacterId) {
		informClients(new ResultTask() {
			@Override
			protected void informClient(
					AuthoritativeResultListener resultListener) {
				resultListener.onImpeachmentVoteArranged(targetCharacterId,
						callerCharacterId);
			}
		});
	}

	/**
	 * This method takes care of informing every
	 * {@linkplain AuthoritativeSession#resultListeners result listener} about a
	 * result that happened on the server on another thread.
	 * 
	 * @param task
	 *            The task that is used to denote the result.
	 */
	private void informClients(ResultTask task) {
		executor.submit(new Runnable() {
			@Override
			public void run() {
				for (AuthoritativeResultListener resultListener : serverSession
						.getResultListeners().values()) {
					task.informClient(resultListener);
				}
			}
		});
	}

	public abstract class ResultTask {
		protected abstract void informClient(
				AuthoritativeResultListener resultListener);
	}

}