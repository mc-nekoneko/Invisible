package com.rathserver.event.invisible;

/*
 * The MIT License.
 *
 *  Copyright (c) 2019 Nekoneko
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

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
