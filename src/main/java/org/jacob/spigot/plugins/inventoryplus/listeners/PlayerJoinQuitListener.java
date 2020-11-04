package org.jacob.spigot.plugins.inventoryplus.listeners;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jacob.spigot.plugins.inventoryplus.InventoryPlus;

import java.io.IOException;

public class PlayerJoinQuitListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();

        FileConfiguration data = InventoryPlus.getInstance().getPlayerData();

        data.set("inventories." + p.getUniqueId().toString() + ".content", InventoryPlus
                .toBase64(p.getInventory()));

        InventoryPlus.getInstance().savePlayerData();

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) throws IOException {
        Player p = event.getPlayer();

        FileConfiguration data = InventoryPlus.getInstance().getPlayerData();

        if(!data.contains("inventories." + p.getUniqueId().toString())) {
            return;
        }

        Inventory inv = InventoryPlus.fromBase64(data.getString("inventories." + p.getUniqueId().toString() + ".content"));

        Inventory newinv = p.getInventory();

        newinv.clear();

        for(ItemStack stack : inv.getContents()) {
            newinv.addItem(stack);
        }
    }

}
