package dev.creatormind.respawntimeout.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import dev.creatormind.respawntimeout.RespawnTimeoutMod;
import dev.creatormind.respawntimeout.enums.PlayerStatus;
import dev.creatormind.respawntimeout.state.PlayerState;
import dev.creatormind.respawntimeout.state.ServerState;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.*;


public class RespawnCommand {

    private static final int MODERATOR_PERMISSION_LEVEL = 2;


    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        // Registration call for a normal user.
        dispatcher.register(literal("respawntimeout")
            .then(literal("respawn")
                .executes((context) -> respawn(context.getSource(), context.getSource().getPlayer(), false))
            ));

        // Registration call for a forced use by a moderator.
        dispatcher.register(literal("respawntimeout")
            .then(literal("respawn")
                .then(argument("player", EntityArgumentType.player())
                    .requires((source) -> source.hasPermissionLevel(MODERATOR_PERMISSION_LEVEL))
                    .executes((context) -> respawn(context.getSource(), EntityArgumentType.getPlayer(context, "player"), true))
                )));
    }

    private static int respawn(ServerCommandSource source, ServerPlayerEntity player, boolean force) {
        final PlayerStatus playerStatus = RespawnTimeoutMod.getPlayerStatus(player);

        switch (playerStatus) {
            case ALIVE: {
                String translationKey = (player == source.getPlayer())
                    ? "txt.respawn-timeout.player_na"
                    : "txt.respawn-timeout.player_ext_na";

                source.sendFeedback(Text.translatable(translationKey, player.getName().getString()), false);

                break;
            }
            case TIMED_OUT: {
                if (force) {
                    RespawnTimeoutMod.respawnPlayer(player);

                    source.sendFeedback(Text.translatable("txt.respawn-timeout.player_ext_respawn", player.getName().getString()), false);
                }
                else {
                    source.sendFeedback(Text.translatable("txt.respawn-timeout.player_status"), false);
                }

                break;
            }
            case AWAITING_RESPAWN: {
                RespawnTimeoutMod.respawnPlayer(player);

                player.sendMessage(Text.translatable("txt.respawn-timeout.player_respawn"), false);

                if (force)
                    source.sendFeedback(Text.translatable("txt.respawn-timeout.player_ext_respawn", player.getName().getString()), false);

                break;
            }
            default:
                throw new AssertionError("Unreachable");
        }

        return Command.SINGLE_SUCCESS;
    }

}
