package dev.creatormind.respawntimeout.state;

import dev.creatormind.respawntimeout.RespawnTimeoutMod;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
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
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
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


    public static ServerState createFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        final ServerState serverState = new ServerState();
        final NbtCompound playersNbt = tag.getCompound("players");

        playersNbt.getKeys().forEach((key) -> {
            final PlayerState playerState = new PlayerState();

            playerState.deathTimestamp = playersNbt.getCompound(key).getLong("deathTimestamp");

            final UUID uuid = UUID.fromString(key);

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

    private static final Type<ServerState> type = new Type<>(
        ServerState::new,
        ServerState::createFromNbt,
        null
    );

    public static ServerState getServerState(MinecraftServer server) {
        final PersistentStateManager stateManager = server.getOverworld().getPersistentStateManager();

        final ServerState state = stateManager.getOrCreate(type, RespawnTimeoutMod.MOD_ID);

        state.markDirty();

        return state;
    }

    public static PlayerState getPlayerState(ServerPlayerEntity player) {
        final MinecraftServer server = player.getServer();

        if (server == null)
            throw new NullPointerException("[Respawn Timeout] Server is null!");

        final ServerState serverState = ServerState.getServerState(server);

        return serverState.players.computeIfAbsent(player.getUuid(), (uuid) -> new PlayerState());
    }

}
