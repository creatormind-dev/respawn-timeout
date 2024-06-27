package dev.creatormind.respawntimeout.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.creatormind.respawntimeout.state.ServerState;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.concurrent.TimeUnit;

import static com.mojang.brigadier.arguments.LongArgumentType.*;
import static net.minecraft.server.command.CommandManager.*;


public class SetCommand {

    private static final int ADMIN_PERMISSION_LEVEL = 4;


    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        // Registration call for configuration in seconds.
        dispatcher.register(literal("respawntimeout")
            .then(literal("set")
                .requires((source) -> source.hasPermissionLevel(ADMIN_PERMISSION_LEVEL))
                .then(argument("timeout", longArg(0L, 604800L))
                    .then(literal("seconds")
                        .executes((context) -> set(context.getSource(), getLong(context, "timeout"), TimeUnit.SECONDS))
                    ))));

        // Registration call for configuration in minutes.
        dispatcher.register(literal("respawntimeout")
            .then(literal("set")
                .requires((source) -> source.hasPermissionLevel(ADMIN_PERMISSION_LEVEL))
                .then(argument("timeout", longArg(0L, 10080L))
                    .then(literal("minutes")
                        .executes((context) -> set(context.getSource(), getLong(context, "timeout"), TimeUnit.MINUTES))
                    ))));

        // Registration call for configuration in hours.
        dispatcher.register(literal("respawntimeout")
            .then(literal("set")
                .requires((source) -> source.hasPermissionLevel(ADMIN_PERMISSION_LEVEL))
                .then(argument("timeout", longArg(0L, 168L))
                    .then(literal("hours")
                        .executes((context) -> set(context.getSource(), getLong(context, "timeout"), TimeUnit.HOURS))
                    ))));

        // Registration call for configuration in days.
        dispatcher.register(literal("respawntimeout")
            .then(literal("set")
                .requires((source) -> source.hasPermissionLevel(ADMIN_PERMISSION_LEVEL))
                .then(argument("timeout", longArg(0L, 7L))
                    .then(literal("days")
                        .executes((context) -> set(context.getSource(), getLong(context, "timeout"), TimeUnit.DAYS))
                    ))));
    }

    private static int set(ServerCommandSource source, long timeout, TimeUnit unit) throws CommandSyntaxException {
        // No comment.
        if (timeout < 0L)
            throw new SimpleCommandExceptionType(Text.translatable("cmd.respawn-timeout.set.err")).create();

        final ServerState serverState = ServerState.getServerState(source.getServer());

        serverState.respawnTimeout = timeout;
        serverState.timeUnit = unit;
        serverState.markDirty();

        source.sendFeedback(Text.translatable(
            "cmd.respawn-timeout.set.res",
            timeout,
            unit.toString().toLowerCase().charAt(0) + ""
        ), true);

        return Command.SINGLE_SUCCESS;
    }

}
