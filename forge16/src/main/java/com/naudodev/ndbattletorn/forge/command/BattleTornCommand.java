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
import com.naudodev.ndbattletorn.forge.service.BattleStartService;
import com.naudodev.ndbattletorn.forge.utils.BattleUtils;
import com.pixelmonmod.pixelmon.battles.BattleRegistry;

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

        BattleStartService service = new BattleStartService(player1, player2, sender, NDBattleTorn.getConfig());
        service.startBattleTorn();
    }
}
