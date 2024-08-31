package net.weesli.rClaim;

import net.weesli.rClaim.EconomyManager.EconomyImpl;
import net.weesli.rClaim.EconomyManager.EconomyType;
import net.weesli.rClaim.EconomyManager.PlayerPointsEconomy;
import net.weesli.rClaim.EconomyManager.VaultEconomy;
import net.weesli.rClaim.StorageManager.*;
import net.weesli.rClaim.events.ClaimListener;
import net.weesli.rClaim.events.PlayerListener;
import net.weesli.rClaim.hooks.HPlaceholderAPI;
import net.weesli.rClaim.hooks.Holograms.*;
import net.weesli.rClaim.hooks.Minions.MinionsManager;
import net.weesli.rClaim.hooks.Spawners.SpawnerManager;
import net.weesli.rClaim.management.ClaimManager;
import net.weesli.rClaim.management.modules.ModuleLoader;
import net.weesli.rClaim.tasks.ClaimTask;
import net.weesli.rozsLib.color.ColorBuilder;
import net.weesli.rozsLib.configuration.YamlFileBuilder;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public final class RClaim extends JavaPlugin {

    private static StorageImpl storage;
    private static EconomyImpl economy;
    private static HologramImpl hologram;
    private static SpawnerManager spawnerManager;
    private static MinionsManager minionsManager;

    private static RClaim instance;

    private static YamlFileBuilder menusFile;
    private static YamlFileBuilder messagesFile;
    private static YamlFileBuilder claim_builder;

    public SpawnerManager getSpawnerManager() {
        return spawnerManager;
    }

    public MinionsManager getMinionsManager() {
        return minionsManager;
    }

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
        loadData();
        loadHologram();
        Bukkit.getScheduler().runTaskAsynchronously(this, (this::checkVersion));
        spawnerManager = new SpawnerManager();
        minionsManager = new MinionsManager();
        new Commands(this);
        ModuleLoader.loadAddons(this.getDataFolder().getPath() + "/modules");
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

    private void loadData() {
        Bukkit.getConsoleSender().sendMessage("[RClaim] Loading data...");
        new Loader();
    }

    private void loadStorage() {
        StorageType type = StorageType.valueOf(getConfig().getString("options.storage-type"));
        switch (type){
            case MySQL -> MySQLStorage.getInstance().register();
            case YAML -> new YamlStorage().register();
            case SQLite -> SQLiteStorage.getInstance().register();
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
        menusFile = new YamlFileBuilder(this, "menus").setResource(true);
        menusFile.create();
        claim_builder = new YamlFileBuilder(this, "claims").setPath(new File(this.getDataFolder(), "data"));
        claim_builder.create();
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    @Override
    public void onDisable() {
        for (ClaimTask task : ClaimManager.getTasks()) {
            if (getStorage().hasClaim(task.getClaimId())) {
                storage.updateTime(task);
            }
        }
    }

    public String getMessage(String path){
        return ColorBuilder.convertColors(getConfig().getString("options.prefix") + new YamlFileBuilder(this,"lang").load().getString(path));
    }

    public void setStorage(StorageImpl storage) {
        RClaim.storage = storage;
    }

    public StorageImpl getStorage() {
        return storage;
    }

    public YamlFileBuilder getMenusFile() {
        return menusFile;
    }

    public YamlFileBuilder getMessagesFile() {
        return messagesFile;
    }

    public YamlFileBuilder getClaim_builder() {
        return claim_builder;
    }

    public void setEconomy(EconomyImpl economy) {
        RClaim.economy = economy;
    }

    public EconomyImpl getEconomy() {
        return economy;
    }

    public HologramImpl getHologram() {
        return hologram;
    }

    public static RClaim getInstance() {
        return instance;
    }
}
