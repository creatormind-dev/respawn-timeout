package dev.creatormind.respawntimeout;

import dev.creatormind.respawntimeout.commands.*;
import dev.creatormind.respawntimeout.enums.PlayerStatus;
import dev.creatormind.respawntimeout.state.PlayerState;
import dev.creatormind.respawntimeout.state.ServerState;
import dev.creatormind.respawntimeout.utils.TimeFormatter;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;


public class RespawnTimeoutMod implements ModInitializer {

    public static final String MOD_ID = "respawn-timeout";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);


    @Override
    public void onInitialize() {
        LOGGER.info("[Respawn Timeout] Initializing");

        // Commands.
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            if (!environment.dedicated)
                return;

            SetCommand.register(dispatcher);
            SetRandomCommand.register(dispatcher);
            GetCommand.register(dispatcher);
            ClearCommand.register(dispatcher);
            RespawnCommand.register(dispatcher);
        });

        // Automatic respawn verification if needed when joining.
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            final ServerPlayerEntity player = handler.getPlayer();
            final ServerState serverState = ServerState.getServerState(server);
            final PlayerState playerState = ServerState.getPlayerState(player);
            final PlayerStatus playerStatus = getPlayerStatus(player);

            if (playerStatus == PlayerStatus.TIMED_OUT) {
                final long remainingTime = serverState.timeUnit.toMillis(serverState.respawnTimeout) - (System.currentTimeMillis() - playerState.deathTimestamp);

                player.sendMessage(Text.translatable(
                    "respawn-timeout.info.self.status",
                    TimeFormatter.format(remainingTime, TimeUnit.MILLISECONDS)
                ), false);
            }
            else if (playerStatus == PlayerStatus.AWAITING_RESPAWN) {
                respawnPlayer(player);

                player.sendMessage(Text.translatable("respawn-timeout.info.self.respawned"), false);
            }
        });

        // Timing out process for when a player dies, if there's an applicable timeout.
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            if (!(entity instanceof ServerPlayerEntity player))
                return;

            final MinecraftServer server = player.getServer();

            if (server == null)
                throw new NullPointerException("[Respawn Timeout] Server is null!");

            final ServerState serverState = ServerState.getServerState(server);

            // If there's no timeout there's no need to perform any action.
            if (serverState.respawnTimeout <= 0)
                return;

            final PlayerState playerState = ServerState.getPlayerState(player);

            playerState.deathTimestamp = System.currentTimeMillis();

            serverState.players.put(player.getUuid(), playerState);
            serverState.markDirty();

            player.changeGameMode(GameMode.SPECTATOR);
            player.sendMessage(Text.translatable(
                "respawn-timeout.info.self.status",
                TimeFormatter.format(serverState.respawnTimeout, serverState.timeUnit)
            ), false);
        });
    }


    public static PlayerStatus getPlayerStatus(ServerPlayerEntity player) {
        final MinecraftServer server = player.getServer();

        if (server == null)
            throw new NullPointerException("[Respawn Timeout] Server is null!");

        final ServerState serverState = ServerState.getServerState(server);
        final PlayerState playerState = ServerState.getPlayerState(player);

        // The player must be on spectator mode (timed out) and have a registered death time.
        if (playerState.deathTimestamp == 0L || !player.isSpectator())
            return PlayerStatus.ALIVE;

        final long respawnTimeoutInSeconds = serverState.timeUnit.toSeconds(serverState.respawnTimeout);
        final long timeSinceDeathInSeconds = (System.currentTimeMillis() - playerState.deathTimestamp) / 1000L;

        // Time elapsed since death should be less than the defined timeout for a player to be timed out.
        if (timeSinceDeathInSeconds < respawnTimeoutInSeconds)
            return PlayerStatus.TIMED_OUT;

        return PlayerStatus.AWAITING_RESPAWN;
    }

    public static void respawnPlayer(ServerPlayerEntity player) {
        final MinecraftServer server = player.getServer();

        if (server == null)
            throw new NullPointerException("[Respawn Timeout] Server is null!");

        final ServerState serverState = ServerState.getServerState(server);
        final PlayerState playerState = ServerState.getPlayerState(player);

        ServerWorld spawnWorld = server.getWorld(player.getSpawnPointDimension());
        BlockPos spawnPosition = player.getSpawnPointPosition();

        // Use the player's spawn point if it's valid, otherwise use world's spawn.
        if (spawnWorld == null || spawnPosition == null) {
            spawnWorld = server.getOverworld();
            spawnPosition = spawnWorld.getSpawnPos();
        }

        final int x = spawnPosition.getX();
        final int y = spawnPosition.getY();
        final int z = spawnPosition.getZ();

        player.teleport(spawnWorld, x, y, z, player.getYaw(), player.getPitch());
        player.changeGameMode(server.getDefaultGameMode());

        // Resets the timeout status.
        playerState.deathTimestamp = 0L;

        serverState.players.put(player.getUuid(), playerState);
        serverState.markDirty();
    }

}
