package com.rathserver.event.invisible.command;

import com.rathserver.event.invisible.InvisiblePlugin;
import com.rathserver.event.invisible.Items;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

/**
 * Created by Nekoneko on 2020/06/17.
 */
public class InvToolCommand implements CommandExecutor {
    private final InvisiblePlugin plugin;

    public InvToolCommand(InvisiblePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;
        PlayerInventory inventory = player.getInventory();
        if (player.hasMetadata(InvisiblePlugin.METADATA_KEY)) {
            inventory.addItem(Items.getInvisibleDeActiveItem().clone());
        } else {
            inventory.addItem(Items.getInvisibleActiveItem().clone());
        }
        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.6F, 0.5F);
        return true;
    }
}
