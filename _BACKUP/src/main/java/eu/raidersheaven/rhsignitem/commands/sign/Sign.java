package eu.raidersheaven.rhsignitem.commands.sign;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import eu.raidersheaven.rhsignitem.Main;
import eu.raidersheaven.rhsignitem.handlers.*;
import eu.raidersheaven.rhsignitem.configurations.ConfigFile;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Sign {
    public static ArgumentCommandNode<CommandSourceStack, String> register(Main plugin) {
        return Commands.argument("", StringArgumentType.greedyString())
                .requires(stack -> stack.getSender().hasPermission("RHSignItem.*") || stack.getSender().hasPermission("RHSignItem.command.*") || stack.getSender().hasPermission("RHSignItem.command.sign.sign"))
                .executes(Sign::signItem)
                .build();
    }


    /**
     * Format content (full)
     *
     * @param player
     * @param text
     * @return
     */
    private static List<Component> getFormattedLore(Player player, String text) {
        String argumentString = PAPIHandler.parse(player, text).replace("\\n", "\n").replace("\\\n", "\n").replace("<br>", "\n").replace("<newline>", "\n");
        Pattern formatPattern = Pattern.compile("(<[^>]+>|&[0-9a-fk-or])");
        List<String> processedLines = new ArrayList<>();
        String lastFormat = "";

        for (String line : ConfigFile.defaultContent) {
            String parsedLine = PAPIHandler.parse(player, line).replace("%text%", argumentString)
                    .replace("%player%", player.getName())
                    .replace("%date%", DateFormatHandler.getCurrentDate(ConfigFile.formatsDate))
                    .replace("%time%", DateFormatHandler.getCurrentDate(ConfigFile.formatsTime))
                    /*.replace("%group%", VaultPrefixHandler.getFormattedPrefix(player))*/;

            String[] splitLines = parsedLine.split("\n");

            for (int i = 0; i < splitLines.length; i++) {
                Matcher matcher = formatPattern.matcher(splitLines[i]);
                while (matcher.find()) {
                    lastFormat = matcher.group();
                }
                if (i > 0) {
                    splitLines[i] = lastFormat + splitLines[i];
                }
                processedLines.add(splitLines[i]);
            }
        }
        return ColorFormatHandler.formatStringList(processedLines);
    }


    /**
     * Format lore (short)
     *
     * @param player
     * @return
     */
    private static List<Component> handleFastLore(Player player) {
        Pattern formatPattern = Pattern.compile("(<[^>]+>|&[0-9a-fk-or])");
        List<String> processedLines = new ArrayList<>();
        String lastFormat = "";

        for (String line : ConfigFile.shortContent) {
            String parsedLine = PAPIHandler.parse(player, line)
                    .replace("%player%", player.getName())
                    .replace("%date%", DateFormatHandler.getCurrentDate(ConfigFile.formatsDate))
                    .replace("%time%", DateFormatHandler.getCurrentDate(ConfigFile.formatsTime))
                    /*.replace("%group%", VaultPrefixHandler.getFormattedPrefix(player))*/;
            String[] splitLines = parsedLine.split("\n");

            for (int i = 0; i < splitLines.length; i++) {
                Matcher matcher = formatPattern.matcher(splitLines[i]);
                while (matcher.find()) {
                    lastFormat = matcher.group();
                }
                if (i > 0) {
                    splitLines[i] = lastFormat + splitLines[i];
                }
                processedLines.add(splitLines[i]);
            }
        }
        return ColorFormatHandler.formatStringList(processedLines);
    }


    /**
     * Write the lines to the item, that are added on top of the existing lore
     *
     * @param meta
     * @param addedLines
     */
    private static void setLoreLineCount(ItemMeta meta, int addedLines) {
        PersistentDataContainer data = meta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(Main.get(), "lines");
        int currentCount = data.getOrDefault(key, PersistentDataType.INTEGER, 0);
        data.set(key, PersistentDataType.INTEGER, currentCount + addedLines);
    }


    /**
     * Full signature
     *
     * @param context
     * @return
     */
    private static int signItem(CommandContext<CommandSourceStack> context) {
        if (!(context.getSource().getSender() instanceof Player player)) {
            Help.noConsoleCommand(context);
            return 0;
        }
        if (VaultMoneyHandler.isVaultAvailable()) {
            if (!VaultMoneyHandler.checkStatus(player, () -> ConfigFile.vaultActionsSign)) {
                return 0;
            }
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        int stackSize = ConfigFile.maxStackSize;

        if (item.isEmpty() || item.getType().isAir()) {
            player.sendMessage(ColorFormatHandler.formatComponent(PAPIHandler.parse(player, LocaleHandler.noItem.replace("%prefix%", LocaleHandler.prefix))));
            SoundHandler.fail(player);
            return 0;
        }
        if (stackSize <= 0 || stackSize > 64) {
            stackSize = 64;
        }
        if (item.getAmount() > stackSize) {
            player.sendMessage(ColorFormatHandler.formatComponent(PAPIHandler.parse(player, LocaleHandler.aboveStackLimit.replace("%prefix%", LocaleHandler.prefix).replace("%stacksize%", ColorFormatHandler.formatString(String.valueOf(stackSize))))));
            SoundHandler.fail(player);
            return 0;
        }
        if (ConfigFile.itemBlacklist.stream().map(String::toUpperCase).anyMatch(itemType -> itemType.equals(item.getType().toString()))) {
            player.sendMessage(ColorFormatHandler.formatComponent(PAPIHandler.parse(player, LocaleHandler.itemBlacklisted.replace("%prefix%", LocaleHandler.prefix))));
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
        NamespacedKey lockedKey = new NamespacedKey(Main.get(), "locked");
        NamespacedKey lockedByKey = new NamespacedKey(Main.get(), "locked-by");

        if (data.has(lockedKey, PersistentDataType.BOOLEAN)) {
            String lockedOwner = Objects.requireNonNull(Bukkit.getPlayer(UUID.fromString(Objects.requireNonNull(data.get(new NamespacedKey(Main.get(), "locked-by"), PersistentDataType.STRING))))).getName();
            String lockedByUUID = data.get(lockedByKey, PersistentDataType.STRING);
            if (lockedByUUID == null || !lockedByUUID.equals(player.getUniqueId().toString())) {
                player.sendMessage(ColorFormatHandler.formatComponent(PAPIHandler.parse(player, LocaleHandler.itemIsLocked.replace("%prefix%", LocaleHandler.prefix).replace("%player%", ColorFormatHandler.formatString(lockedOwner)))));
            } else {
                player.sendMessage(ColorFormatHandler.formatComponent(PAPIHandler.parse(player, LocaleHandler.itemIsLockedByPlayer.replace("%prefix%", LocaleHandler.prefix).replace("%player%", ColorFormatHandler.formatString(lockedOwner)))));
            }
            SoundHandler.fail(player);
            return 0;
        }
        if (data.has(signedKey, PersistentDataType.BOOLEAN)) {
            String ownerName = Objects.requireNonNull(Bukkit.getPlayer(UUID.fromString(Objects.requireNonNull(data.get(ownerKey, PersistentDataType.STRING))))).getName();
            String ownerUUID = data.get(ownerKey, PersistentDataType.STRING);
            if (ownerUUID == null || !ownerUUID.equals(player.getUniqueId().toString())) {
                player.sendMessage(ColorFormatHandler.formatComponent(PAPIHandler.parse(player, LocaleHandler.itemAlreadySigned.replace("%prefix%", LocaleHandler.prefix).replace("%player%", ColorFormatHandler.formatString(ownerName)))));
            } else {
                player.sendMessage(ColorFormatHandler.formatComponent(PAPIHandler.parse(player, LocaleHandler.itemAlreadySignedByPlayer.replace("%prefix%", LocaleHandler.prefix).replace("%player%", ColorFormatHandler.formatString(ownerName)))));
            }
            SoundHandler.fail(player);
            return 0;
        }

        List<Component> existingLore = meta.lore();

        if (existingLore == null) {
            existingLore = new ArrayList<>();
        } else {
            if (ConfigFile.denyItemsWithLore) {
                player.sendMessage(ColorFormatHandler.formatComponent(PAPIHandler.parse(player, LocaleHandler.itemContainsLore.replace("%prefix%", LocaleHandler.prefix))));
                SoundHandler.fail(player);
                return 0;
            }
        }

        String text = StringArgumentType.getString(context, "");
        List<Component> newLore = getFormattedLore(player, text);
        existingLore.addAll(newLore);
        meta.lore(existingLore);

        if (VaultMoneyHandler.isVaultAvailable()) {
            if (!VaultMoneyHandler.handlePayment(player, () -> ConfigFile.vaultActionsSign)) {
                return 0;
            }
        }

        data.set(new NamespacedKey(Main.get(), "signed"), PersistentDataType.BOOLEAN, true);
        data.set(new NamespacedKey(Main.get(), "owner"), PersistentDataType.STRING, player.getUniqueId().toString());
        setLoreLineCount(meta, newLore.size());
        item.setItemMeta(meta);
        autoLock(player);
        player.sendMessage(ColorFormatHandler.formatComponent(PAPIHandler.parse(player, LocaleHandler.itemSigned.replace("%prefix%", LocaleHandler.prefix))));
        SoundHandler.success(player);

        return Command.SINGLE_SUCCESS;
    }


    /**
     * Short signature
     *
     * @param context
     */
    public static int fastSignItem(CommandContext<CommandSourceStack> context) {
        if (!(context.getSource().getSender() instanceof Player player)) {
            Help.noConsoleCommand(context);
            return 0;
        }
        if (VaultMoneyHandler.isVaultAvailable()) {
            if (!VaultMoneyHandler.checkStatus(player, () -> ConfigFile.vaultActionsSign)) {
                return 0;
            }
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        int stackSize = ConfigFile.maxStackSize;

        if (item.isEmpty() || item.getType().isAir()) {
            player.sendMessage(ColorFormatHandler.formatComponent(PAPIHandler.parse(player, LocaleHandler.noItem.replace("%prefix%", LocaleHandler.prefix))));
            SoundHandler.fail(player);
            return 0;
        }
        if (stackSize <= 0 || stackSize > 64) {
            stackSize = 64;
        }
        if (item.getAmount() > stackSize) {
            player.sendMessage(ColorFormatHandler.formatComponent(PAPIHandler.parse(player, LocaleHandler.aboveStackLimit.replace("%prefix%", LocaleHandler.prefix).replace("%stacksize%", ColorFormatHandler.formatString(String.valueOf(stackSize))))));
            SoundHandler.fail(player);
            return 0;
        }
        if (ConfigFile.itemBlacklist.stream().map(String::toUpperCase).anyMatch(itemType -> itemType.equals(item.getType().toString()))) {
            player.sendMessage(ColorFormatHandler.formatComponent(PAPIHandler.parse(player, LocaleHandler.itemBlacklisted.replace("%prefix%", LocaleHandler.prefix))));
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
        NamespacedKey lockedKey = new NamespacedKey(Main.get(), "locked");
        NamespacedKey lockedByKey = new NamespacedKey(Main.get(), "locked-by");

        if (data.has(lockedKey, PersistentDataType.BOOLEAN)) {
            String lockedOwner = Objects.requireNonNull(Bukkit.getPlayer(UUID.fromString(Objects.requireNonNull(data.get(new NamespacedKey(Main.get(), "locked-by"), PersistentDataType.STRING))))).getName();
            String lockedByUUID = data.get(lockedByKey, PersistentDataType.STRING);
            if (lockedByUUID == null || !lockedByUUID.equals(player.getUniqueId().toString())) {
                player.sendMessage(ColorFormatHandler.formatComponent(PAPIHandler.parse(player, LocaleHandler.itemIsLocked.replace("%prefix%", LocaleHandler.prefix).replace("%player%", ColorFormatHandler.formatString(lockedOwner)))));
            } else {
                player.sendMessage(ColorFormatHandler.formatComponent(PAPIHandler.parse(player, LocaleHandler.itemIsLockedByPlayer.replace("%prefix%", LocaleHandler.prefix).replace("%player%", ColorFormatHandler.formatString(lockedOwner)))));
            }
            SoundHandler.fail(player);
            return 0;
        }
        if (data.has(signedKey, PersistentDataType.BOOLEAN)) {
            String ownerName = Objects.requireNonNull(Bukkit.getPlayer(UUID.fromString(Objects.requireNonNull(data.get(ownerKey, PersistentDataType.STRING))))).getName();
            String ownerUUID = data.get(ownerKey, PersistentDataType.STRING);
            if (ownerUUID == null || !ownerUUID.equals(player.getUniqueId().toString())) {
                player.sendMessage(ColorFormatHandler.formatComponent(PAPIHandler.parse(player, LocaleHandler.itemAlreadySigned.replace("%prefix%", LocaleHandler.prefix).replace("%player%", ColorFormatHandler.formatString(ownerName)))));
            } else {
                player.sendMessage(ColorFormatHandler.formatComponent(PAPIHandler.parse(player, LocaleHandler.itemAlreadySignedByPlayer.replace("%prefix%", LocaleHandler.prefix).replace("%player%", ColorFormatHandler.formatString(ownerName)))));
            }
            SoundHandler.fail(player);
            return 0;
        }

        List<Component> existingLore = meta.lore();

        if (existingLore == null) {
            existingLore = new ArrayList<>();
        } else {
            if (ConfigFile.denyItemsWithLore) {
                player.sendMessage(ColorFormatHandler.formatComponent(PAPIHandler.parse(player, LocaleHandler.itemContainsLore.replace("%prefix%", LocaleHandler.prefix))));
                SoundHandler.fail(player);
                return 0;
            }
        }

        List<Component> newLore = handleFastLore(player);
        existingLore.addAll(newLore);
        meta.lore(existingLore);

        if (VaultMoneyHandler.isVaultAvailable()) {
            if (!VaultMoneyHandler.handlePayment(player, () -> ConfigFile.vaultActionsSign)) {
                return 0;
            }
        }

        data.set(new NamespacedKey(Main.get(), "signed"), PersistentDataType.BOOLEAN, true);
        data.set(new NamespacedKey(Main.get(), "owner"), PersistentDataType.STRING, player.getUniqueId().toString());
        setLoreLineCount(meta, newLore.size());
        item.setItemMeta(meta);
        autoLock(player);
        player.sendMessage(ColorFormatHandler.formatComponent(PAPIHandler.parse(player, LocaleHandler.itemSigned.replace("%prefix%", LocaleHandler.prefix))));
        SoundHandler.success(player);

        return Command.SINGLE_SUCCESS;
    }


    /**
     * Auto Lock (if activated)
     *
     * @param player
     */
    private static void autoLock(Player player) {
        if (!ConfigFile.autoLockEnabled) {
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return;
        }

        PersistentDataContainer data = meta.getPersistentDataContainer();
        NamespacedKey lockedKey = new NamespacedKey(Main.get(), "locked");
        NamespacedKey lockedByKey = new NamespacedKey(Main.get(), "locked-by");

        if (ConfigFile.autoLockPermissionBased) {
            if (player.hasPermission("RHSignItem.*") || player.hasPermission("RHSignItem.command.sign.lock.auto")) {
                data.set(lockedKey, PersistentDataType.BOOLEAN, true);
                data.set(lockedByKey, PersistentDataType.STRING, player.getUniqueId().toString());
                item.setItemMeta(meta);
            }
        } else {
            data.set(lockedKey, PersistentDataType.BOOLEAN, true);
            data.set(lockedByKey, PersistentDataType.STRING, player.getUniqueId().toString());
            item.setItemMeta(meta);
        }
    }
}
