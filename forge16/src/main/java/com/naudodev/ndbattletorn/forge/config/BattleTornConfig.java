package com.naudodev.ndbattletorn.forge.config;

import com.envyful.api.config.data.ConfigPath;
import com.envyful.api.config.yaml.AbstractYamlConfig;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigPath("config/BattleTorn/config.yml")
@ConfigSerializable
public class BattleTornConfig extends AbstractYamlConfig {

    @Comment("Mensagem de vitória")
    private String messageWin = "&e&l[TORNEIO] &f%player% ganhou a partida!";

    @Comment("Comando que será executado para o jogador vencedor da batalha, utilize %player% para informar o player e %dimension% para o mundo.")
    private String commandWinner = "execute in %dimension% run tp %player% 560 69 1233\n";

    @Comment("Nome do mundo que poderá ser utilizado no commandWinner ou commandLooser")
    private String dimension = "minecraft:world/plavania";

    @Comment("Comando que será executado para o jogador perdedor da batalha, utilize %player% para informar o player e %dimension% para o mundo.")
    private String commandLooser = "execute in %dimension% run tp %player% 560 69 1233\n";

    public BattleTornConfig() {
        super();
    }

    public String getCommandLooser() {
        return commandLooser;
    }

    public String getMessageWin() {
        return messageWin;
    }

    public String getCommandWinner() {
        return commandWinner;
    }

    public String getDimension() {
        return dimension;
    }
}
