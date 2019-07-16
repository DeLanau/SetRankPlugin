package me.h21.Commands;

import me.h21.Config.Config;
import me.h21.SetRankMain;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class ExecutorAddO implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        Player player = args.<Player>getOne("player").get();
        String rank = args.<String>getOne("rank").get();

        if(Config.getCheckother() == true) {

            try {
                if (Config.getBlackList().toString().toLowerCase().contains(
                        rank.toString().toLowerCase().replaceAll("&.", ""))) {


                    throw  new CommandException(Text.of("Your rank is in Black list!"));
                }
            } catch (ObjectMappingException e) {
                e.printStackTrace();
            }

            if (rank.toString().replaceAll("&.", "").length() > Config.getRankLength()) {

                throw new CommandException(Text.of("Too long rank! Max characters length is : " + Config.getRankLength()));
            }
            if(Config.getLatinLetters() == true){

                if(!rank.toString().toLowerCase().replaceAll("&.", "").replaceAll("\\p{InCOMBINING_DIACRITICAL_MARKS}+","").matches("[A-Za-z]+"))

                    throw new CommandException(Text.of("Your rank contains non latin letters!"));
            }
        }

        if (player.isOnline() == false) {

            throw new CommandException(Text.of("Player is offline!"));
        }

        SetRankMain.getInstance().getCmdManager().process(Sponge.getServer().getConsole(), "lp user "+player.getName()+" meta set rank "+rank);
        src.sendMessage(Text.of(TextColors.GREEN, "Rank: " +rank+" successful added to "+player.getName()));

        return CommandResult.success();
    }
}
