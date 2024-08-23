package net.weesli.rClaim;

import net.weesli.rClaim.EconomyManager.EconomyImpl;
import net.weesli.rClaim.EconomyManager.EconomyType;
import net.weesli.rClaim.EconomyManager.VaultEconomy;
import net.weesli.rClaim.StorageManager.*;
import net.weesli.rClaim.events.ClaimListener;
import net.weesli.rClaim.events.PlayerListener;
import net.weesli.rClaim.hooks.HPlaceholderAPI;
import net.weesli.rClaim.hooks.Holograms.*;
import net.weesli.rClaim.hooks.Spawners.SpawnerManager;
import net.weesli.rClaim.management.ClaimManager;
import net.weesli.rClaim.tasks.ClaimTask;
import net.weesli.rozsLib.ColorManager.ColorBuilder;
import net.weesli.rozsLib.ConfigurationManager.YamlFileBuilder;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class RClaim extends JavaPlugin {

    private static StorageImpl storage;
    private static EconomyImpl economy;
    private static HologramImpl hologram;

    private static RClaim instance;

    private static YamlFileBuilder menusFile;
    private static YamlFileBuilder messagesFile;
    private static YamlFileBuilder claim_builder;

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
        new SpawnerManager();
        new Commands(this);
    }


    private void loadHologram() {
        if (getConfig().getBoolean("options.hologram.enabled")){
            HologramModule module = HologramModule.valueOf(getConfig().getString("options.hologram.hologram-module"));
            switch (module){
                case DecentHologram -> hologram = new HDecentHologram();
                default -> Bukkit.getConsoleSender().sendMessage(ColorBuilder.convertColors("&cInvalid hologram module, please check config."));
            }
            new HologramUpdater();
        }
    }

    private void loadEconomy() {
        EconomyType type = EconomyType.valueOf(getConfig().getString("options.economy-type"));
        switch (type){
            case VAULT -> new VaultEconomy().register();
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
            case YAML -> new YamlStorage().register();
            case MySQL -> new MySQLStorage().register();
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
        for (ClaimTask task : ClaimManager.getTasks()){
            StorageType type = getStorage().getStorageType();
            switch (type){
                case YAML:
                    if(getStorage().hasClaim(task.getClaimId())){
                        claim_builder.load().set("claims." + task.getClaimId() + ".time", task.getTime());
                        claim_builder.save();
                    }
                    break;
                case MySQL:
                    MySQLStorage.getInstance().updateClaim(MySQLStorage.getInstance().getClaim(task.getClaimId()));
                    break;
            }
        }
    }

    public static RClaim getInstance() {
        return instance;
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
}
