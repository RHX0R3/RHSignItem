package eu.raidersheaven.rhsignitem.commands.sign;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import eu.raidersheaven.rhsignitem.Main;
import eu.raidersheaven.rhsignitem.configurations.ConfigFile;
import eu.raidersheaven.rhsignitem.handlers.*;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Delete {
    public static LiteralArgumentBuilder<CommandSourceStack> register(Main plugin) {
        return Commands.literal("delete")
                .requires(stack -> (stack.getSender().hasPermission("RHSignItem.*") || stack.getSender().hasPermission("RHSignItem.command.*") || stack.getSender().hasPermission("RHSignItem.command.sign.delete")))
                .executes(Delete::deleteSignature);
    }


    /**
     * Deletes the signature
     * @param context
     * @return
     */
    private static int deleteSignature(CommandContext<CommandSourceStack> context) {
        if (!(context.getSource().getSender() instanceof Player player)) {
            Help.noConsoleCommand(context);
            return 0;
        }
        if (VaultMoneyHandler.isVaultAvailable()) {
            if (!VaultMoneyHandler.checkStatus(player, () -> ConfigFile.vaultActionsDelete)) {
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

        if (meta == null) {
            player.sendMessage(ColorFormatHandler.formatComponent(PAPIHandler.parse(player, LocaleHandler.noItem.replace("%prefix%", LocaleHandler.prefix))));
            SoundHandler.fail(player);
            return 0;
        }

        PersistentDataContainer data = meta.getPersistentDataContainer();
        NamespacedKey signedKey = new NamespacedKey(Main.get(), "signed");
        NamespacedKey ownerKey = new NamespacedKey(Main.get(), "owner");
        NamespacedKey linesKey = new NamespacedKey(Main.get(), "lines");
        NamespacedKey lockedKey = new NamespacedKey(Main.get(), "locked");
        NamespacedKey lockedByKey = new NamespacedKey(Main.get(), "locked-by");

        if (data.has(lockedKey, PersistentDataType.BOOLEAN)) {
            String lockedOwner = Objects.requireNonNull(Bukkit.getPlayer(UUID.fromString(Objects.requireNonNull(data.get(lockedByKey, PersistentDataType.STRING))))).getName();
            String lockedByUUID = data.get(lockedByKey, PersistentDataType.STRING);
            if (lockedByUUID == null || !lockedByUUID.equals(player.getUniqueId().toString())) {
                player.sendMessage(ColorFormatHandler.formatComponent(PAPIHandler.parse(player, LocaleHandler.itemIsLocked.replace("%prefix%", LocaleHandler.prefix).replace("%player%", ColorFormatHandler.formatString(lockedOwner)))));
            } else {
                player.sendMessage(ColorFormatHandler.formatComponent(PAPIHandler.parse(player, LocaleHandler.itemIsLockedByPlayer.replace("%prefix%", LocaleHandler.prefix).replace("%player%", ColorFormatHandler.formatString(lockedOwner)))));
            }
            SoundHandler.fail(player);
            return 0;
        }
        if (!data.has(signedKey, PersistentDataType.BOOLEAN)) {
            player.sendMessage(ColorFormatHandler.formatComponent(PAPIHandler.parse(player, LocaleHandler.itemNotSigned.replace("%prefix%", LocaleHandler.prefix))));
            SoundHandler.fail(player);
            return 0;
        }

        String signOwner = Objects.requireNonNull(Bukkit.getPlayer(UUID.fromString(Objects.requireNonNull(data.get(ownerKey, PersistentDataType.STRING))))).getName();
        String signOwnerUUID = data.get(ownerKey, PersistentDataType.STRING);

        if (signOwnerUUID == null || !signOwnerUUID.equals(player.getUniqueId().toString())) {
            player.sendMessage(ColorFormatHandler.formatComponent(PAPIHandler.parse(player, LocaleHandler.itemWrongSignOwner.replace("%prefix%", LocaleHandler.prefix).replace("%player%", ColorFormatHandler.formatString(signOwner)))));
            SoundHandler.fail(player);
            return 0;
        }
        if (!data.has(linesKey, PersistentDataType.INTEGER)) {
            player.sendMessage(ColorFormatHandler.formatComponent(PAPIHandler.parse(player, LocaleHandler.itemNotSigned.replace("%prefix%", LocaleHandler.prefix))));
            SoundHandler.fail(player);
            return 0;
        }
        if (VaultMoneyHandler.isVaultAvailable()) {
            if (!VaultMoneyHandler.handlePayment(player, () -> ConfigFile.vaultActionsDelete)) {
                return 0;
            }
        }

        data.remove(signedKey);
        data.remove(ownerKey);
        List<Component> lore = meta.lore();

        if (lore != null && !lore.isEmpty()) {
            lore = lore.subList(0, Math.max(0, lore.size() - data.getOrDefault(new NamespacedKey(Main.get(), "lines"), PersistentDataType.INTEGER, 0)));
            meta.lore(lore);
        }

        data.remove(linesKey);
        item.setItemMeta(meta);
        player.sendMessage(ColorFormatHandler.formatComponent(PAPIHandler.parse(player, LocaleHandler.itemDeleted.replace("%prefix%", LocaleHandler.prefix))));
        SoundHandler.success(player);
        return Command.SINGLE_SUCCESS;
    }
}
