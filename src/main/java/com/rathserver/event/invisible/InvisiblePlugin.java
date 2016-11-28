package com.rathserver.event.invisible;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

public class InvisiblePlugin extends JavaPlugin {

    @Getter
    private String prefix = "invisible";

    private ItemStack activeInvisibleItem;
    private ItemStack deActiveInvisibleItem;
    private InvisibleTask task;

    @Override
    public void onEnable() {
        super.onEnable();
        getServer().getPluginManager().registerEvents(new InvisibleListener(this), this);
        (task = new InvisibleTask(this)).runTaskTimer(this, 0L, 60L);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        task.cancel();
        task = null;
    }

    ItemStack getInvisibleActiveItem() {
        if (activeInvisibleItem != null) {
            return activeInvisibleItem;
        }

        activeInvisibleItem = new ItemStack(Material.ENDER_PEARL);
        ItemMeta itemMeta = activeInvisibleItem.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GRAY + "Hide Players");
        activeInvisibleItem.setItemMeta(itemMeta);

        return activeInvisibleItem;
    }

    ItemStack getInvisibleDeActiveItem() {
        if (deActiveInvisibleItem != null) {
            return deActiveInvisibleItem;
        }

        deActiveInvisibleItem = new ItemStack(Material.EYE_OF_ENDER);
        ItemMeta itemMeta = deActiveInvisibleItem.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GRAY + "Show Players");
        deActiveInvisibleItem.setItemMeta(itemMeta);

        return deActiveInvisibleItem;
    }

    void hidePlayers(Player player) {
        getServer().getOnlinePlayers().forEach(player::hidePlayer);
        player.setMetadata(getPrefix(), new FixedMetadataValue(this, true));
    }

    void showPlayers(Player player) {
        getServer().getOnlinePlayers().forEach(player::showPlayer);
        player.removeMetadata(getPrefix(), this);
    }
}
