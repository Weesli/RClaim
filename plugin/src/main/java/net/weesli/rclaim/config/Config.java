package net.weesli.rclaim.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.weesli.rclaim.config.lang.MenuConfig;
import net.weesli.rozsconfig.annotations.Comment;
import net.weesli.rozsconfig.annotations.ConfigKey;
import net.weesli.rozsconfig.model.RozsConfig;

import java.util.List;

@Getter
@Setter
public class Config extends RozsConfig {

    @ConfigKey("language")
    @Comment("Language for messages and commands. Available languages: 'en', 'tr'")
    private String language;

    @ConfigKey("prefix")
    private String prefix;

    @ConfigKey("viewerMode")
    @Comment({"System showing the claim zone for players, 'classic' and 'Border' are available."})
    private String viewerMode;

    @ConfigKey("storageType")
    @Comment({"Storage type can be 'MySQL' or 'SQLite' or 'RozsDBLite", "Default: 'RozsDBLite'"})
    private String storageType;

    @ConfigKey("maxTrustedPlayer")
    @Comment({"The maximum number of people a beneficiary can trust.", "Deprecated!!!"})
    private int maxTrustedPlayer;

    @ConfigKey("minBetweenClaim")
    @Comment({"Minimum distance from a claim that does not have its own ownership to a claim to be taken", "type of Block size"})
    private int minBetweenClaim;

    @ConfigKey("worldGuard")
    private WorldGuardConfig worldGuard;

    @ConfigKey("mapSupport")
    private MapSupportConfig mapSupport;

    @ConfigKey("combatSystem")
    @Comment("Prevents players from fleeing to their own space when fighting within the server")
    private boolean combatSystem;

    @ConfigKey("effects")
    private EffectsConfig effects;

    @ConfigKey("blockTypes")
    private List<String> blockTypes;

    @ConfigKey("publicMenuSettings")
    private PublicMenuSettings publicMenu;

    @ConfigKey("status")
    private StatusConfig status;

    @ConfigKey("economyType")
    @Comment("Economy type can be 'VAULT' or 'none' or 'PLAYER_POINTS'")
    private String economyType;

    @ConfigKey("claimTimeoutMessage")
    private ClaimTimeoutMessageConfig claimTimeoutMessage;

    @ConfigKey("activeWorlds")
    private List<String> activeWorlds;

    @ConfigKey("enterMessage")
    private EnterMessageConfig enterMessage;

    @ConfigKey("timeFormat")
    private String timeFormat;

    @ConfigKey("database")
    private DatabaseConfig database;

    @ConfigKey("hologram")
    private HologramConfig hologram;

    @ConfigKey("claimBlock")
    private ClaimBlockConfig claimBlock;

    @ConfigKey("claimSettings")
    private ClaimSettingsConfig claimSettings;

    @ConfigKey("ClaimPermissionNames")
    private ClaimPermissionNames claimPermissions;

    @Getter@Setter
    @NoArgsConstructor
    public static class WorldGuardConfig {
        @ConfigKey("enabled")
        private boolean enabled;

        @ConfigKey("bannedRegions")
        private List<String> bannedRegions;
    }
    @Getter@Setter
    @NoArgsConstructor
    public static class MapSupportConfig {
        @ConfigKey("dynmapSupport")
        private boolean dynmapSupport;
    }
    @Getter@Setter
    @NoArgsConstructor
    public static class EffectsConfig{
        @ConfigKey("enabled")
        private boolean enabled;

        @ConfigKey("maxLevelMessage")
        private String maxLevelMessage;

        @ConfigKey("speed")
        private EffectConfig speed;

        @ConfigKey("jump")
        private EffectConfig jump;

        @ConfigKey("haste")
        private EffectConfig haste;
        @Getter@Setter
        @NoArgsConstructor
        public static class EffectConfig {
            @ConfigKey("buyCost")
            private int buyCost;

            @ConfigKey("upgradeCost")
            private int upgradeCost;
        }
    }
    @Getter@Setter
    @NoArgsConstructor
    public static class StatusConfig{
        @ConfigKey("active")
        private String active;

        @ConfigKey("nonActive")
        private String nonActive;
    }

    @Getter@Setter
    @NoArgsConstructor
    public static class ClaimTimeoutMessageConfig{
        @ConfigKey("enabled")
        private boolean enabled;

        @ConfigKey("text")
        private List<String> text;
    }

    @Getter@Setter
    @NoArgsConstructor
    public static class EnterMessageConfig {
        @ConfigKey("enabled")
        private boolean enabled;

        @ConfigKey("format")
        @Comment("available format 'actionbar' and 'title'")
        private String format;

        @ConfigKey("text")
        private String text;
    }

    @Getter@Setter
    @NoArgsConstructor
    public static class DatabaseConfig {
        @ConfigKey("host")
        private String host;

        @ConfigKey("port")
        private int port;

        @ConfigKey("username")
        private String username;

        @ConfigKey("password")
        private String password;

        @ConfigKey("database")
        private String database;
    }

    @Getter@Setter
    @NoArgsConstructor
    public static class HologramConfig {
        @ConfigKey("enabled")
        private boolean enabled;

        @ConfigKey("hologramModule")
        @Comment("Available modules: 'DecentHolograms' and 'FancyHolograms'")
        private String hologramModule;

        @ConfigKey("hologramSettings")
        private HologramSettingsConfig hologramSettings;
        @Getter@Setter
        @NoArgsConstructor
        public static class HologramSettingsConfig {
            @ConfigKey("hologramHeight")
            private float hologramHeight;
            @ConfigKey("hologramLines")
            private List<String> hologramLines;
        }
    }

    @Getter@Setter
    @NoArgsConstructor
    public static class ClaimBlockConfig {
        @ConfigKey("enabled")
        private boolean enabled;

        @ConfigKey("item")
        private ClaimBlockItemConfig item;
        @Getter@Setter
        @NoArgsConstructor
        public static class ClaimBlockItemConfig {
            @ConfigKey("material")
            private String material;

            @ConfigKey("customModelData")
            private int customModelData;

            @ConfigKey("title")
            private String title;

            @ConfigKey("lore")
            private List<String> lore;
        }
    }

    @Getter@Setter

    @NoArgsConstructor
    public static class ClaimSettingsConfig {
        @ConfigKey("claim-cost-per-day")
        @Comment({"Required money for buy claim, 0 for free, -1 for disabled", "day * claimCost"})
        private int claimCostPerDay;

        @ConfigKey("claimDuration")
        private int claimDuration;

        @ConfigKey("defaultClaimStatus")
        private ClaimStatusConfig defaultClaimStatus;
        @Getter@Setter

        @NoArgsConstructor
        public static class ClaimStatusConfig {

            @ConfigKey("SPAWN_ANIMAL")
            private boolean spawnAnimal;

            @ConfigKey("SPAWN_MONSTER")
            private boolean spawnMonster;

            @ConfigKey("EXPLOSION")
            private boolean explosion;

            @ConfigKey("PVP")
            private boolean pvp;
        }
    }

    @Getter@Setter
    @NoArgsConstructor
    public static class PublicMenuSettings {
        @Comment({"This item will be valid in all paged menus!"})
        private MenuConfig.MenuItem previousItem;
        @Comment({"This item will be valid in all paged menus!"})
        private MenuConfig.MenuItem nextItem;
    }

    @Getter@Setter
    @NoArgsConstructor
    public static class ClaimPermissionNames {

        private String BLOCK_BREAK;
        private String BLOCK_PLACE;
        private String PICKUP_ITEM;
        private String DROP_ITEM;
        private String CONTAINER_OPEN;
        private String INTERACT_ENTITY;
        private String ATTACK_ANIMAL;
        private String ATTACK_MONSTER;
        private String BREAK_CONTAINER;
        private String USE_DOOR;
        private String USE_PORTAL;
        private String USE_POTION;

    }
}
