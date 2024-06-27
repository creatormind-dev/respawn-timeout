package dev.creatormind.respawntimeout;

import dev.creatormind.respawntimeout.enums.PlayerStatus;
import dev.creatormind.respawntimeout.state.PlayerState;
import dev.creatormind.respawntimeout.state.ServerState;
import net.fabricmc.api.ModInitializer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RespawnTimeoutMod implements ModInitializer {

    public static final String MOD_ID = "respawn-timeout";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);


    @Override
    public void onInitialize() {
        LOGGER.info("[Respawn Timeout] Initializing");
    }


    public static PlayerStatus getPlayerStatus(ServerPlayerEntity playerEntity) {
        final MinecraftServer server = playerEntity.getServer();

        if (server == null)
            throw new NullPointerException("[Respawn Timeout] Server is null!");

        final ServerState serverState = ServerState.getServerState(server);
        final PlayerState playerState = ServerState.getPlayerState(playerEntity);

        // The player must be on spectator mode (timed out) and have a registered death time.
        if (playerState.deathTimestamp == 0L || !playerEntity.isSpectator())
            return PlayerStatus.ALIVE;

        final long respawnTimeoutInSeconds = serverState.timeUnit.toSeconds(serverState.respawnTimeout);
        final long timeSinceDeathInSeconds = (System.currentTimeMillis() - playerState.deathTimestamp) / 1000L;

        // Time elapsed since death should be less than the defined timeout for a player to be timed out.
        if (timeSinceDeathInSeconds < respawnTimeoutInSeconds)
            return PlayerStatus.TIMED_OUT;

        return PlayerStatus.AWAITING_RESPAWN;
    }

}
