package org.jacob.spigot.plugins.inventoryplus;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jacob.spigot.plugins.inventoryplus.commands.InventoryCommand;
import org.jacob.spigot.plugins.inventoryplus.listeners.InventoryEditListener;
import org.jacob.spigot.plugins.inventoryplus.listeners.PlayerJoinQuitListener;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class InventoryPlus extends JavaPlugin {

    private static InventoryPlus instance;

    public static InventoryPlus getInstance() {
        return instance;
    }

    private static Map<UUID, UUID> editors = new HashMap<UUID,UUID>();

    public static Map<UUID,UUID> getEditors() {
        return editors;
    }

    private File playerDataFile;

    private FileConfiguration playerData;


    @Override
    public void onEnable() {
        instance = this;

        getConfig().options().copyDefaults(true);
        saveConfig();

        try {
            createPlayerData();
            savePlayerData();
        } catch (Exception e) {

        }

        getCommand("inv").setExecutor(new InventoryCommand());

        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new PlayerJoinQuitListener(), instance);
        pm.registerEvents(new InventoryEditListener(), instance);

    }

    private void createPlayerData() {
        this.playerDataFile = new File(getDataFolder(), "data.yml");
        if (!this.playerDataFile.exists()) {
            this.playerDataFile.getParentFile().mkdirs();
            saveResource("data.yml", false);
        }
        this.playerData = (FileConfiguration)new YamlConfiguration();
        try {
            this.playerData.load(this.playerDataFile);
        } catch (IOException |org.bukkit.configuration.InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getPlayerData() {
        return this.playerData;
    }

    public void savePlayerData() {
        try {
            this.playerData.save(this.playerDataFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Inventory fromBase64(String data) throws IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            Inventory inventory = Bukkit.getServer().createInventory(null, dataInput.readInt());

            // Read the serialized inventory
            for (int i = 0; i < inventory.getSize(); i++) {
                inventory.setItem(i, (ItemStack) dataInput.readObject());
            }

            dataInput.close();
            return inventory;
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }

    public static String toBase64(Inventory inventory) throws IllegalStateException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            // Write the size of the inventory
            dataOutput.writeInt(inventory.getSize());

            // Save every element in the list
            for (int i = 0; i < inventory.getSize(); i++) {
                dataOutput.writeObject(inventory.getItem(i));
            }

            // Serialize that array
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }
}
