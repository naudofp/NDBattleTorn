package com.naudodev.ndbattletorn.forge.service;

import com.envyful.api.platform.Messageable;
import com.pixelmonmod.pixelmon.battles.controller.BattleController;
import com.pixelmonmod.pixelmon.battles.controller.participants.PlayerParticipant;
import com.pixelmonmod.pixelmon.battles.status.EntryHazard;
import com.pixelmonmod.pixelmon.battles.status.StatusBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BattleInfoService {

    private static final Logger LOGGER = LogManager.getLogger("ndbattletorn");

    public static void getBattleInfo(BattleController battle, Messageable<?> sender) {
        for (PlayerParticipant p: battle.getPlayers()) {
            sender.message("&4" + p.player.getName().getString() + "&f tem em seu terreno:\n");
            for (int i = 0; i < p.allPokemon.length; i++) {
                sender.message(p.allPokemon[i].getPokemonName());
                for (StatusBase status : p.allPokemon[i].getStatuses()) {
                    sender.message(status.type.name());
                }
                for (EntryHazard status : p.allPokemon[i].getEntryHazards()) {
                    sender.message(status.type.name());
                }
            }
        }
    }
}
