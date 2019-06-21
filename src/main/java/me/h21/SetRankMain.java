package me.h21;

import com.google.inject.Inject;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
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

import static org.spongepowered.api.Sponge.getGame;

@Plugin(id = "h21", name = "SetRank", version = "1.0", authors = {"DeLanau/h21"})
public class SetRankMain {

    @Inject
    @DefaultConfig(sharedRoot = false)
    private Path defaultConfig;
    @Inject
    @DefaultConfig(sharedRoot = false)
    private ConfigurationLoader<CommentedConfigurationNode> loader;
    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configDir;

    @Inject
    private Logger logger;

    private ConfigurationNode config;

    @Listener
    public void preInit(GamePreInitializationEvent event){
        try {
            config = loader.load();
            if(!defaultConfig.toFile().exists()){
                config.setValue("Determines length of rank, to prevent super big ranks like 'LOOOOOOOOOOOOOOOOL' etc. Default value 6.");
                config.getNode("args length").setValue(6);
                loader.save(config);
            }
        }catch (IOException e){
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

        /**set player rank**/

        CommandManager cmdManager = Sponge.getCommandManager();
        CommandSpec setrank = CommandSpec.builder()
                .description(Text.of("Sets rank to the player"))
                .permission("setrank.commands.set")
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.string(Text.of("rank"))))
                .executor((CommandSource src, CommandContext args) -> {

                    String rank = args.<String>getOne("rank").get();

                    if(rank.length() > config.getNode("args length").getInt()) {
                        src.sendMessage(Text.of(TextColors.RED, "Too long rank!"));
                        return CommandResult.empty();
                    }

                   cmdManager.process(Sponge.getServer().getConsole(), "lp user "+src.getName()+" meta set rank "+rank);
                    src.sendMessage(Text.of(TextColors.GREEN, "Rank: " +rank+" successful added!"));
                   return CommandResult.success();
                })
                .build();

        /**set rank to other players**/

        CommandSpec setrank2 = CommandSpec.builder()
                .description(Text.of("Sets rank to other player"))
                .permission("setrank.commands.setother")
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.player(Text.of("player"))),
                        GenericArguments.remainingJoinedStrings(Text.of("rank")))
                .executor((CommandSource src, CommandContext args) -> {

                    Player player = args.<Player>getOne("player").get();
                    String rank = args.<String>getOne("rank").get();

                    if(rank.length() > config.getNode("args length").getInt()) {
                        src.sendMessage(Text.of(TextColors.RED, "Too long rank!"));
                        return CommandResult.empty();
                    }

                    if(player.isOnline() == false){
                        src.sendMessage(Text.of(TextColors.RED, "Player is offline!"));
                        return CommandResult.empty();
                    }

                    cmdManager.process(Sponge.getServer().getConsole(), "lp user "+player.getName()+" meta set rank "+rank);
                    src.sendMessage(Text.of(TextColors.GREEN, "Rank: " +rank+" successful added to "+player.getName()));
                    return CommandResult.success();
                })
                .build();

        /**self remove rank**/

        CommandSpec setrank3 = CommandSpec.builder()
                .description(Text.of("Remove rank from player"))
                .permission("setrank.commands.remove")
                .executor((CommandSource src, CommandContext args) -> {

                    cmdManager.process(Sponge.getServer().getConsole(), "lp user "+src.getName()+" meta unset rank");
                    src.sendMessage(Text.of(TextColors.GREEN, "Rank successful removed!"));
                    return CommandResult.success();
                })
                .build();

        /**remove rank from other players**/

        CommandSpec setrank4 = CommandSpec.builder()
                .description(Text.of("Remove rank from other player"))
                .permission("setrank.commands.removeother")
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.player(Text.of("player"))),
                        GenericArguments.remainingJoinedStrings(Text.of("rank")))
                .executor((CommandSource src, CommandContext args) -> {

                    Player player = args.<Player>getOne("player").get();
                    String rank = args.<String>getOne("rank").get();

                    if(rank.length() > config.getNode("args length").getInt()) {
                        src.sendMessage(Text.of(TextColors.RED, "Too long rank!"));
                        return CommandResult.empty();
                    }

                    if(player.isOnline() == false){
                        src.sendMessage(Text.of(TextColors.RED, "Player is offline!"));
                        return CommandResult.empty();
                    }

                    cmdManager.process(Sponge.getServer().getConsole(), "lp user "+player.getName()+" meta unset rank");
                    src.sendMessage(Text.of(TextColors.GREEN, "Rank successful removed from "+player.getName()));

                    return CommandResult.success();
                })
                .build();

        /** register commands**/

        getGame().getCommandManager().register(this, setrank, "rankset", "setrank");
        getGame().getCommandManager().register(this, setrank2, "ranksetother", "setrankother");
        getGame().getCommandManager().register(this, setrank3, "rankremove", "removerank");
        getGame().getCommandManager().register(this, setrank4, "rankremoveother", "removerankother");

    }

}
