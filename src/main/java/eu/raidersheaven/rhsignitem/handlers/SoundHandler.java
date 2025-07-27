package eu.raidersheaven.rhsignitem.handlers;

import eu.raidersheaven.rhsignitem.configurations.ConfigFile;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

public class SoundHandler {

    /**
     * Sound that plays when you failed to execute
     **/
    public static void fail(Player player) {
        if (ConfigFile.enableSounds) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, SoundCategory.MASTER, 0.4f, 0.6f);
            //player.playSound(player.getLocation(), Sound.ENTITY_ITEM_FRAME_REMOVE_ITEM, 0.1f, 0.3f);
        }
    }


    /**
     * Sound that plays when you made something right
     **/
    public static void success(Player player) {
        if (ConfigFile.enableSounds) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, SoundCategory.MASTER, 0.4f, 0.9f);
            //player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_ELYTRA, 0.2f, 0.9f);
        }
    }


    /**
     * Sound that plays on /sign version
     **/
    public static void version(Player player) {
        if (ConfigFile.enableSounds) {
            player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.MASTER, 0.9f, 1.6f);
        }
    }
}
