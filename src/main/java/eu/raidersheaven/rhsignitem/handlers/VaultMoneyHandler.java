package eu.raidersheaven.rhsignitem.handlers;

import eu.raidersheaven.rhsignitem.Main;
import eu.raidersheaven.rhsignitem.configurations.ConfigFile;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.function.Supplier;

public class VaultMoneyHandler {

    private static final Economy economy = Main.getEconomy();
    private static final boolean isVaultEnabled = Bukkit.getPluginManager().getPlugin("Vault") != null;

    public static boolean isVaultAvailable() {
        return isVaultEnabled;
    }


    /**
     * Check if the player has enough money
     *
     * @param player
     * @param isCommandEnabled
     * @return
     */
    public static boolean checkStatus(Player player, Supplier<Boolean> isCommandEnabled) {
        if (ConfigFile.vaultEnabled && isCommandEnabled.get()) {
            double cost = ConfigFile.vaultCost;
            if (!economy.has(player, cost)) {
                if (ConfigFile.vaultMessagesPaymentFailed) {
                    player.sendMessage(ColorFormatHandler.formatComponent(LocaleHandler.vaultFail.replace("%prefix%", LocaleHandler.prefix).replace("%cost%", String.valueOf(cost))));
                }
                SoundHandler.fail(player);
                return false;
            }
        }
        return true;
    }


    /**
     * Handle the payment for every action
     *
     * @param player
     * @param isCommandEnabled
     * @return
     */
    public static boolean handlePayment(Player player, Supplier<Boolean> isCommandEnabled) {
        if (ConfigFile.vaultEnabled && isCommandEnabled.get()) {
            double cost = ConfigFile.vaultCost;
            if (!economy.has(player, cost)) {
                if (ConfigFile.vaultMessagesPaymentFailed) {
                    player.sendMessage(ColorFormatHandler.formatComponent(LocaleHandler.vaultFail.replace("%prefix%", LocaleHandler.prefix).replace("%cost%", String.valueOf(cost))));
                }
                SoundHandler.fail(player);
                return false;
            }
            economy.withdrawPlayer(player, cost);
            if (ConfigFile.vaultMessagesPaymentSuccessful) {
                player.sendMessage(ColorFormatHandler.formatComponent(LocaleHandler.vaultSuccess.replace("%prefix%", LocaleHandler.prefix).replace("%cost%", String.valueOf(cost))));
            }
        }
        return true;
    }
}
