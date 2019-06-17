package com.rathserver.event.invisible;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

public class InvisiblePlugin extends JavaPlugin {

    static final String METADATA_KEY = "invisible";

    private InvisibleTask task;

    private ItemStack activeInvisibleItem;
    private ItemStack deActiveInvisibleItem;

    @Override
    public void onEnable() {
        this.task = new InvisibleTask(this);
        this.task.runTaskTimer(this, 0L, 60L);
        this.getServer().getPluginManager().registerEvents(new InvisibleListener(this), this);
    }

    @Override
    public void onDisable() {
        if (this.task != null) {
            this.task.cancel();
            this.task = null;
        }
    }

    ItemStack getInvisibleActiveItem() {
        if (this.activeInvisibleItem != null) {
            return this.activeInvisibleItem;
        }

        this.activeInvisibleItem = new ItemStack(Material.ENDER_PEARL);
        ItemMeta itemMeta = this.activeInvisibleItem.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName(ChatColor.GRAY + "Hide Players");
        }
        this.activeInvisibleItem.setItemMeta(itemMeta);

        return this.activeInvisibleItem;
    }

    ItemStack getInvisibleDeActiveItem() {
        if (this.deActiveInvisibleItem != null) {
            return this.deActiveInvisibleItem;
        }

        this.deActiveInvisibleItem = new ItemStack(Material.ENDER_EYE);
        ItemMeta itemMeta = this.deActiveInvisibleItem.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName(ChatColor.GRAY + "Show Players");
        }
        this.deActiveInvisibleItem.setItemMeta(itemMeta);

        return this.deActiveInvisibleItem;
    }

    void hidePlayers(Player player) {
        getServer().getOnlinePlayers().forEach(target -> player.hidePlayer(this, target));
        player.setMetadata(METADATA_KEY, new FixedMetadataValue(this, true));
    }

    void showPlayers(Player player) {
        getServer().getOnlinePlayers().forEach(target -> player.showPlayer(this, target));
        player.removeMetadata(METADATA_KEY, this);
    }
}
