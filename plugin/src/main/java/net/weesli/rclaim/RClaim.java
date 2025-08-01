package net.weesli.rclaim;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.weesli.rclaim.api.RClaimProvider;
import net.weesli.rclaim.database.interfaces.ClaimDatabase;
import net.weesli.rclaim.input.TextInputManager;
import net.weesli.rclaim.manager.CacheManagerImpl;
import net.weesli.rclaim.command.CommandManager;
import net.weesli.rclaim.config.ConfigLoader;
import net.weesli.rclaim.api.enums.StorageType;
import net.weesli.rclaim.hook.manager.*;
import net.weesli.rclaim.hook.other.HBetterRTP;
import net.weesli.rclaim.database.*;
import net.weesli.rclaim.event.ClaimListener;
import net.weesli.rclaim.event.PlayerListener;
import net.weesli.rclaim.hook.other.HPlaceholderAPI;
import net.weesli.rclaim.hook.map.MapLoader;
import net.weesli.rclaim.manager.ClaimManagerImpl;
import net.weesli.rclaim.manager.TagManagerImpl;
import net.weesli.rclaim.task.PublicTask;
import net.weesli.rclaim.ui.UIManager;
import net.weesli.rozslib.RozsLibService;
import net.weesli.rozslib.color.*;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Getter@Setter
public final class RClaim extends JavaPlugin {

    private ClaimDatabase storage;
    private UIManager uiManager;
    private TextInputManager textInputManager;
    private ClaimManagerImpl claimManager;
    private TagManagerImpl tagManager;
    private CacheManagerImpl cacheManager;

    private HologramManager hologramManager;
    private EconomyManager economyManager;
    private SpawnerManager spawnerManager;
    private MinionsManager minionsManager;
    private CombatManager combatManager;

   @Getter private static RClaim instance;

    @Override
    public void onEnable() {
        
        instance = this;
        if (this.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")){
            new HPlaceholderAPI().register();
        }
        if (!loadFile()) return;
        loadListeners();
        loadStorage();
        Bukkit.getScheduler().runTaskAsynchronously(this, (this::checkVersion));
        Bukkit.getScheduler().runTaskTimerAsynchronously(this,Loader::save, 12000L, 12000L);
        spawnerManager = new SpawnerManager();
        minionsManager = new MinionsManager();
        economyManager = new EconomyManager();
        hologramManager = new HologramManager();
        uiManager = new UIManager();
        combatManager = new CombatManager();
        claimManager = new ClaimManagerImpl();
        tagManager = new TagManagerImpl();
        cacheManager = new CacheManagerImpl();
        textInputManager = new TextInputManager(this);
        new CommandManager();
        new Metrics(this, 	23385);
        ModuleLoader.loadAddons(this.getDataFolder().getPath() + "/modules");
        Loader.load();
        new MapLoader();
        // initialize betterRTP hook if plugin is enabled
        if (getServer().getPluginManager().isPluginEnabled("BetterRTP")) new HBetterRTP(this);

        // register RozsLibService in this plugin
        RozsLibService.start(this);

        // start the public task
        new PublicTask();
        // publish RClaimAPI for other plugins
        RClaimProvider.setPlugin(this);
        RClaimProvider.setClaimManager(claimManager);
        RClaimProvider.setCacheManager(cacheManager);
        RClaimProvider.setTagManager(tagManager);

    }

    @Override
    public void onDisable() {
        Loader.save();
        storage.shutdown();
    }

    @SneakyThrows
    private void checkVersion() {
        try {
            HttpClient client = HttpClient.newBuilder().build();
            HttpRequest request = HttpRequest.newBuilder()
                    .setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36")
                    .GET()
                    .uri(new URI("https://api.spigotmc.org/legacy/update.php?resource=119083"))
                    .build();
            HttpResponse<InputStream> connection = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

            if (connection.statusCode() != 200) {
                throw new IOException("Failed to connect to spigotmc.org: " + connection.statusCode());
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.body(), StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            if (!response.toString().equals("2.2.0")) {
                Bukkit.getConsoleSender().sendMessage(ColorBuilder.convertColors("&cNew version of RClaim available! Please update to version &e" + response.toString()));
            }
        } catch (IOException e) {
            Bukkit.getConsoleSender().sendMessage(ColorBuilder.convertColors("&cFailed to check for new version of RClaim."));
        }
    }

    private void loadStorage() {
        StorageType type = StorageType.valueOf(ConfigLoader.getConfig().getStorageType());
        switch (type){
            case MySQL -> storage = new MySQLStorage();
            case SQLite -> storage =  new SQLiteStorage();
            case RozsDBLite -> storage = new RozsDBLite();
        }
        Bukkit.getConsoleSender().sendMessage("[RClaim] register storage type is " + storage.getStorageType().name());
    }

    private void loadListeners() {
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        Bukkit.getPluginManager().registerEvents(new ClaimListener(), this);
    }

    private boolean loadFile(){ // added in 2.3.0
        Bukkit.getConsoleSender().sendMessage(ColorBuilder.convertColors("[RClaim] Loading files.."));
        ConfigLoader.create(this,"en");
        // default language is 'en' but if users change language in config
        // again change plugin language with language in config
        String langCode = ConfigLoader.getConfig().getLanguage();
        if (!langCode.equals("en")){ // if language isn't 'en' in config
            try {
                ConfigLoader.create(this,langCode);
            }catch (IllegalArgumentException e){
                Bukkit.getPluginManager().disablePlugin(this);
                return false;
            }
        }
        return true;
    }

    public String getMessage(String path){
        return ColorBuilder.convertColors(ConfigLoader.getConfig().getPrefix() + ConfigLoader.getLangConfig().get(path));
    }

    public CombatManager getCombatManager() {
        if (combatManager.getCombatIntegration() == null || !combatManager.getCombatIntegration().isEnabled()){
            return null;
        }
        return combatManager;
    }
}
