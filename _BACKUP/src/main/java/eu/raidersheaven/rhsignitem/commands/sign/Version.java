package eu.raidersheaven.rhsignitem.commands.sign;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import eu.raidersheaven.rhsignitem.Main;
import eu.raidersheaven.rhsignitem.configurations.ConfigFile;
import eu.raidersheaven.rhsignitem.handlers.LocaleHandler;
import eu.raidersheaven.rhsignitem.handlers.SoundHandler;
import eu.raidersheaven.rhsignitem.handlers.ColorFormatHandler;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class Version {

    public static LiteralArgumentBuilder<CommandSourceStack> register(Main plugin) {
        return Commands.literal("version")
                .requires(stack -> stack.getSender().hasPermission("RHSignItem.*") || stack.getSender().hasPermission("RHSignItem.command.*") || stack.getSender().hasPermission("RHSignItem.command.sign.version"))
                .executes(Version::showInfo);
    }


    /**
     * Shows version information
     *
     * @param context
     * @return
     */
    private static int showInfo(CommandContext<CommandSourceStack> context) {
        String version = JavaPlugin.getPlugin(Main.class).getPluginMeta().getVersion();
        String apiVersion = JavaPlugin.getPlugin(Main.class).getPluginMeta().getAPIVersion();

        if (context.getSource().getSender() instanceof Player) {
            SoundHandler.version((Player) context.getSource().getSender());
        }
        if (Objects.equals(ConfigFile.locale, "de_DE")) {
            context.getSource().getSender().sendMessage(ColorFormatHandler.formatComponent(LocaleHandler.prefix + "<reset><gray> Du nutzt Version <yellow>" + version + "<gray>, erstellt und getestet mit <white>Paper <yellow>" + apiVersion + "+"));
            context.getSource().getSender().sendMessage(ColorFormatHandler.formatComponent(LocaleHandler.prefix + "<reset><gray> Wenn Probleme auftauchen oder Du Fragen hast, kontaktiere mich."));
        } else {
            context.getSource().getSender().sendMessage(ColorFormatHandler.formatComponent(LocaleHandler.prefix + "<reset><gray> You are using version <yellow>" + version + "<gray>, made and tested for <white>Paper <yellow>" + apiVersion + "+"));
            context.getSource().getSender().sendMessage(ColorFormatHandler.formatComponent(LocaleHandler.prefix + "<reset><gray> If you encounter any problems, please contact me."));
        }

        return Command.SINGLE_SUCCESS;
    }
}