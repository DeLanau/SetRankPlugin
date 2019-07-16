package me.h21.Commands;

import me.h21.Config.Config;
import me.h21.SetRankMain;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class ExecutorAdd implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        String rank = args.<String>getOne("rank").get();

        try {
            if(Config.getBlackList().toString().toLowerCase().contains(
                    rank.toString().toLowerCase().replaceAll("&.", ""))) {

                throw new CommandException(Text.of("Your rank is in Black list!"));

            }else if(Config.getLatinLetters() == true){

                if(!rank.toString().toLowerCase().replaceAll("&.", "").replaceAll("\\p{InCOMBINING_DIACRITICAL_MARKS}+","").matches("[A-Za-z]+"))

                    throw new CommandException(Text.of("Your rank contains non latin letters!"));

            }
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }

        if(rank.toString().replaceAll("&.", "").length() > Config.getRankLength()){

            throw new CommandException(Text.of("Too long rank! Max characters length is : "+Config.getRankLength()));
        }

        SetRankMain.getInstance() .getCmdManager().process(Sponge.getServer().getConsole(), "lp user "+src.getName()+" meta set rank "+rank);
        src.sendMessage(Text.of(TextColors.GREEN, "Rank: " +rank+" successful added!"));

        return CommandResult.success();
    }
}
