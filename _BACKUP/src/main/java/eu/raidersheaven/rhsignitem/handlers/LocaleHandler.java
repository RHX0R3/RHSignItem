package eu.raidersheaven.rhsignitem.handlers;

import eu.raidersheaven.rhsignitem.configurations.ConfigFile;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class LocaleHandler {

    private static final Map<String, String> messages = new HashMap<>();
    private static JavaPlugin plugin;
    public static String prefix;
    public static List<String> signUsage;
    public static String noPermission;
    public static String noConsoleCommand;
    public static String noItem;
    public static String itemBlacklisted;
    public static String itemUnlocked;
    public static String itemLocked;
    public static String itemSigned;
    public static String itemDeleted;
    public static String itemRenamed;
    public static String itemNotSigned;
    public static String itemNotLocked;
    public static String itemAlreadySigned;
    public static String itemAlreadySignedByPlayer;
    public static String itemAlreadyLocked;
    public static String itemAlreadyLockedByPlayer;
    public static String itemIsLocked;
    public static String itemIsLockedByPlayer;
    public static String itemWrongSignOwner;
    public static String itemWrongLockOwner;
    public static String aboveStackLimit;
    public static String fastSignDisabled;
    public static String itemContainsLore;
    public static String vaultSuccess;
    public static String vaultFail;
    private static final String[] defaultLocales = {"de_DE", "en_US"};


    /**
     * Load on startup
     */
    public static void init(JavaPlugin pluginInstance) {
        plugin = pluginInstance;
        copyDefaultLocales();
        loadLocale();
    }


    /**
     * Lädt die gewählte Sprache aus der config.yml
     */
    public static void loadLocale() {
        plugin.saveDefaultConfig();
        String currentLocale = ConfigFile.locale;
        File localeDir = new File(plugin.getDataFolder(), "locale");

        if (!localeDir.exists()) {
            localeDir.mkdirs();
        }

        File localeFile = new File(localeDir, currentLocale + ".yml");

        if (!localeFile.exists()) {
            plugin.getLogger().warning("Locale file " + currentLocale + ".yml not found! Using default (en_US).");
            localeFile = new File(localeDir, "en_US.yml");
        }

        FileConfiguration localeConfig = YamlConfiguration.loadConfiguration(localeFile);

        for (String key : localeConfig.getKeys(true)) {
            messages.put(key, ColorFormatHandler.formatString(localeConfig.getString(key, key)));
        }

        prefix = localeConfig.getString("prefix", "");
        signUsage = localeConfig.getStringList("help");
        noPermission = localeConfig.getString("no-permission", "");
        noConsoleCommand = localeConfig.getString("no-console-command", "");
        noItem = localeConfig.getString("no-item", "");
        itemBlacklisted = localeConfig.getString("blacklisted", "");
        itemUnlocked = localeConfig.getString("unlock", "");
        itemLocked = localeConfig.getString("lock", "");
        itemSigned = localeConfig.getString("sign", "");
        itemDeleted = localeConfig.getString("delete", "");
        itemRenamed = localeConfig.getString("rename", "");
        itemNotSigned = localeConfig.getString("not-signed", "");
        itemNotLocked = localeConfig.getString("not-locked", "");
        itemAlreadySigned = localeConfig.getString("already-signed", "");
        itemAlreadySignedByPlayer = localeConfig.getString("already-signed-own", "");
        itemAlreadyLocked = localeConfig.getString("already-locked", "");
        itemAlreadyLockedByPlayer = localeConfig.getString("already-locked-own", "");
        itemIsLocked = localeConfig.getString("locked", "");
        itemIsLockedByPlayer = localeConfig.getString("locked-own", "");
        itemWrongSignOwner = localeConfig.getString("wrong-sign-owner", "");
        itemWrongLockOwner = localeConfig.getString("wrong-lock-owner", "");
        aboveStackLimit = localeConfig.getString("stack-limit", "");
        fastSignDisabled = localeConfig.getString("fast-sign-disabled", "");
        itemContainsLore = localeConfig.getString("blocked-lore", "");
        vaultSuccess = localeConfig.getString("vault-withdraw-success", "");
        vaultFail = localeConfig.getString("vault-withdraw-fail", "");
    }


    /**
     * Kopiert die Standard-Locale-Dateien, falls sie nicht existieren
     */
    private static void copyDefaultLocales() {
        File localeDir = new File(plugin.getDataFolder(), "locale");

        if (!localeDir.exists()) {
            localeDir.mkdirs();
        }

        for (String lang : defaultLocales) {

            File localeFile = new File(localeDir, lang + ".yml");

            if (localeFile.exists()) {
                continue;
            }

            InputStream resource = plugin.getResource("locale/" + lang + ".yml");

            if (resource == null) {
                continue;
            }

            try {
                Files.copy(resource, localeFile.toPath());
                plugin.getLogger().info("Copied default locale file: " + lang + ".yml");
                resource.close();
            } catch (Exception e) {
                plugin.getLogger().warning("Could not copy locale file: " + lang + ".yml (" + e.getMessage() + ")");
            }
        }
    }


    /**
     * Holt eine Nachricht anhand des Schlüssels
     *
     * @param key Nachrichtenschlüssel
     * @return Nachricht oder der Schlüssel, falls nicht gefunden
     */
    public static String getMessage(String key) {
        return messages.getOrDefault(key, key);
    }


    /**
     * Lädt die Locale-Dateien neu
     */
    public static void reloadLocale() {
        messages.clear();
        loadLocale();
    }
}
