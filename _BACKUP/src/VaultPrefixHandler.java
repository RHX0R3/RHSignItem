/*package eu.raidersheaven.rhsignitem.handlers;

import eu.raidersheaven.rhsignitem.Main;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultPrefixHandler {
    private static Chat chat = null;
    private static Permission perms = null;
    private static LuckPerms luckPerms = null;

    public static void setupVault(Main plugin) {
        RegisteredServiceProvider<Chat> rspChat = plugin.getServer().getServicesManager().getRegistration(Chat.class);
        if (rspChat != null) {
            chat = rspChat.getProvider();
        }

        RegisteredServiceProvider<Permission> rspPerms = plugin.getServer().getServicesManager().getRegistration(Permission.class);
        if (rspPerms != null) {
            perms = rspPerms.getProvider();
        }

        if (plugin.getServer().getPluginManager().getPlugin("LuckPerms") != null) {
            luckPerms = LuckPermsProvider.get();
        }
    }

    public static String getFormattedPrefix_old(Player player) {
        if (chat == null || perms == null) {
            return "";
        }

        String prefix = chat.getPlayerPrefix(player);

        if (prefix == null || prefix.isEmpty()) {
            return "";
        }

        return ColorFormatHandler.formatString(prefix);
    }

    public static String getFormattedPrefix(Player player) {
        if (luckPerms != null) {
            User user = luckPerms.getUserManager().getUser(player.getUniqueId());
            if (user != null && user.getCachedData().getMetaData().getPrefix() != null) {
                return ColorFormatHandler.formatString(user.getCachedData().getMetaData().getPrefix());
            }
        }

        if (chat != null && perms != null) {
            String prefix = chat.getPlayerPrefix(player);
            if (prefix != null && !prefix.isEmpty()) {
                return ColorFormatHandler.formatString(prefix);
            }
        }

        return "";
    }
}
*/