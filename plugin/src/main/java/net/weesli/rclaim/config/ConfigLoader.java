package net.weesli.rclaim.config;

import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.yaml.snakeyaml.YamlSnakeYamlConfigurer;
import lombok.Getter;
import lombok.Setter;
import net.weesli.rclaim.config.adapter.ClaimAdapter;
import net.weesli.rclaim.config.lang.LangConfig;
import net.weesli.rclaim.config.lang.MenuConfig;
import org.bukkit.plugin.Plugin;
import java.io.File;
import java.util.List;

public final class ConfigLoader {

    private final Plugin plugin;

    @Getter private static Config config;
    @Getter@Setter
    private static LangConfig langConfig;
    @Getter@Setter
    private static MenuConfig menuConfig;

    @Getter private static final List<String> defaultLanguages = List.of( "tr");

    private ConfigLoader(Plugin plugin, String langCode){
        this.plugin = plugin;
        initDefaultDirectory(langCode);
        boolean enableDirectory = checkDirectory(langCode);
        if(!enableDirectory){
            throw new IllegalArgumentException("Language directory not found! Please check the provided language code.");
        }
        applyConfigBuild(Config.class, langCode);
        applyConfigBuild(LangConfig.class, langCode);
        applyConfigBuild(MenuConfig.class, langCode);
    }

    public static void create(Plugin plugin, String langCode){
        new ConfigLoader(plugin, langCode);
    }

    private void initDefaultDirectory(String langCode){
        File folderDir = new File(plugin.getDataFolder(), "lang/" + langCode);
        if (!folderDir.exists()) {
            folderDir.mkdirs();
            if (getDefaultLanguages().contains(langCode)) {
                plugin.saveResource("lang/" + langCode + "/lang.yml", true);
                plugin.saveResource("lang/" + langCode + "/menus.yml", true);
            }
        }
    }

    private  boolean checkDirectory(String langCode){
        File langDir = new File(plugin.getDataFolder(), "lang/" + langCode);
        return langDir.exists() && langDir.isDirectory();
    }

    public void applyConfigBuild(Class<? extends OkaeriConfig> clazz, String langCode){
        if (clazz.equals(Config.class)) {
            config = (Config) ConfigManager.create(clazz)
                    .withConfigurer(new YamlSnakeYamlConfigurer(), new ClaimAdapter())
                    .withBindFile(new File(plugin.getDataFolder(), "config.yml"))
                    .saveDefaults()
                    .load(true);
        } else if (clazz.equals(MenuConfig.class)) {
            menuConfig = (MenuConfig) ConfigManager.create(clazz)
                    .withConfigurer(new YamlSnakeYamlConfigurer(), new ClaimAdapter())
                    .withBindFile(new File(plugin.getDataFolder(), "lang/" + langCode + "/menus.yml"))
                    .saveDefaults()
                    .load(true);
        } else if (clazz.equals(LangConfig.class)) {
            langConfig = (LangConfig) ConfigManager.create(clazz)
                    .withConfigurer(new YamlSnakeYamlConfigurer(), new ClaimAdapter())
                    .withBindFile(new File(plugin.getDataFolder(), "lang/" + langCode + "/lang.yml"))
                    .saveDefaults()
                    .load(true);
        }
    }
}
