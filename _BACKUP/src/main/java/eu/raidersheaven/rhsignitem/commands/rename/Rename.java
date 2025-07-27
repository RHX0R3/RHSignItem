package eu.raidersheaven.rhsignitem.commands.rename;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import eu.raidersheaven.rhsignitem.Main;
import eu.raidersheaven.rhsignitem.commands.sign.Help;
import eu.raidersheaven.rhsignitem.configurations.ConfigFile;
import eu.raidersheaven.rhsignitem.handlers.*;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

public class Rename {

    public static ArgumentCommandNode<CommandSourceStack, String> register(Main plugin) {
        return Commands.argument("", StringArgumentType.greedyString())
                .requires(stack -> (stack.getSender().hasPermission("RHSignItem.*") || stack.getSender().hasPermission("RHSignItem.command.*") || stack.getSender().hasPermission("RHSignItem.command.rename")))
                .executes(Rename::renameItem)
                .build();
    }


    /**
     * Renames the held item
     * @param context
     * @return
     */
    private static int renameItem(CommandContext<CommandSourceStack> context) {
        if (!(context.getSource().getSender() instanceof Player player)) {
            Help.noConsoleCommand(context);
            return 0;
        }
        if (VaultMoneyHandler.isVaultAvailable()) {
            if (!VaultMoneyHandler.checkStatus(player, () -> ConfigFile.vaultActionsRename)) {
                return 0;
            }
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        var argText = context.getArgument("", String.class);

        if (item.isEmpty() || item.getType().isAir()) {
            player.sendMessage(ColorFormatHandler.formatComponent(PAPIHandler.parse(player, LocaleHandler.noItem.replace("%prefix%", ColorFormatHandler.formatString(LocaleHandler.prefix)))));
            SoundHandler.fail(player);
            return 0;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            player.sendMessage(ColorFormatHandler.formatComponent(PAPIHandler.parse(player, LocaleHandler.noItem.replace("%prefix%", ColorFormatHandler.formatString(LocaleHandler.prefix)))));
            SoundHandler.fail(player);
            return 0;
        }

        PersistentDataContainer data = meta.getPersistentDataContainer();
        if (VaultMoneyHandler.isVaultAvailable()) {
            if (!VaultMoneyHandler.handlePayment(player, () -> ConfigFile.vaultActionsRename)) {
                return 0;
            }
        }

        meta.displayName(ColorFormatHandler.formatComponent(PAPIHandler.parse(player, ColorFormatHandler.formatString(argText))));
        item.setItemMeta(meta);
        player.sendMessage(ColorFormatHandler.formatComponent(PAPIHandler.parse(player, LocaleHandler.itemRenamed.replace("%prefix%", ColorFormatHandler.formatString(LocaleHandler.prefix)))));
        SoundHandler.success(player);
        return Command.SINGLE_SUCCESS;
    }
}
