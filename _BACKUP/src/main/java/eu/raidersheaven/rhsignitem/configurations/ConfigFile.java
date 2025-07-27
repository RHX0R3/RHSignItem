package eu.raidersheaven.rhsignitem.configurations;

import eu.raidersheaven.rhsignitem.Main;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class ConfigFile {
    private final Main main;

    public ConfigFile(Main main) {
        this.main = main;
    }


    /**
     * Variables from config.yml
     */
    public static String mode;
    public static List<String> defaultContent;
    public static List<String> shortContent;
    public static String locale;
    public static String formatsDate;
    public static String formatsTime;
    public static Integer maxStackSize;
    public static Boolean autoLockEnabled;
    public static Boolean autoLockPermissionBased;
    public static List<String> itemBlacklist;
    public static Boolean denyItemsWithLore;
    public static Boolean enableSounds;
    public static Boolean vaultEnabled;
    public static Double vaultCost;
    public static Boolean vaultActionsSign;
    public static Boolean vaultActionsDelete;
    public static Boolean vaultActionsLock;
    public static Boolean vaultActionsUnlock;
    public static Boolean vaultActionsRename;
    public static Boolean vaultMessagesPaymentSuccessful;
    public static Boolean vaultMessagesPaymentFailed;


    /**
     * Values from config.yml
     */
    public void set() {
        FileConfiguration cfg = main.getConfig();
        mode = cfg.getString("settings.mode");
        defaultContent = cfg.getStringList("settings.content.DEFAULT");
        shortContent = cfg.getStringList("settings.content.SHORT");
        locale = cfg.getString("settings.locale", "en_US");
        formatsDate = cfg.getString("settings.formats.date");
        formatsTime = cfg.getString("settings.formats.time");
        maxStackSize = Math.min(Math.max(cfg.getInt("settings.max-stack-size", 64), 1), 64);
        autoLockEnabled = cfg.getBoolean("settings.auto-lock.enabled");
        autoLockPermissionBased = cfg.getBoolean("settings.auto-lock.permission-based");
        itemBlacklist = cfg.getStringList("settings.item-blacklist");
        denyItemsWithLore = cfg.getBoolean("settings.deny-items-with-lore");
        enableSounds = cfg.getBoolean("settings.enable-sounds");
        vaultEnabled = cfg.getBoolean("settings.vault.enabled");
        vaultCost = cfg.getDouble("settings.vault.cost");
        vaultActionsSign = cfg.getBoolean("settings.vault.actions.sign");
        vaultActionsDelete = cfg.getBoolean("settings.vault.actions.delete");
        vaultActionsLock = cfg.getBoolean("settings.vault.actions.lock");
        vaultActionsUnlock = cfg.getBoolean("settings.vault.actions.unlock");
        vaultActionsRename = cfg.getBoolean("settings.vault.actions.rename");
        vaultMessagesPaymentSuccessful = cfg.getBoolean("settings.vault.messages.payment-successful");
        vaultMessagesPaymentFailed = cfg.getBoolean("settings.vault.messages.payment-failed");

    }
}