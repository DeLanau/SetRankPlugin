package me.h21;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.game.state.*;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.spongepowered.api.Sponge.getGame;

@Plugin(id = "h21", name = "SetRank", version = "2.0", authors = {"DeLanau/h21"})
public class SetRankMain {

    @Inject
    @DefaultConfig(sharedRoot = true)
    private Path defaultConfig;

    @Inject
    @DefaultConfig(sharedRoot = true)
    private ConfigurationLoader<CommentedConfigurationNode> loader;

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configDir;

    @Inject
    private Logger logger;

    private CommentedConfigurationNode config;

    @Listener
    public void preInit(GamePreInitializationEvent event){
        try {
            config = loader.load();
            if(!defaultConfig.toFile().exists()){
                //create config option character length
                config.getNode("Rank character length")
                        .setComment("Determines length of rank, to prevent super big ranks like 'LOOOOOOOOOOOOOOOOL' etc. Default value 6.")
                        .setValue(6);
                //create config option check rankother true/false
                config.getNode("Check black list and rank lengt for rankother add command").setValue(false);
                //create list
                List<String> list = new ArrayList<>();
                list.add("Admin");
                list.add("Moder");
                //setting up list to the config file
                config.getNode("Black list").setValue(new TypeToken<List<String>>(){}, list);

                loader.save(config);
            }
        }catch (IOException | ObjectMappingException e){
            logger.warn("Error loading default configuration!");
        }
    }
    @Listener
    public void Init(GameInitializationEvent event){
        createAndRegisterCmd();
    }
    @Listener
    public void postInit(GamePostInitializationEvent event){}

    @Listener
    public void onServerStart(GameStartedServerEvent event){
        logger.info("SetRankPlugin have been started");
    }
    @Listener
    public void onServerStop(GameStoppedEvent event){
        logger.info("SetRankPlugin have been stopped");
    }

    private void createAndRegisterCmd(){

        CommandManager cmdManager = Sponge.getCommandManager();

        //rank add command

        CommandSpec rank_add = CommandSpec.builder()
                .description(Text.of("Adds rank"))
                .permission("h21.commands.add")
                .arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("rank"))))
                .executor((CommandSource src, CommandContext args) -> {

                    String rank = args.<String>getOne("rank").get();
                    //black list check
                    try {
                        if(config.getNode("Black list").getList(TypeToken.of(String.class)).toString().toLowerCase().contains(
                                rank.toString().toLowerCase().replaceAll("&.", ""))) {

                            src.sendMessage(Text.of(TextColors.RED, "Your rank is in Black list!"));

                            return CommandResult.empty();
                        }
                    } catch (ObjectMappingException e) {
                        e.printStackTrace();
                    }
                    //character length check
                    if(rank.toString().replaceAll("&.", "").length() > config.getNode("Rank character length").getInt()){

                        src.sendMessage(Text.of(TextColors.RED, "Too long rank! Max character length is: "+config.getNode("Rank character length").getInt()));

                        return CommandResult.empty();
                    }
                    //main thing, adds rank and send message to player
                    cmdManager.process(Sponge.getServer().getConsole(), "lp user "+src.getName()+" meta set rank "+rank);
                    src.sendMessage(Text.of(TextColors.GREEN, "Rank: " +rank+" successful added!"));

                    return CommandResult.success();
                })
                .build();

        //rank remove command

        CommandSpec rank_remove = CommandSpec.builder()
                .description(Text.of("Removes rank"))
                .permission("h21.commands.remove")
                .executor((CommandSource src, CommandContext args) -> {
                //main thing, removes rank and send message to player
                    cmdManager.process(Sponge.getServer().getConsole(), "lp user "+src.getName()+" meta unset rank");
                    src.sendMessage(Text.of(TextColors.GREEN, "Rank successful removed!"));

                    return CommandResult.success();
                })
                .build();

        //rank reload command

        CommandSpec rank_reload = CommandSpec.builder()
                .description(Text.of("Reload config file"))
                .permission("h21.admin.reload")
                .executor((CommandSource src, CommandContext args) -> {
                    //main thing, reloads config file
                    try {
                        loader.load();
                        src.sendMessage(Text.of(TextColors.GREEN, "Config have successful reloaded!"));

                        return CommandResult.success();
                    } catch (IOException e) {
                        e.printStackTrace();
                        src.sendMessage(Text.of(TextColors.RED, "Error reloading config!"));
                        return CommandResult.empty();
                    }
                })
                .build();

        //command rank add/remove

        CommandSpec rank_main = CommandSpec.builder()
                .description(Text.of("Base command for player rank add/remove"))
                .child(rank_add,"add")
                .child(rank_remove, "remove")
                .child(rank_reload, "reload")
                .build();

        //rankother add

        CommandSpec rank_other_add = CommandSpec.builder()
                .description(Text.of("Adds rank to other player"))
                .permission("h21.admin.add")
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.player(Text.of("player"))),
                        GenericArguments.remainingJoinedStrings(Text.of("rank")))
                .executor((CommandSource src, CommandContext args) -> {

                    Player player = args.<Player>getOne("player").get();
                    String rank = args.<String>getOne("rank").get();

                //check for config value
                if(config.getNode("Check black list and rank lengt for rankother add command").getBoolean() == true) {
                    //black list check
                    try {
                        if (config.getNode("Black list").getList(TypeToken.of(String.class)).toString().toLowerCase().contains(
                                rank.toString().toLowerCase().replaceAll("&.", ""))) {

                            src.sendMessage(Text.of(TextColors.RED, "Your rank is in Black list!"));

                            return CommandResult.empty();
                        }
                    } catch (ObjectMappingException e) {
                        e.printStackTrace();
                    }
                    //character length check
                    if (rank.toString().replaceAll("&.", "").length() > config.getNode("Rank character length").getInt()) {

                        src.sendMessage(Text.of(TextColors.RED, "Too long rank! Max character length is: " + config.getNode("Rank character length").getInt()));

                        return CommandResult.empty();
                    }
                }
                    //check if player is online
                    if (player.isOnline() == false) {

                        src.sendMessage(Text.of(TextColors.RED, "Player is offline!"));

                        return CommandResult.empty();
                    }
                    //main thing, adds rank to other player and send message to player
                    cmdManager.process(Sponge.getServer().getConsole(), "lp user "+player.getName()+" meta set rank "+rank);
                    src.sendMessage(Text.of(TextColors.GREEN, "Rank: " +rank+" successful added to "+player.getName()));

                    return CommandResult.success();
                })
                .build();

                //rankother remove command

        CommandSpec rank_other_remove = CommandSpec.builder()
                .description(Text.of("Remove rank from other player"))
                .permission("h21.admin.remove")
                .arguments(GenericArguments.onlyOne(GenericArguments.player(Text.of("player"))))
                .executor((CommandSource src, CommandContext args) -> {

                    Player player = args.<Player>getOne("player").get();
                    //check if player is online
                    if(player.isOnline() == false){

                        src.sendMessage(Text.of(TextColors.RED, "Player is offline!"));

                        return CommandResult.empty();
                    }

                    cmdManager.process(Sponge.getServer().getConsole(), "lp user "+player.getName()+" meta unset rank");
                    src.sendMessage(Text.of(TextColors.GREEN, "Rank successful removed from "+player.getName()));

                    return CommandResult.success();
                })
                .build();

        //command rankother add/remove

        CommandSpec rank_other_main = CommandSpec.builder()
                .description(Text.of("Admin command for adding/removing rank from player/to player"))
                .child(rank_other_add,"add")
                .child(rank_other_remove, "remove")
                .build();

        //command register
        getGame().getCommandManager().register(this, rank_main, "r", "rank");
        getGame().getCommandManager().register(this,rank_other_main,"rankother", "ro");

    }

}
