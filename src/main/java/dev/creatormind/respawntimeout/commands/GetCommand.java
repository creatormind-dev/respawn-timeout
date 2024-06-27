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

        source.sendFeedback(Text.translatable(
            "cmd.respawn-timeout.get.res",
            serverState.respawnTimeout,
            serverState.timeUnit.toString().toLowerCase().charAt(0) + ""
        ), false);

        return Command.SINGLE_SUCCESS;
    }

}
