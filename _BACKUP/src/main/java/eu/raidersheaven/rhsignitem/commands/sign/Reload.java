package eu.raidersheaven.rhsignitem.commands.sign;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import eu.raidersheaven.rhsignitem.Main;
import eu.raidersheaven.rhsignitem.handlers.LocaleHandler;
import eu.raidersheaven.rhsignitem.handlers.SoundHandler;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

public class Reload {

    public static LiteralArgumentBuilder<CommandSourceStack> register(Main plugin) {
        return Commands.literal("reload")
                .requires(stack -> stack.getSender().hasPermission("RHSignItem.*") || stack.getSender().hasPermission("RHSignItem.command.*") || stack.getSender().hasPermission("RHSignItem.command.sign.reload"))
                .executes(Reload::reloadConfig);
    }


    /**
     * Reloads the configuration and locale
     * @param context
     * @return
     */
    private static int reloadConfig(CommandContext<CommandSourceStack> context) {
        if (context.getSource().getSender() instanceof Player) {
            SoundHandler.success((Player) context.getSource().getSender());
        }

        Main.get().reloadConfig();
        LocaleHandler.reloadLocale();
        return Command.SINGLE_SUCCESS;
    }
}