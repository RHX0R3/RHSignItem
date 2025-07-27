package eu.raidersheaven.rhsignitem;

import eu.raidersheaven.bstats.Metrics;
import eu.raidersheaven.rhsignitem.commands.CommandNode;
import eu.raidersheaven.rhsignitem.configurations.ConfigFile;
import eu.raidersheaven.rhsignitem.handlers.LocaleHandler;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin implements Listener {

    private static Main instance;
    private final ConfigFile config = new ConfigFile(this);
    private static Economy vaultEconomy = null;
    MiniMessage miniMessage = MiniMessage.miniMessage();


    /**
     * Startup
     */
    @Override
    public void onEnable() {
        instance = this;
        getPlugins();
        saveDefaultConfig();
        config.set();
        LocaleHandler.init(this);
        loadMetrics();
        registerCommands();
        Bukkit.getConsoleSender().sendMessage(miniMessage.deserialize(" "));
        Bukkit.getConsoleSender().sendMessage(miniMessage.deserialize("<gradient:#FA6F02:#EA034E><bold>   __          __ _              _____ _                 </bold></gradient>"));
        Bukkit.getConsoleSender().sendMessage(miniMessage.deserialize("<gradient:#FA6F02:#EA034E><bold>  /__\\  /\\  /\\/ _(_) __ _ _ __   \\_   \\ |_ ___ _ __ ___  </bold></gradient>"));
        Bukkit.getConsoleSender().sendMessage(miniMessage.deserialize("<gradient:#FA6F02:#EA034E><bold> / \\// / /_/ /\\ \\| |/ _` | '_ \\   / /\\/ __/ _ \\ '_ ` _ \\ </bold></gradient>"));
        Bukkit.getConsoleSender().sendMessage(miniMessage.deserialize("<gradient:#FA6F02:#EA034E><bold>/ _  \\/ __  / _\\ \\ | (_| | | | /\\/ /_ | | | __/ | | | | |</bold></gradient>"));
        Bukkit.getConsoleSender().sendMessage(miniMessage.deserialize("<gradient:#FA6F02:#EA034E><bold>\\/ \\_/\\/ /_/  \\__/_|\\__, |_| |_\\____/  \\__\\___|_| |_| |_|</bold></gradient>"));
        Bukkit.getConsoleSender().sendMessage(miniMessage.deserialize("<gradient:#FA6F02:#EA034E><bold>                    |___/                                </bold></gradient>"));
        Bukkit.getConsoleSender().sendMessage(miniMessage.deserialize("<gray>                                                (" + getPluginMeta().getVersion() + "<gray>)"));
        Bukkit.getConsoleSender().sendMessage(miniMessage.deserialize("<dark_gray>«<white>*<dark_gray>» Adds a personal item signature ☄\uFE0F Customizable!"));
        Bukkit.getConsoleSender().sendMessage(miniMessage.deserialize("<dark_gray>«<white>*<dark_gray>» Made with \uD83D\uDC95 by <gray>X0R3"));
        Bukkit.getConsoleSender().sendMessage(miniMessage.deserialize(" "));

        if (!setupEconomy()) {
            Bukkit.getConsoleSender().sendMessage(miniMessage.deserialize("<gray>Vault <dark_gray>not found! Disabling economy integration."));
        }
    }


    /**
     * Shutdown
     */
    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(miniMessage.deserialize("<dark_gray>Disabling <gradient:#FA6F02:#EA034E><bold>RHSignItem</bold></gradient><dark_gray>."));
    }


    /**
     * Register the commands from the node
     */
    private void registerCommands() {
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS.newHandler(event -> event.registrar().register(CommandNode.createSign(this))));
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS.newHandler(event -> event.registrar().register(CommandNode.createRename(this))));
    }


    /**
     * Get plugin instance
     *
     * @return Plugin instance
     */
    public static Main get() {
        return instance;
    }


    /**
     * Load bstats Metrics
     */
    public void loadMetrics() {
        int pluginId = 11372;
        Metrics metrics = new Metrics(this, pluginId);
    }


    /**
     * Fetch plugins
     */
    private void getPlugins() {
        Bukkit.getPluginManager().getPlugin("PlaceholderAPI");
        Bukkit.getPluginManager().getPlugin("Vault");
    }


    /**
     * Setup economy
     *
     * @return economy
     */
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        vaultEconomy = rsp.getProvider();
        return true;
    }


    /**
     * Get plugin economy
     *
     * @return economy
     */
    public static Economy getEconomy() {
        return vaultEconomy;
    }
}
