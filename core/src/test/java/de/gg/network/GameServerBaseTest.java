package de.gg.network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.mockito.Mockito;

import de.gg.ProjektGGUnitTest;
import de.gg.game.GameSessionSetup;
import de.gg.game.types.GameDifficulty;
import de.gg.game.types.GameMap;
import de.gg.network.GameServer.IHostCallback;
import de.gg.utils.PlayerUtils.PlayerStub;
import net.jodah.concurrentunit.Waiter;

public abstract class GameServerBaseTest extends ProjektGGUnitTest {

	protected static GameServer server;
	protected static Waiter waiter;
	protected static final List<GameClient> clients = new ArrayList<>();

	protected static GameSessionSetup sessionSetup;
	protected static int port = 55557;
	protected static final String gameName = "Test Game";
	protected static final String serverVersion = "1.2.3";
	protected static final int maxPlayerCount = 2;
	protected static PlayerStub stub;

	@BeforeAll
	public static void createServer() throws TimeoutException {
		com.esotericsoftware.minlog.Log.INFO();
		waiter = new Waiter();

		ServerSetup serverSetup = new ServerSetup(gameName, maxPlayerCount,
				port, false, serverVersion, true);
		sessionSetup = new GameSessionSetup(GameDifficulty.EASY,
				GameMap.BAMBERG, 25);

		// Server Creation
		stub = Mockito.mock(PlayerStub.class);

		server = new GameServer(serverSetup, sessionSetup, null,
				Arrays.asList(stub, stub, stub));
		server.start(new IHostCallback() {
			@Override
			public void onHostStarted(Exception e) {
				if (e != null)
					waiter.fail(e);
				else {
					waiter.resume();
				}
			}
		});

		// Wait for resume() to be called
		waiter.await(8000);
	}

	@AfterAll
	public static void afterAll() throws TimeoutException {
		for (GameClient c : clients) {
			c.disconnect();
		}

		if (server != null)
			server.stop();
	}

}