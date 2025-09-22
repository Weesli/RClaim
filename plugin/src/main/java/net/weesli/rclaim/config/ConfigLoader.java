package net.weesli.rclaim.config;

import net.weesli.rclaim.config.lang.LangConfig;
import net.weesli.rclaim.config.lang.MenuConfig;
import net.weesli.rozsconfig.serializer.ConfigMapper;
import org.bukkit.plugin.Plugin;

public final class ConfigLoader {

    private static ConfigMapper configMapper;
    private static ConfigMapper langMapper;
    private static ConfigMapper menuMapper;

    private static Config config;
    private static LangConfig lang;
    private static MenuConfig menu;

    private ConfigLoader(Plugin plugin, String langCode){
        configMapper = ConfigMapper.of(Config.class).load(plugin.getResource("config.yml"))
                .file(plugin.getDataPath().resolve("config.yml").toFile());
        langMapper = ConfigMapper.of(LangConfig.class).load(plugin.getResource("lang/" + langCode + "/lang.yml"))
                .file(plugin.getDataPath().resolve("lang").resolve(langCode).resolve("lang.yml").toFile());
        menuMapper = ConfigMapper.of(MenuConfig.class).load(plugin.getResource("lang/" + langCode + "/menus.yml"))
                .file(plugin.getDataPath().resolve("lang").resolve(langCode).resolve("menus.yml").toFile());
        save();
    }

    private void save() {
        configMapper.save(getConfig());
        langMapper.save(getLangConfig());
        menuMapper.save(getMenuConfig());
    }

    public static void create(Plugin plugin, String langCode){
        new ConfigLoader(plugin, langCode);
    }

    public static Config getConfig() {
        if (config == null) {
            config = configMapper.build();
        }
        return config;
    }

    public static LangConfig getLangConfig() {
        if (lang == null) {
            lang = langMapper.build();
        }
        return lang;
    }

    public static MenuConfig getMenuConfig() {
        if (menu == null) {
            menu = menuMapper.build();
        }
        return menu;
    }

    public static void reload(){
        config = configMapper.build();
        lang = langMapper.build();
        menu = menuMapper.build();
    }
}
