package com.rathserver.event.invisible.command;

import com.rathserver.event.invisible.InvisiblePlugin;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Nekoneko on 2020/06/17.
 */
public class InvAllCommand implements CommandExecutor {
    private final InvisiblePlugin plugin;

    public InvAllCommand(InvisiblePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;
        this.plugin.hidePlayers(player);
        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.6F, 0.5F);
        return true;
    }
}
