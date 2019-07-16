package me.h21.Commands;

import me.h21.SetRankMain;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class ExecutorRemove implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        SetRankMain.getInstance().getCmdManager().process(Sponge.getServer().getConsole(), "lp user "+src.getName()+" meta unset rank");
        src.sendMessage(Text.of(TextColors.GREEN, "Rank successful removed!"));

        return CommandResult.success();
    }
}
