package com.naudodev.ndbattletorn.forge;

import com.envyful.api.concurrency.UtilLogger;
import com.envyful.api.config.yaml.YamlConfigFactory;
import com.envyful.api.forge.chat.ITextComponentTextFormatter;
import com.envyful.api.forge.command.ForgeCommandFactory;
import com.envyful.api.forge.command.parser.ForgeAnnotationCommandParser;
import com.envyful.api.forge.gui.factory.ForgeGuiFactory;
import com.envyful.api.forge.platform.ForgePlatformHandler;
import com.envyful.api.forge.player.ForgePlayerManager;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.platform.PlatformProxy;
import com.naudodev.ndbattletorn.forge.command.BattleTornCommand;
import com.naudodev.ndbattletorn.forge.command.ReloadCommand;
import com.naudodev.ndbattletorn.forge.command.SpectateCommand;
import com.naudodev.ndbattletorn.forge.config.BattleTornConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@Mod("ndbattletorn")
public class NDBattleTorn {

    private static final Logger LOGGER = LogManager.getLogger("ndbattletorn");
    private static NDBattleTorn instance;
    private BattleTornConfig config;
    private ForgePlayerManager playerManager = new ForgePlayerManager();
    private ForgeCommandFactory commandFactory = new ForgeCommandFactory(ForgeAnnotationCommandParser::new, playerManager);

    public NDBattleTorn() {
        UtilLogger.setLogger(LOGGER);

        MinecraftForge.EVENT_BUS.register(this);

        GuiFactory.setPlatformFactory(new ForgeGuiFactory());
        GuiFactory.setPlayerManager(this.playerManager);
        PlatformProxy.setHandler(ForgePlatformHandler.getInstance());
        PlatformProxy.setPlayerManager(this.playerManager);
        PlatformProxy.setTextFormatter(ITextComponentTextFormatter.getInstance());

        instance = this;
    }

    public static NDBattleTorn getInstance() {
        return instance;
    }

    public static ForgePlayerManager getPlayerManager() {
        return instance.playerManager;
    }

    public void reloadConfig() {
        try {
            this.config = YamlConfigFactory.getInstance(BattleTornConfig.class);
            LOGGER.info("BattleTorn : ON");

        } catch (IOException e) {
            getLogger().error("Failed to load config", e);
        }
    }

    @SubscribeEvent
    public void onInit(FMLServerAboutToStartEvent event) {
        this.reloadConfig();
    }

    @SubscribeEvent
    public void onServerStart(RegisterCommandsEvent event) {
        this.commandFactory.registerCommand(event.getDispatcher(), this.commandFactory.parseCommand(new BattleTornCommand()));
        this.commandFactory.registerCommand(event.getDispatcher(), this.commandFactory.parseCommand(new SpectateCommand()));
        this.commandFactory.registerCommand(event.getDispatcher(), this.commandFactory.parseCommand(new ReloadCommand()));
    }

    public static BattleTornConfig getConfig() {
        return instance.config;
    }

    public static Logger getLogger() {
        return LOGGER;
    }
}
