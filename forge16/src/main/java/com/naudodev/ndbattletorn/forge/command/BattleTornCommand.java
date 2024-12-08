package com.naudodev.ndbattletorn.forge.command;

import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.executor.Argument;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Completable;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.command.annotate.permission.Permissible;
import com.envyful.api.forge.command.completion.player.PlayerTabCompleter;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.platform.Messageable;
import com.naudodev.ndbattletorn.forge.NDBattleTorn;
import com.naudodev.ndbattletorn.forge.config.BattleTornConfig;
import com.naudodev.ndbattletorn.forge.utils.BattleUtils;
import com.pixelmonmod.pixelmon.api.battles.BattleEndCause;
import com.pixelmonmod.pixelmon.api.events.battles.BattleEndEvent;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.storage.PlayerPartyStorage;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import com.pixelmonmod.pixelmon.battles.BattleRegistry;
import com.pixelmonmod.pixelmon.battles.api.rules.BattleRuleRegistry;
import com.pixelmonmod.pixelmon.battles.api.rules.BattleRules;
import com.pixelmonmod.pixelmon.battles.api.rules.teamselection.TeamSelectionRegistry;
import com.pixelmonmod.pixelmon.battles.controller.participants.PlayerParticipant;
import net.minecraft.entity.player.ServerPlayerEntity;
import java.util.List;

@Command(
        value = "battletorn"
)
@Permissible(value = "nd.battlemod.mod")
public class BattleTornCommand {

    @CommandProcessor
    public void onCommand(
            @Sender Messageable<?> sender,
            @Completable(PlayerTabCompleter.class) @Argument ForgeEnvyPlayer player1,
            @Completable(PlayerTabCompleter.class) @Argument ForgeEnvyPlayer player2) {

        if (player1.getUniqueId().equals(player2.getUniqueId())) {
            sender.message(BattleUtils.DUPLICATE_PLAYER_IN_BATTLE);
            return;
        }

        if (BattleRegistry.getBattle(player1.getParent()) != null
                || BattleRegistry.getBattle(player2.getParent()) != null) {
            sender.message(BattleUtils.PLAYER_ALREADY_IN_BATTLE);
            return;
        }

        startBattle(NDBattleTorn.getConfig(), player1, player2);
    }

    private void startBattle(BattleTornConfig config, ForgeEnvyPlayer player1, ForgeEnvyPlayer player2) {
        PlayerParticipant participant1 = new PlayerParticipant(player1.getParent(), getPlayerPokemon(player1.getParent()), 0);
        PlayerParticipant participant2 = new PlayerParticipant(player2.getParent(), getPlayerPokemon(player2.getParent()), 0);

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
                    battleController.clearHurtTimer();
                    battleController.addFunctionAtEvent(BattleEndEvent.class, (event, bc) -> {
                        if (event.getCause() == BattleEndCause.NORMAL) {
                            if (isLivePokemon(player1.getParent())) {
                                BattleUtils.winMessage(player1, config);
                                BattleUtils.executeCommand(player1, config.getCommandWinner(), config.getDimension());
                                BattleUtils.executeCommand(player2, config.getCommandLooser(), config.getDimension());
                            } else {
                                BattleUtils.winMessage(player2, config);
                                BattleUtils.executeCommand(player2, config.getCommandWinner(), config.getDimension());
                                BattleUtils.executeCommand(player1, config.getCommandLooser(), config.getDimension());
                            }
                        }
                        return null;
                    });
                })
                .start();
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
