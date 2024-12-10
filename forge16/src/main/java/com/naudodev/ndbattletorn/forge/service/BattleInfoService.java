package com.naudodev.ndbattletorn.forge.service;

import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.forge.server.UtilForgeServer;
import com.envyful.api.platform.Messageable;
import com.naudodev.ndbattletorn.forge.utils.ColorsUtils;
import com.pixelmonmod.pixelmon.battles.controller.BattleController;

public class BattleInfoService {

    private Messageable<?> sender;
    private ForgeEnvyPlayer player1;
    private ForgeEnvyPlayer player2;

    public BattleInfoService(Messageable<?> sender, ForgeEnvyPlayer player1, ForgeEnvyPlayer player2) {
        this.sender = sender;
        this.player1 = player1;
        this.player2 = player2;
    }

    public void sendBattleLogToSender(BattleController bc, StringBuilder[] terrainPLayers, StringBuilder globalStatusString) {
        UtilForgeServer.executeCommand(
                "/s " + "\n&4&l[!] &r&fBatalha de "
                + ColorsUtils.inDarkRed(player1.getName()) + " &fe "
                + ColorsUtils.inDarkRed(player2.getName())
                + "&f foi interrompida\n"
                + terrainPLayers[0].toString() + "\n" + terrainPLayers[1].toString()
                + globalStatusString);
    }
}
