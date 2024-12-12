package com.naudodev.ndbattletorn.forge.utils;

import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.forge.server.UtilForgeServer;
import com.naudodev.ndbattletorn.forge.NDBattleTorn;
import com.naudodev.ndbattletorn.forge.config.BattleTornConfig;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.SoundEvents;

import java.util.List;

public class BattleUtils {

    public static final String TORNEIO = "\n§c§l[Torneio] §f";
    public static final String DUPLICATE_PLAYER_IN_BATTLE = ColorsUtils.inRed("&l(!) &eÉ necessário dois jogadores diferentes para iniciar uma batalha");
    public static final String PLAYER_ALREADY_IN_BATTLE = ColorsUtils.inRed("&l(!) &eOs jogadores já estão em batalha.");

    public static void winMessage(ForgeEnvyPlayer player, BattleTornConfig config) {
        if (config.getMessageWin().contains("%player%")) {
            UtilForgeServer.broadcast(config.getMessageWin().replace("%player%", player.getName()) + "\n");
        }
        UtilForgeServer.broadcast(TORNEIO + player.getParent().getName().getString() + " " + config.getMessageWin() + "\n");
    }

    public static void executeCommand(ForgeEnvyPlayer player, String command, String dimension) {
        if (command.contains("%player%")) {
            command = command.replace("%player%", player.getName());
        }
        if (command.contains("%dimension%")) {
            command = command.replace("%dimension%", dimension);
        }

        UtilForgeServer.executeCommand(command);
    }

    public static void endBattleSound(List<ForgeEnvyPlayer> players) {
        players.stream().forEach(p -> p.playSound(SoundEvents.PLAYER_LEVELUP, 1.0F, 1.0F));
    }
}
