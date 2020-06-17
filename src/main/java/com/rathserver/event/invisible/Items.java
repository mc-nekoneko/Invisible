package com.rathserver.event.invisible;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

/**
 * Created by Nekoneko on 2020/06/17.
 */
public final class Items {

    private static NamespacedKey INVISIBLE_KEY;
    private static ItemStack ACTIVE_INVISIBLE_ITEM;
    private static ItemStack DE_ACTIVE_INVISIBLE_ITEM;

    static void initialize(InvisiblePlugin plugin) {
        INVISIBLE_KEY = new NamespacedKey(plugin, "invisible-item");
        ACTIVE_INVISIBLE_ITEM = createItemStack(Material.ENDER_PEARL, ChatColor.GRAY + "Hide Players");
        DE_ACTIVE_INVISIBLE_ITEM = createItemStack(Material.ENDER_EYE, ChatColor.GRAY + "Show Players");
    }

    private static ItemStack createItemStack(Material material, String name) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName(name);
            itemMeta.getPersistentDataContainer().set(INVISIBLE_KEY, PersistentDataType.STRING, "");
        }
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static NamespacedKey getInvisibleKey() {
        return INVISIBLE_KEY;
    }

    public static ItemStack getInvisibleActiveItem() {
        return ACTIVE_INVISIBLE_ITEM;
    }

    public static ItemStack getInvisibleDeActiveItem() {
        return DE_ACTIVE_INVISIBLE_ITEM;
    }

    private Items() {
        throw new UnsupportedOperationException();
    }
}
