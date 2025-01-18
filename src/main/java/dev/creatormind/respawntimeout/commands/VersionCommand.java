package dev.creatormind.respawntimeout.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import dev.creatormind.respawntimeout.RespawnTimeoutMod;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.*;


public class VersionCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        // Registers the command.
        dispatcher.register(literal("respawntimeout")
            .then(literal("version")
                .executes((context) -> version(context.getSource()))
            ));
    }

    private static int version(ServerCommandSource source) {
        source.sendFeedback(() -> Text.translatable(
            "respawn-timeout.commands.version.success",
            RespawnTimeoutMod.MOD_VERSION,
            RespawnTimeoutMod.MINECRAFT_VERSION
        ), false);

        return Command.SINGLE_SUCCESS;
    }

}
