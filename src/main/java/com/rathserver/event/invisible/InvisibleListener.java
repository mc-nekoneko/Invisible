package com.rathserver.event.invisible;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
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
        if (player.hasMetadata(plugin.getPrefix())) {
            player.removeMetadata(plugin.getPrefix(), plugin);
        }

        Inventory inventory = player.getInventory();
        if (inventory.contains(plugin.getInvisibleActiveItem())) inventory.remove(plugin.getInvisibleActiveItem());
        if (inventory.contains(plugin.getInvisibleDeActiveItem())) inventory.remove(plugin.getInvisibleDeActiveItem());

        inventory.setItem(4, plugin.getInvisibleDeActiveItem());
    }

    @EventHandler
    public void playerDeath(PlayerDeathEvent event) {
        event.getDrops().remove(plugin.getInvisibleActiveItem());
        event.getDrops().remove(plugin.getInvisibleDeActiveItem());
    }

    @EventHandler
    public void playerDropItem(PlayerDropItemEvent event) {
        ItemStack drop = event.getItemDrop().getItemStack();
        if (drop.equals(plugin.getInvisibleActiveItem()) || drop.equals(plugin.getInvisibleDeActiveItem())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void pvp(EntityDamageByEntityEvent event) {
        if (event.getDamager().getType() != EntityType.PLAYER || event.getEntityType() != EntityType.PLAYER) {
            return;
        }

        Player target = (Player) event.getEntity();
        Inventory inventory = target.getInventory();
        if (inventory.contains(plugin.getInvisibleActiveItem())) event.setCancelled(true);
    }

    @EventHandler
    public void playerInteract(PlayerInteractEvent event) {
        if ((event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR)
                || !event.hasItem()) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack clickedItem = event.getItem();
        if (clickedItem == null) {
            return;
        }
        if (!clickedItem.equals(plugin.getInvisibleActiveItem()) && !clickedItem.equals(plugin.getInvisibleDeActiveItem())) {
            return;
        }
        if (checkLastUse(player)) {
            if (clickedItem.equals(plugin.getInvisibleActiveItem())) {
                plugin.showPlayers(player);
                int setId = anInt(player.getInventory().getContents(), clickedItem);
                player.getInventory().setItem(setId, plugin.getInvisibleDeActiveItem());
                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.6F, 0.5F);
            }
            if (clickedItem.equals(plugin.getInvisibleDeActiveItem())) {
                plugin.hidePlayers(player);
                int setId = anInt(player.getInventory().getContents(), clickedItem);
                player.getInventory().setItem(setId, plugin.getInvisibleActiveItem());
                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.6F, 0.5F);
            }
        }

        event.setCancelled(true);
        player.updateInventory();
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
        if (lastUsed.containsKey(player.getUniqueId())) {
            long lastUse = lastUsed.get(player.getUniqueId());
            if ((System.currentTimeMillis() - lastUse) <= 3000) {
                player.sendMessage(ChatColor.RED + "3秒間連続して使用できません。");
                return false;
            }
        }

        lastUsed.put(player.getUniqueId(), System.currentTimeMillis());
        return true;
    }
}
