package org.jacob.spigot.plugins.inventoryplus.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jacob.spigot.plugins.inventoryplus.InventoryPlus;

import java.io.IOException;

public class InventoryCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(label.equalsIgnoreCase("inv")) {
            if(!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You must be a player to execute this command");
                return true;
            }
            Player p = (Player) sender;

            if(!p.hasPermission("inventoryplus.commands.inv")) {
                p.sendMessage(ChatColor.RED + "No permission");
                return true;
            }

            if(args.length != 1) {
                p.sendMessage(ChatColor.RED + "Usage: /inv <player>");
                return true;
            }

            OfflinePlayer t = Bukkit.getOfflinePlayer(args[0]);

            InventoryPlus.getEditors().put(p.getUniqueId(), t.getUniqueId());

            FileConfiguration data = InventoryPlus.getInstance().getPlayerData();

            if(t.isOnline()) {
                p.openInventory(t.getPlayer().getInventory());
            } else {

                try {
                    Inventory inv = InventoryPlus.fromBase64(data.getString("inventories." + t.getUniqueId().toString() + ".content"));

                    Inventory newinv = Bukkit.createInventory(null, 54, t.getName() + "'s inventory");

                    for(ItemStack stack : inv.getContents()) {
                        newinv.addItem(stack);
                    }

                    p.openInventory(newinv);
                } catch (NullPointerException | IOException e) {
                    p.sendMessage(ChatColor.RED + "That player does not exist!");
                    return true;
                }
            }

        }

        return false;
    }
}
