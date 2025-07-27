package eu.raidersheaven.rhsignitem.handlers;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PAPIHandler {

    private static final boolean isPAPIEnabled = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;


    /**
     * Check if PlaceholderAPI is enabled and loaded
     *
     * @return 'true', if PlaceholderAPI is found, else 'false'
     */
    public static boolean isPAPIAvailable() {
        return isPAPIEnabled;
    }


    /**
     * Replace all PlaceholderAPI placeholders within a String
     *
     * @param player Player, used for the placeholder
     * @param text   String, that needs to be formatted
     * @return String with formatted placeholders
     */
    public static String parse(Player player, String text) {
        if (isPAPIAvailable()) {
            return PlaceholderAPI.setPlaceholders(player, text);
        }
        return text;
    }
}
