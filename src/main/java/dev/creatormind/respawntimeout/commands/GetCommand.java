package dev.creatormind.respawntimeout.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import dev.creatormind.respawntimeout.state.ServerState;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.*;


public class GetCommand {

    private static final int ADMIN_PERMISSION_LEVEL = 4;


    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("respawntimeout")
            .then(literal("get")
                .requires((source) -> source.hasPermissionLevel(ADMIN_PERMISSION_LEVEL))
                .executes((context) -> get(context.getSource()))
            ));
    }

    private static int get(ServerCommandSource source) {
        final ServerState serverState = ServerState.getServerState(source.getServer());

        if (serverState.respawnTimeout >= 0L) {
            source.sendFeedback(Text.translatable(
                "respawn-timeout.commands.get.success.fixed",
                serverState.respawnTimeout,
                serverState.timeUnit.toString().toLowerCase().charAt(0) + ""
            ), false);
        }
        else {
            source.sendFeedback(Text.translatable(
                "respawn-timeout.commands.get.success.random",
                serverState.minRandomTimeout,
                serverState.maxRandomTimeout
            ), false);
        }

        return Command.SINGLE_SUCCESS;
    }

}
