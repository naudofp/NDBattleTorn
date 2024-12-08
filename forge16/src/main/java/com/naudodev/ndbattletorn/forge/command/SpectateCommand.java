package com.naudodev.ndbattletorn.forge.command;

import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.executor.Argument;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.platform.PlatformProxy;
import com.pixelmonmod.pixelmon.api.util.helpers.NetworkHelper;
import com.pixelmonmod.pixelmon.battles.BattleRegistry;
import com.pixelmonmod.pixelmon.battles.api.rules.BattleRuleRegistry;
import com.pixelmonmod.pixelmon.battles.controller.participants.PixelmonWrapper;
import com.pixelmonmod.pixelmon.battles.controller.participants.Spectator;
import com.pixelmonmod.pixelmon.client.gui.battles.PixelmonClientData;
import com.pixelmonmod.pixelmon.comm.packetHandlers.battles.*;

import java.util.Arrays;
import java.util.List;

@Command(
    value = {
        "specbattle",
        "sb"
    }
)
public class SpectateCommand {

    @CommandProcessor
    public void onCommand(@Sender ForgeEnvyPlayer sender, @Argument ForgeEnvyPlayer playerToWatch) {
        var battle = BattleRegistry.getBattle(playerToWatch.getParent());

        if (battle == null) {
            PlatformProxy.sendMessage(sender, List.of("&c(!) Esse jogador não está em batalha"));
            return;
        }

        if (battle.hasSpectator(sender.getParent())) {
            PlatformProxy.sendMessage(sender, List.of("&c(!) Você já está assistindo uma batalha"));
            return;
        }

        var watchedPlayer = battle.getPlayer(playerToWatch.getParent());

        NetworkHelper.sendPacket(new StartBattlePacket(battle.battleIndex, battle.getBattleType(watchedPlayer), battle.rules), sender.getParent());
        NetworkHelper.sendPacket(new SetAllBattlingPokemonPacket(PixelmonClientData.convertToGUI(Arrays.asList(watchedPlayer.allPokemon)), true), sender.getParent());
        List<PixelmonWrapper> teamList = watchedPlayer.getTeamPokemonList();
        NetworkHelper.sendPacket(new SetBattlingPokemonPacket(teamList), sender.getParent());
        NetworkHelper.sendPacket(new SetPokemonBattleDataPacket(PixelmonClientData.convertToGUI(teamList), false), sender.getParent());
        NetworkHelper.sendPacket(new SetPokemonBattleDataPacket(watchedPlayer.getOpponentData(), true), sender.getParent());
        if (battle.getTeam(watchedPlayer).size() > 1) {
            NetworkHelper.sendPacket(new SetPokemonTeamDataPacket(watchedPlayer.getAllyData()), sender.getParent());
        }

        NetworkHelper.sendPacket(new StartSpectatePacket(playerToWatch.getUniqueId(), battle.rules.getOrDefault(BattleRuleRegistry.BATTLE_TYPE)), sender.getParent());

        battle.addSpectator(new Spectator(sender.getParent(), playerToWatch.getName()));
        PlatformProxy.sendMessage(sender, List.of("&a&l(!) &aVocê está assistindo " + playerToWatch.getName()));
    }
}
