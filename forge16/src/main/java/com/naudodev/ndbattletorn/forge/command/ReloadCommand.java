package com.naudodev.ndbattletorn.forge.command;

import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.command.annotate.permission.Permissible;
import com.envyful.api.platform.Messageable;
import com.naudodev.ndbattletorn.forge.NDBattleTorn;

@Command(
        value = "reload"
)
@Permissible(value = "nd.battletorn.admin")
public class ReloadCommand {

    @CommandProcessor
    public void onCommand(@Sender Messageable<?> sender, String[] args) {
        NDBattleTorn.getInstance().reloadConfig();
        sender.message("&e&l(!) &eReloaded configs");
    }
}
