package eu.raidersheaven.rhsignitem.commands.sign;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import eu.raidersheaven.rhsignitem.Main;
import eu.raidersheaven.rhsignitem.handlers.*;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

import java.util.List;

public class Help {
    public static LiteralArgumentBuilder<CommandSourceStack> register(Main plugin) {
        return Commands.literal("help")
                .requires(stack -> stack.getSender().hasPermission("RHSignItem.*") || stack.getSender().hasPermission("RHSignItem.command.*") || stack.getSender().hasPermission("RHSignItem.command.sign"))
                .executes(Help::displayConfigList);
    }


    /**
     * Displays the help message
     *
     * @param context
     * @return
     */
    public static int displayConfigList(CommandContext<CommandSourceStack> context) {
        List<String> usageMessage = LocaleHandler.signUsage;
        if (usageMessage.isEmpty()) {
            return 0;
        } else {
            usageMessage.forEach(message -> context.getSource().getSender().sendMessage(ColorFormatHandler.formatComponent(message.replace("%prefix%", LocaleHandler.prefix))));
            if (context.getSource().getSender() instanceof Player player) {
                SoundHandler.success(player);
            }
        }
        return Command.SINGLE_SUCCESS;
    }


    /**
     * Console output, if commands are used. Except '/sign help' and '/sign version'
     * @param context
     * @return
     */
    public static int noConsoleCommand(CommandContext<CommandSourceStack> context) {
        context.getSource().getSender().sendMessage(ColorFormatHandler.formatComponent(LocaleHandler.noConsoleCommand.replace("%prefix%", LocaleHandler.prefix)));
        return Command.SINGLE_SUCCESS;
    }
}
