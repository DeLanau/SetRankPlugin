package me.h21;

import com.google.inject.Inject;
import me.h21.Commands.*;
import me.h21.Config.Config;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.*;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.io.File;

import static org.spongepowered.api.Sponge.getGame;

@Plugin(id = "h21", name = "SetRank", version = "Recoded", authors = {"DeLanau/h21"}, description = "Set rank plugin recoded")
public class SetRankMain {

    @Inject
    private Logger logger;

    private static SetRankMain instance;

    public static SetRankMain getInstance(){
        return instance;
    }

    @Inject
    @DefaultConfig(sharedRoot = true)
    private ConfigurationLoader<CommentedConfigurationNode> loader;

    @Inject
    @DefaultConfig(sharedRoot = true)
    private File configFile;

    /**Game Init Events**/

    @Listener
    public void preInit(GamePreInitializationEvent event){Config.buildConfig(loader, configFile);}
    @Listener
    public void Init(GameInitializationEvent event){
        createAndRegisterCmd();
    }
    @Listener
    public void postInit(GamePostInitializationEvent event){instance = this;}

    /**Server Events**/

    @Listener
    public void onServerStart(GameStartedServerEvent event){
        logger.info("SetRankPlugin have been started");
    }
    @Listener
    public void onServerStop(GameStoppedEvent event){
        logger.info("SetRankPlugin have been stopped");
    }
    @Listener
    public void onReload(GameReloadEvent event){
        Config.reloadConfig();
        Sponge.getGame().getServer().getBroadcastChannel().send(Text.of(TextColors.GREEN, "Set Rank config have been reloaded!"));
    }

    /** creating and registering commands**/

    private void createAndRegisterCmd(){

        CommandSpec rank_main = CommandSpec.builder()
                .child(CommandSpec.builder()
                        .description(Text.of("Adds rank"))
                        .permission("setrank.commands.add")
                        .arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("rank"))))
                        .executor(new ExecutorAdd()).build(), "add")
                .child(CommandSpec.builder()
                        .description(Text.of("Removes rank"))
                        .permission("setrank.commands.remove")
                        .executor(new ExecutorRemove()).build(), "remove")
                .child(CommandSpec.builder()
                        .description(Text.of("Reload config file"))
                        .permission("setrank.admin.reload")
                        .executor(new ExecutorReload()).build(), "reload")
                .description(Text.of("Base command for player rank add/remove")).build();

        CommandSpec rank_other_main = CommandSpec.builder()
                .child(CommandSpec.builder()
                        .description(Text.of("Adds rank to other player"))
                        .permission("setrank.admin.add")
                        .arguments(GenericArguments.onlyOne(GenericArguments.player(Text.of("player"))),
                                GenericArguments.remainingJoinedStrings(Text.of("rank")))
                        .executor(new ExecutorAddO()).build(), "add")
                .child(CommandSpec.builder()
                        .description(Text.of("Remove rank from other player"))
                        .permission("setrank.admin.remove")
                        .arguments(GenericArguments.onlyOne(GenericArguments.player(Text.of("player"))))
                        .executor(new ExecutorRemoveO()).build(), "remove")
                .description(Text.of("Admin command for adding/removing rank from player/to player")).build();

        //command register
        getGame().getCommandManager().register(this, rank_main, "r", "rank");
        getGame().getCommandManager().register(this, rank_other_main,"rankother", "ro");
    }

    public CommandManager getCmdManager(){

        return Sponge.getCommandManager();
    }

    public ConfigurationLoader<CommentedConfigurationNode> getLoader(){
        return loader;
    }

}
