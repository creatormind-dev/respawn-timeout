package dev.creatormind.respawntimeout;

import dev.creatormind.respawntimeout.enums.PlayerStatus;
import dev.creatormind.respawntimeout.state.PlayerState;
import dev.creatormind.respawntimeout.state.ServerState;
import net.fabricmc.api.ModInitializer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
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

    public static void respawnPlayer(ServerPlayerEntity playerEntity) {
        final MinecraftServer server = playerEntity.getServer();

        if (server == null)
            throw new NullPointerException("[Respawn Timeout] Server is null!");

        final ServerState serverState = ServerState.getServerState(server);
        final PlayerState playerState = ServerState.getPlayerState(playerEntity);

        ServerWorld spawnWorld = server.getWorld(playerEntity.getSpawnPointDimension());
        BlockPos spawnPosition = playerEntity.getSpawnPointPosition();

        // Use the player's spawn point if it's valid, otherwise use world's spawn.
        if (spawnWorld == null || spawnPosition == null) {
            spawnWorld = server.getOverworld();
            spawnPosition = spawnWorld.getSpawnPos();
        }

        final int x = spawnPosition.getX();
        final int y = spawnPosition.getY();
        final int z = spawnPosition.getZ();

        playerEntity.teleport(spawnWorld, x, y, z, playerEntity.getYaw(), playerEntity.getPitch());
        playerEntity.changeGameMode(server.getDefaultGameMode());

        // Resets the timeout status.
        playerState.deathTimestamp = 0L;

        serverState.players.put(playerEntity.getUuid(), playerState);
        serverState.markDirty();
    }

}
