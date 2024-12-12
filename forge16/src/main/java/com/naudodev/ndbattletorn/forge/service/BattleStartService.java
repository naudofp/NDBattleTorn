package com.naudodev.ndbattletorn.forge.service;

import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.platform.Messageable;
import com.naudodev.ndbattletorn.forge.NDBattleTorn;
import com.naudodev.ndbattletorn.forge.config.BattleTornConfig;
import com.naudodev.ndbattletorn.forge.utils.BattleUtils;
import com.naudodev.ndbattletorn.forge.utils.ColorsUtils;
import com.pixelmonmod.pixelmon.api.battles.BattleEndCause;
import com.pixelmonmod.pixelmon.api.events.battles.BattleEndEvent;
import com.pixelmonmod.pixelmon.api.events.battles.TurnEndEvent;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.storage.PlayerPartyStorage;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import com.pixelmonmod.pixelmon.battles.api.rules.BattleRuleRegistry;
import com.pixelmonmod.pixelmon.battles.api.rules.BattleRules;
import com.pixelmonmod.pixelmon.battles.api.rules.teamselection.TeamSelectionRegistry;
import com.pixelmonmod.pixelmon.battles.controller.BattleController;
import com.pixelmonmod.pixelmon.battles.controller.participants.PlayerParticipant;
import com.pixelmonmod.pixelmon.battles.status.*;
import net.minecraft.entity.player.ServerPlayerEntity;

import java.util.List;

public class BattleStartService {

    private ForgeEnvyPlayer player1;
    private ForgeEnvyPlayer player2;
    private Messageable<?> sender;
    private BattleTornConfig config;
    private BattleInfoService battleInfoService;

    public BattleStartService(ForgeEnvyPlayer player1, ForgeEnvyPlayer player2, Messageable<?> sender, BattleTornConfig config) {
        this.player1 = player1;
        this.player2 = player2;
        this.sender = sender;
        this.config = config;
        this.battleInfoService = new BattleInfoService(sender, player1, player2);
    }

    public void startBattleTorn() {
        PlayerParticipant participant1 = new PlayerParticipant(this.player1.getParent(), getPlayerPokemon(this.player1.getParent()), 0);
        PlayerParticipant participant2 = new PlayerParticipant(this.player2.getParent(), getPlayerPokemon(this.player2.getParent()), 0);

        StringBuilder[] terrainPlayers = {new StringBuilder(), new StringBuilder()};
        StringBuilder globalStatusString = new StringBuilder();

        BattleRules rules = new BattleRules();
        rules.set(BattleRuleRegistry.FULL_HEAL, false);
        rules.set(BattleRuleRegistry.TURN_TIME, 60);
        rules.set(BattleRuleRegistry.TEAM_PREVIEW, true);
        TeamSelectionRegistry.builder()
                .notCloseable()
                .members(
                        participant1.getStorage(),
                        participant2.getStorage()
                )
                .battleRules(rules)
                .battleStartConsumer(battleController -> {
                    this.setEndBattleEvent(battleController, terrainPlayers, globalStatusString);
                    this.setEndTurnEvent(battleController, terrainPlayers, globalStatusString);
                })
                .start();
    }

    private void setEndTurnEvent(BattleController battleController, StringBuilder[] terrainPLayers, StringBuilder globalStatusString) {
        battleController.addPersistentTaskAtEvent(TurnEndEvent.class, (event, bc) -> {
            for(int i = 0; i < bc.getPlayers().size(); i++) {
                for (int j = 0; j < bc.getPlayers().get(i).allPokemon.length; j++) {
                    if (!bc.getPlayers().get(i).allPokemon[j].getStatuses().isEmpty()) {
                        terrainPLayers[i].setLength(0);
                        terrainPLayers[i].append(ColorsUtils.inBlue("\n&lTerreno") + " &fde " + ColorsUtils.inAqua(bc.getPlayers().get(i).getDisplayName()) + "\n");

                        for (StatusBase s : bc.getPlayers().get(i).allPokemon[j].getStatuses()) {
                            if (s instanceof EntryHazard) {
                                EntryHazard hazard = (EntryHazard) s;
                                terrainPLayers[i].append(ColorsUtils.inRed(" - " + hazard.type.name()) + ColorsUtils.inDarkRed("(" + hazard.getNumLayers() + ")\n"));
                            }

                            if (s instanceof Screen) {
                                Screen screen = (Screen) s;
                                terrainPLayers[i].append(ColorsUtils.inRed(" - " + screen.type.name()) + "\n");
                            }
                        }
                    }
                }
            }
            if (!bc.globalStatusController.getGlobalStatuses().isEmpty()) {
                globalStatusString.setLength(0);
                globalStatusString.append("\n" + ColorsUtils.inGold("&lStatus globais\n"));
                for (GlobalStatusBase gb : bc.globalStatusController.getGlobalStatuses()) {
                    if (gb instanceof Terrain) {
                        Terrain terrain = (Terrain) gb;
                        globalStatusString.append(ColorsUtils.inYellow(terrain.type.name()) + "&f(" + terrain.getRemainingTurns() + ")\n");
                    }

                    if (gb instanceof Weather) {
                        Weather weather = (Weather) gb;
                        globalStatusString.append(ColorsUtils.inYellow(weather.type.name()) + "&f(" + weather.getRemainingTurns() + ")\n");
                    }

                    if (gb instanceof TrickRoom) {
                        TrickRoom trickRoom = (TrickRoom) gb;
                        globalStatusString.append(ColorsUtils.inYellow(trickRoom.type.name()) + "\n");
                    }

                    if (gb instanceof MagicRoom) {
                        MagicRoom magicRoom = (MagicRoom) gb;
                        globalStatusString.append(ColorsUtils.inYellow(magicRoom.type.name()) + "\n");
                    }

                    if (gb instanceof WonderRoom) {
                        WonderRoom wonderRoom = (WonderRoom) gb;
                        globalStatusString.append(ColorsUtils.inYellow(wonderRoom.type.name()) + "\n");
                    }
                }
            } else
                globalStatusString.setLength(0);
        });
    }

    private void setEndBattleEvent(BattleController battleController, StringBuilder[] terrainPLayers, StringBuilder globalStatusString) {
        battleController.addFunctionAtEvent(BattleEndEvent.class, (event, bc) -> {
            if (event.getCause() == BattleEndCause.NORMAL) {
                if (isLivePokemon(this.player1.getParent())) {
                    BattleUtils.endBattleSound(NDBattleTorn.getPlayerManager().getOnlinePlayers());
                    BattleUtils.winMessage(this.player1, config);
                    BattleUtils.executeCommand(this.player1, config.getCommandWinner(), config.getDimension());
                    BattleUtils.executeCommand(this.player2, config.getCommandLooser(), config.getDimension());
                } else {
                    BattleUtils.endBattleSound(NDBattleTorn.getPlayerManager().getOnlinePlayers());
                    BattleUtils.winMessage(this.player2, config);
                    BattleUtils.executeCommand(this.player2, config.getCommandWinner(), config.getDimension());
                    BattleUtils.executeCommand(this.player1, config.getCommandLooser(), config.getDimension());
                }
            } else {
                battleInfoService.sendBattleLogToSender(bc, terrainPLayers, globalStatusString);
            }
            return null;
        });
    }

    private static List<Pokemon> getPlayerPokemon(ServerPlayerEntity player) {
        PlayerPartyStorage party = StorageProxy.getParty(player);
        return party.getTeam();
    }

    private static boolean isLivePokemon(ServerPlayerEntity player) {
        for (Pokemon pokemon : getPlayerPokemon(player)) {
            if (!pokemon.isFainted()) {
                return true;
            }
        }
        return false;
    }
}
