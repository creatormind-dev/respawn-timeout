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

    private static final long MAX_TIMEOUT_IN_SECONDS = 604800L;
    private static final long MAX_TIMEOUT_IN_MINUTES = 10080L;
    private static final long MAX_TIMEOUT_IN_HOURS = 168L;
    private static final long MAX_TIMEOUT_IN_DAYS = 7L;


    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        // Registration call for configuration in seconds.
        dispatcher.register(literal("respawntimeout")
            .then(literal("set")
                .requires((source) -> source.hasPermissionLevel(ADMIN_PERMISSION_LEVEL))
                .then(literal("fixed")
                    .then(argument("timeout", longArg(0L, MAX_TIMEOUT_IN_SECONDS))
                        .then(literal("seconds")
                            .executes((context) -> set(context.getSource(), getLong(context, "timeout"), MAX_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS))
                        )))));

        // Registration call for configuration in minutes.
        dispatcher.register(literal("respawntimeout")
            .then(literal("set")
                .requires((source) -> source.hasPermissionLevel(ADMIN_PERMISSION_LEVEL))
                .then(literal("fixed")
                    .then(argument("timeout", longArg(0L, MAX_TIMEOUT_IN_MINUTES))
                        .then(literal("minutes")
                            .executes((context) -> set(context.getSource(), getLong(context, "timeout"), MAX_TIMEOUT_IN_MINUTES, TimeUnit.MINUTES))
                        )))));

        // Registration call for configuration in hours.
        dispatcher.register(literal("respawntimeout")
            .then(literal("set")
                .requires((source) -> source.hasPermissionLevel(ADMIN_PERMISSION_LEVEL))
                .then(literal("fixed")
                    .then(argument("timeout", longArg(0L, MAX_TIMEOUT_IN_HOURS))
                        .then(literal("hours")
                            .executes((context) -> set(context.getSource(), getLong(context, "timeout"), MAX_TIMEOUT_IN_HOURS, TimeUnit.HOURS))
                        )))));

        // Registration call for configuration in days.
        dispatcher.register(literal("respawntimeout")
            .then(literal("set")
                .requires((source) -> source.hasPermissionLevel(ADMIN_PERMISSION_LEVEL))
                .then(literal("fixed")
                    .then(argument("timeout", longArg(0L, MAX_TIMEOUT_IN_DAYS))
                        .then(literal("days")
                            .executes((context) -> set(context.getSource(), getLong(context, "timeout"), MAX_TIMEOUT_IN_DAYS, TimeUnit.DAYS))
                        )))));
    }

    private static int set(ServerCommandSource source, long timeout, long maxTimeout, TimeUnit unit) throws CommandSyntaxException {
        // No comment.
        if (timeout < 0L)
            throw new SimpleCommandExceptionType(Text.translatable("respawn-timeout.commands.set.error.negative")).create();

        // Throws an error in case the timeout in a time unit is out of range.
        switch (unit) {
            case SECONDS:
                if (timeout <= MAX_TIMEOUT_IN_SECONDS)
                    break;
            case MINUTES:
                if (timeout <= MAX_TIMEOUT_IN_MINUTES)
                    break;
            case HOURS:
                if (timeout <= MAX_TIMEOUT_IN_HOURS)
                    break;
            case DAYS:
                if (timeout <= MAX_TIMEOUT_IN_DAYS)
                    break;
            default:
                throw new SimpleCommandExceptionType(Text.translatable(
                    "respawn-timeout.commands.set.error.invalid",
                    unit.toString().toUpperCase(),
                    0L,
                    maxTimeout
                )).create();
        }

        final ServerState serverState = ServerState.getServerState(source.getServer());

        serverState.respawnTimeout = timeout;
        serverState.timeUnit = unit;
        serverState.minRandomTimeout = 0L;
        serverState.maxRandomTimeout = 0L;
        serverState.markDirty();

        source.sendFeedback(() -> Text.translatable(
            "respawn-timeout.commands.set.success",
            timeout,
            unit.toString().toLowerCase().charAt(0) + ""
        ), true);

        return Command.SINGLE_SUCCESS;
    }

}
