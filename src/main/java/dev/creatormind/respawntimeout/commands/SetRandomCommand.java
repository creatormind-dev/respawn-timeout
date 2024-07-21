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


public class SetRandomCommand {

    private static final int ADMIN_PERMISSION_LEVEL = 4;

    private static final long MAX_TIMEOUT_IN_SECONDS = 604800L;


    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        // Registration call for a random timeout configuration.
        dispatcher.register(literal("respawntimeout")
            .then(literal("set")
                .then(literal("random")
                    .requires((source) -> source.hasPermissionLevel(ADMIN_PERMISSION_LEVEL))
                    .then(argument("min", longArg(0L, MAX_TIMEOUT_IN_SECONDS - 1))
                        .then(argument("max", longArg(0L, MAX_TIMEOUT_IN_SECONDS))
                            .executes((context) -> set(context.getSource(), context.getArgument("min", long.class), context.getArgument("max", long.class)))
                    ))
                )));
    }

    private static int set(ServerCommandSource source, long minTimeout, long maxTimeout) throws CommandSyntaxException {
        if (minTimeout < 0L || minTimeout >= maxTimeout || minTimeout >= MAX_TIMEOUT_IN_SECONDS)
            // TODO: Change the error message to fit the actual case.
            throw new SimpleCommandExceptionType(Text.translatable("respawn-timeout.commands.set_random.error.invalid_min")).create();

        if (maxTimeout > MAX_TIMEOUT_IN_SECONDS)
            // TODO: Change the error message to fit the actual case.
            throw new SimpleCommandExceptionType(Text.translatable("respawn-timeout.commands.set_random.error.invalid_max")).create();

        final ServerState serverState = ServerState.getServerState(source.getServer());

        serverState.respawnTimeout = -1L;
        serverState.timeUnit = TimeUnit.SECONDS;
        serverState.minRandomTimeout = minTimeout;
        serverState.maxRandomTimeout = maxTimeout;
        serverState.markDirty();

        source.sendFeedback(() -> Text.translatable(
            "respawn-timeout.commands.set_random.success",
            minTimeout,
            maxTimeout
        ), true);

        return Command.SINGLE_SUCCESS;
    }

}
