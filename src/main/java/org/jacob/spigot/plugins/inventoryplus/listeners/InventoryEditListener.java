package org.jacob.spigot.plugins.inventoryplus.listeners;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.jacob.spigot.plugins.inventoryplus.InventoryPlus;

public class InventoryEditListener implements Listener {

    @EventHandler
    public void onSave(InventoryCloseEvent event) {
        Player p = (Player) event.getPlayer();

        Inventory updated = event.getInventory();

        FileConfiguration data = InventoryPlus.getInstance().getPlayerData();

        data.set("inventories." + InventoryPlus.getEditors().get(p.getUniqueId()) + ".content", InventoryPlus.toBase64(updated));
        InventoryPlus.getInstance().savePlayerData();

        InventoryPlus.getEditors().remove(p.getUniqueId());


    }

}
