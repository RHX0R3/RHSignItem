package eu.raidersheaven.rhsignitem.commands.sign;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import eu.raidersheaven.rhsignitem.Main;
import eu.raidersheaven.rhsignitem.handlers.*;
import eu.raidersheaven.rhsignitem.configurations.ConfigFile;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;
import java.util.UUID;

public class Unlock {

    public static LiteralArgumentBuilder<CommandSourceStack> register(Main plugin) {
        return Commands.literal("unlock")
                .requires(stack -> (stack.getSender().hasPermission("RHSignItem.*") || stack.getSender().hasPermission("RHSignItem.command.*") || stack.getSender().hasPermission("RHSignItem.command.sign.unlock")))
                .executes(Unlock::unlockItem);
    }


    /**
     * Removes the 'locked' state
     * @param context
     * @return
     */
    private static int unlockItem(CommandContext<CommandSourceStack> context) {
        if (!(context.getSource().getSender() instanceof Player player)) {
            Help.noConsoleCommand(context);
            return 0;
        }
        if (VaultMoneyHandler.isVaultAvailable()) {
            if (!VaultMoneyHandler.checkStatus(player, () -> ConfigFile.vaultActionsUnlock)) {
                return 0;
            }
        }

        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.isEmpty() || item.getType().isAir()) {
            player.sendMessage(ColorFormatHandler.formatComponent(PAPIHandler.parse(player, LocaleHandler.noItem.replace("%prefix%", LocaleHandler.prefix))));
            SoundHandler.fail(player);
            return 0;
        }

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        NamespacedKey lockedKey = new NamespacedKey(Main.get(), "locked");
        NamespacedKey lockedByKey = new NamespacedKey(Main.get(), "locked-by");

        if (!data.has(lockedKey, PersistentDataType.BOOLEAN)) {
            player.sendMessage(ColorFormatHandler.formatComponent(PAPIHandler.parse(player, LocaleHandler.itemNotLocked.replace("%prefix%", LocaleHandler.prefix))));
            SoundHandler.fail(player);
            return 0;
        }
        String lockedOwner = Objects.requireNonNull(Bukkit.getPlayer(UUID.fromString(Objects.requireNonNull(data.get(lockedByKey, PersistentDataType.STRING))))).getName();
        String lockedByUUID = data.get(lockedByKey, PersistentDataType.STRING);
        if (lockedByUUID == null || !lockedByUUID.equals(player.getUniqueId().toString())) {
            player.sendMessage(ColorFormatHandler.formatComponent(PAPIHandler.parse(player, LocaleHandler.itemWrongLockOwner.replace("%prefix%", LocaleHandler.prefix).replace("%player%", ColorFormatHandler.formatString(lockedOwner)))));
            return 0;
        }
        if (VaultMoneyHandler.isVaultAvailable()) {
            if (!VaultMoneyHandler.handlePayment(player, () -> ConfigFile.vaultActionsUnlock)) {
                return 0;
            }
        }

        data.remove(lockedKey);
        data.remove(lockedByKey);
        item.setItemMeta(meta);
        player.sendMessage(ColorFormatHandler.formatComponent(PAPIHandler.parse(player, LocaleHandler.itemUnlocked.replace("%prefix%", LocaleHandler.prefix))));
        SoundHandler.success(player);
        return Command.SINGLE_SUCCESS;
    }

}