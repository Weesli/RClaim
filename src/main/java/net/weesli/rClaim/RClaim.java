package net.weesli.rClaim;

import lombok.Getter;
import lombok.Setter;
import net.weesli.rClaim.command.CommandManager;
import net.weesli.rClaim.enums.HologramModule;
import net.weesli.rClaim.enums.StorageType;
import net.weesli.rClaim.hooks.map.HDynmap;
import net.weesli.rClaim.hooks.economy.ClaimEconomy;
import net.weesli.rClaim.enums.EconomyType;
import net.weesli.rClaim.hooks.economy.PlayerPointsEconomy;
import net.weesli.rClaim.hooks.economy.VaultEconomy;
import net.weesli.rClaim.database.*;
import net.weesli.rClaim.events.ClaimListener;
import net.weesli.rClaim.events.PlayerListener;
import net.weesli.rClaim.hooks.HPlaceholderAPI;
import net.weesli.rClaim.hooks.hologram.*;
import net.weesli.rClaim.hooks.map.MapLoader;
import net.weesli.rClaim.hooks.minion.MinionsManager;
import net.weesli.rClaim.hooks.spawner.SpawnerManager;
import net.weesli.rClaim.module.ModuleLoader;
import net.weesli.rClaim.ui.UIManager;
import net.weesli.rozsLib.color.ColorBuilder;
import net.weesli.rozsLib.configuration.YamlFileBuilder;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Getter@Setter
public final class RClaim extends JavaPlugin {

    private Database storage;
    private UIManager uiManager;

    private ClaimEconomy economy;
    private ClaimHologram hologram;
    private SpawnerManager spawnerManager;
    private MinionsManager minionsManager;

   @Getter private static RClaim instance;

    private YamlFileBuilder menusFile;
    private YamlFileBuilder messagesFile;
    private FileConfiguration messages;

    @Override
    public void onEnable() {
        instance = this;
        if (!this.getServer().getPluginManager().isPluginEnabled("RozsLib")){
            Bukkit.getConsoleSender().sendMessage(ColorBuilder.convertColors("&cRozsLib is not found or enabled, please check RozsLib."));
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        if (this.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")){
            new HPlaceholderAPI().register();
        }
        loadFiles();
        loadListeners();
        loadStorage();
        loadEconomy();
        loadHologram();
        Bukkit.getScheduler().runTaskAsynchronously(this, (this::checkVersion));
        spawnerManager = new SpawnerManager();
        minionsManager = new MinionsManager();
        uiManager = new UIManager();
        new CommandManager();
        new Metrics(this, 	23385);
        ModuleLoader.loadAddons(this.getDataFolder().getPath() + "/modules");
        Loader.load();
        new MapLoader();
    }

    @Override
    public void onDisable() {
        Loader.save();
    }

    private void checkVersion() {
        try {
            URL url = new URL("https://api.spigotmc.org/legacy/update.php?resource=119083");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                if (!response.toString().equals(this.getDescription().getVersion())) {
                    Bukkit.getConsoleSender().sendMessage(ColorBuilder.convertColors("&cNew version of RClaim available! Please update to version &e" + response));
                }
            }
        } catch (IOException e) {
            Bukkit.getConsoleSender().sendMessage(ColorBuilder.convertColors("&cFailed to check for new version of RClaim."));
        }
    }


    private void loadHologram() {
        if (getConfig().getBoolean("options.hologram.enabled")){
            HologramModule module = HologramModule.valueOf(getConfig().getString("options.hologram.hologram-module"));
            if (module.equals(HologramModule.DecentHologram)){
                hologram = new HDecentHologram();
            }
            new HologramUpdater();
        }
    }

    private void loadEconomy() {
        EconomyType type = EconomyType.valueOf(getConfig().getString("options.economy-type"));
        switch (type){
            case VAULT -> economy = new VaultEconomy();
            case PLAYER_POINTS -> economy = new PlayerPointsEconomy();
        }
        if (economy != null && economy.isActive()){
            Bukkit.getConsoleSender().sendMessage("[RClaim] register economy type is " + economy.getEconomyType().name());
        }else {
            Bukkit.getConsoleSender().sendMessage(ColorBuilder.convertColors("[RClaim] &cEconomy is not loaded"));
        }
    }


    private void loadStorage() {
        StorageType type = StorageType.valueOf(getConfig().getString("options.storage-type"));
        switch (type){
            case MySQL -> storage = new MySQLStorage();
            case SQLite -> storage =  new SQLiteStorage();
        }
        Bukkit.getConsoleSender().sendMessage("[RClaim] register storage type is " + storage.getStorageType().name());
    }

    private void loadListeners() {
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        Bukkit.getPluginManager().registerEvents(new ClaimListener(), this);
    }

    private void loadFiles() {
        Bukkit.getConsoleSender().sendMessage(ColorBuilder.convertColors("[RClaim] Loading files.."));
        messagesFile = new YamlFileBuilder(this,"lang").setResource(true);
        messagesFile.create();
        messages = messagesFile.load();
        menusFile = new YamlFileBuilder(this, "menus").setResource(true);
        menusFile.create();
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    public String getMessage(String path){
        return ColorBuilder.convertColors(getConfig().getString("options.prefix") + messages.getString(path));
    }
}
