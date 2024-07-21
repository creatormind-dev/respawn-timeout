package dev.creatormind.respawntimeout.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import dev.creatormind.respawntimeout.state.ServerState;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.concurrent.TimeUnit;

import static net.minecraft.server.command.CommandManager.*;


public class ClearCommand {

    private static final int ADMIN_PERMISSION_LEVEL = 4;


    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("respawntimeout")
            .then(literal("clear")
                .requires((source) -> source.hasPermissionLevel(ADMIN_PERMISSION_LEVEL))
                .executes((context) -> clear(context.getSource()))
            ));
    }

    private static int clear(ServerCommandSource source) {
        final ServerState serverState = ServerState.getServerState(source.getServer());

        serverState.timeUnit = TimeUnit.SECONDS;
        serverState.respawnTimeout = 0L;
        serverState.minRandomTimeout = 0L;
        serverState.maxRandomTimeout = 0L;
        serverState.markDirty();

        source.sendFeedback(() -> Text.translatable("respawn-timeout.commands.clear.success"), false);

        return Command.SINGLE_SUCCESS;
    }

}
