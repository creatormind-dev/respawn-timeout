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

    public long respawnTimeout                  = 0L;
    public TimeUnit timeUnit                    = TimeUnit.SECONDS;
    public HashMap<UUID, PlayerState> players   = new HashMap<>();


    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        final NbtCompound playersNbt = new NbtCompound();

        players.forEach((uuid, playerState) -> {
            final NbtCompound playerStateNbt = new NbtCompound();

            playerStateNbt.putLong("deathTimestamp", playerState.deathTimestamp);

            playersNbt.put(uuid.toString(), playerStateNbt);
        });

        nbt.put("players", playersNbt);
        nbt.putLong("respawnTimeout", respawnTimeout);
        nbt.putString("timeUnit", timeUnit.toString());

        return nbt;
    }


    public static ServerState createFromNbt(NbtCompound tag) {
        final ServerState serverState = new ServerState();
        final NbtCompound playersNbt = tag.getCompound("players");

        playersNbt.getKeys().forEach((key) -> {
            final PlayerState playerState = new PlayerState();

            playerState.deathTimestamp = tag.getCompound(key).getLong("deathTimestamp");

            final UUID uuid = UUID.fromString(playersNbt.getString(key));

            serverState.players.put(uuid, playerState);
        });

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

    public static PlayerState getPlayerState(ServerPlayerEntity playerEntity) {
        final MinecraftServer server = playerEntity.getServer();

        if (server == null)
            throw new NullPointerException("[Respawn Timeout] Server is null!");

        final ServerState serverState = ServerState.getServerState(server);

        return serverState.players.computeIfAbsent(playerEntity.getUuid(), (uuid) -> new PlayerState());
    }

}
