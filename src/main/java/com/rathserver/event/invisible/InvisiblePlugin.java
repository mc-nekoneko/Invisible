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

import com.rathserver.event.invisible.command.InvToolCommand;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

public class InvisiblePlugin extends JavaPlugin {

    public static final String METADATA_KEY = "invisible";

    private InvisibleTask task;

    @Override
    public void onEnable() {
        Items.initialize(this);

        this.task = new InvisibleTask(this);
        this.task.runTaskTimer(this, 0L, 60L);

        this.getServer().getPluginManager().registerEvents(new InvisibleListener(this), this);

        Optional.ofNullable(this.getCommand("invtool")).ifPresent(command -> command.setExecutor(new InvToolCommand(this)));
    }

    @Override
    public void onDisable() {
        if (this.task != null) {
            this.task.cancel();
            this.task = null;
        }
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
