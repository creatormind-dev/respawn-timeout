package dev.creatormind.respawntimeout.state;

import dev.creatormind.respawntimeout.RespawnTimeoutMod;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


public class ServerState extends PersistentState {

    public HashMap<UUID, PlayerState> players   = new HashMap<>();

    // Min and max timeouts will always be in seconds.
    public long maxRandomTimeout                = 0L;
    public long minRandomTimeout                = 0L;

    // This is the global/server specific timeout that will be applied to everyone equally.
    public long respawnTimeout                  = 0L;
    public TimeUnit timeUnit                    = TimeUnit.SECONDS;


    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        final NbtCompound playersNbt = new NbtCompound();

        players.forEach((uuid, playerState) -> {
            final NbtCompound playerStateNbt = new NbtCompound();

            playerStateNbt.putLong("deathTimestamp", playerState.deathTimestamp);

            playersNbt.put(uuid.toString(), playerStateNbt);
        });

        nbt.put("players", playersNbt);
        nbt.putLong("maxRandomTimeout", maxRandomTimeout);
        nbt.putLong("minRandomTimeout", minRandomTimeout);
        nbt.putLong("respawnTimeout", respawnTimeout);
        nbt.putString("timeUnit", timeUnit.toString());

        return nbt;
    }


    public static ServerState createFromNbt(NbtCompound tag) {
        final ServerState serverState = new ServerState();
        final NbtCompound playersNbt = tag.getCompound("players");

        playersNbt.getKeys().forEach((key) -> {
            final PlayerState playerState = new PlayerState();

            playerState.deathTimestamp = playersNbt.getCompound(key).getLong("deathTimestamp");

            final UUID uuid = UUID.fromString(key);

            serverState.players.put(uuid, playerState);
        });

        serverState.maxRandomTimeout = tag.getLong("maxRandomTimeout");
        serverState.minRandomTimeout = tag.getLong("minRandomTimeout");
        serverState.respawnTimeout = tag.getLong("respawnTimeout");

        try {
            serverState.timeUnit = TimeUnit.valueOf(tag.getString("timeUnit"));
        }
        catch (Exception e) {
            serverState.timeUnit = TimeUnit.SECONDS;
        }

        return serverState;
    }

    public static ServerState getServerState(MinecraftServer server) {
        final ServerWorld overworld = server.getOverworld();

        if (overworld == null)
            throw new NullPointerException("[Respawn Timeout] Overworld is null!");

        final PersistentStateManager stateManager = overworld.getPersistentStateManager();

        return stateManager.getOrCreate(
            ServerState::createFromNbt,
            ServerState::new,
            RespawnTimeoutMod.MOD_ID
        );
    }

    public static PlayerState getPlayerState(ServerPlayerEntity player) {
        final MinecraftServer server = player.getServer();

        if (server == null)
            throw new NullPointerException("[Respawn Timeout] Server is null!");

        final ServerState serverState = ServerState.getServerState(server);

        return serverState.players.computeIfAbsent(player.getUuid(), (uuid) -> new PlayerState());
    }

}
