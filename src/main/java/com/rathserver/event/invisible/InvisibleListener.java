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
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Nekoneko on 2016/11/27.
 */
public class InvisibleListener implements Listener {

    private final InvisiblePlugin plugin;
    private final Map<UUID, Long> lastUsed = new HashMap<>();

    InvisibleListener(InvisiblePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() != GameMode.SURVIVAL && player.getGameMode() != GameMode.ADVENTURE) {
            return;
        }
        if (player.hasMetadata(InvisiblePlugin.METADATA_KEY)) {
            player.removeMetadata(InvisiblePlugin.METADATA_KEY, this.plugin);
        }

        Inventory inventory = player.getInventory();
        if (inventory.contains(Items.getInvisibleActiveItem()))
            inventory.remove(Items.getInvisibleActiveItem());
        if (inventory.contains(Items.getInvisibleDeActiveItem()))
            inventory.remove(Items.getInvisibleDeActiveItem());

        inventory.setItem(4, Items.getInvisibleDeActiveItem().clone());
    }

    @EventHandler
    public void playerDeath(PlayerDeathEvent event) {
        List<ItemStack> drops = event.getDrops();
        drops.removeIf(itemStack -> itemStack.getItemMeta() != null &&
                itemStack.getItemMeta().getPersistentDataContainer().has(Items.getInvisibleKey(), PersistentDataType.STRING));
    }

    @EventHandler(ignoreCancelled = true)
    public void playerDropItem(PlayerDropItemEvent event) {
        ItemStack itemStack = event.getItemDrop().getItemStack();
        if (itemStack.getItemMeta() != null &&
                itemStack.getItemMeta().getPersistentDataContainer().has(Items.getInvisibleKey(), PersistentDataType.STRING)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void pvp(EntityDamageByEntityEvent event) {
        if (event.getDamager().getType() != EntityType.PLAYER || event.getEntityType() != EntityType.PLAYER) {
            return;
        }

        Player target = (Player) event.getEntity();
        Inventory inventory = target.getInventory();
        if (inventory.contains(Items.getInvisibleActiveItem())) event.setCancelled(true);
    }

    @EventHandler
    public void projectileLaunch(ProjectileLaunchEvent event) {
        Projectile entity = event.getEntity();
        if (entity.getType() != EntityType.ENDER_PEARL && entity.getType() != EntityType.ENDER_SIGNAL) {
            return;
        }
        if (entity.getShooter() instanceof Player) {
            Player shooter = (Player) entity.getShooter();
            ItemStack itemStack = shooter.getInventory().getItemInMainHand();

            if (!itemStack.equals(Items.getInvisibleActiveItem()) && !itemStack.equals(Items.getInvisibleDeActiveItem())) {
                itemStack = shooter.getInventory().getItemInOffHand();
                if (!itemStack.equals(Items.getInvisibleActiveItem()) && !itemStack.equals(Items.getInvisibleDeActiveItem())) {
                    return;
                }
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void playerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack clickedItem = event.getItem();
        if (clickedItem == null) {
            return;
        }
        if (clickedItem.getItemMeta() == null ||
                !clickedItem.getItemMeta().getPersistentDataContainer().has(Items.getInvisibleKey(), PersistentDataType.STRING)) {
            return;
        }
        if (checkLastUse(player)) {
            if (clickedItem.equals(Items.getInvisibleActiveItem())) {
                this.plugin.showPlayers(player);
                int setId = anInt(player.getInventory().getContents(), clickedItem);
                player.getInventory().setItem(setId, Items.getInvisibleDeActiveItem().clone());
                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.6F, 0.5F);
            }
            if (clickedItem.equals(Items.getInvisibleDeActiveItem())) {
                this.plugin.hidePlayers(player);
                int setId = anInt(player.getInventory().getContents(), clickedItem);
                player.getInventory().setItem(setId, Items.getInvisibleActiveItem().clone());
                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.6F, 0.5F);
            }
        }

        event.setCancelled(true);
        player.updateInventory();
    }

    @EventHandler(ignoreCancelled = true)
    public void inventoryClick(InventoryClickEvent event) {
        ItemStack currentItem = event.getCurrentItem();
        if (currentItem == null
                || currentItem.getItemMeta() == null
                || !currentItem.getItemMeta().getPersistentDataContainer().has(Items.getInvisibleKey(), PersistentDataType.STRING)) {
            return;
        }

        switch (event.getView().getTopInventory().getType()) {
            case PLAYER:
            case CREATIVE:
            case CRAFTING:
                break;
            default:
                event.setCancelled(true);
                break;
        }

    }

    private int anInt(ItemStack[] itemStacks, ItemStack target) {
        for (int i = 0; i < itemStacks.length; i++) {
            ItemStack itemStack = itemStacks[i];
            if (itemStack != null && itemStack.equals(target)) {
                return i;
            }
        }

        return 0;
    }

    private boolean checkLastUse(Player player) {
        if (this.lastUsed.containsKey(player.getUniqueId())) {
            long lastUse = this.lastUsed.get(player.getUniqueId());
            if ((System.currentTimeMillis() - lastUse) <= 3000) {
                player.sendMessage(ChatColor.RED + "3秒間連続して使用できません。");
                return false;
            }
        }

        this.lastUsed.put(player.getUniqueId(), System.currentTimeMillis());
        return true;
    }
}
