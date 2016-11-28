package com.rathserver.event.invisible;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Nekoneko on 2016/11/27.
 */
public class InvisibleTask extends BukkitRunnable {

    private final InvisiblePlugin plugin;

    InvisibleTask(InvisiblePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        plugin.getServer().getOnlinePlayers().forEach(this::check);
    }

    private void check(Player player) {
        if (player.hasMetadata(plugin.getPrefix())) {
            plugin.hidePlayers(player);
        } else {
            plugin.showPlayers(player);
        }
    }
}
