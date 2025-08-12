package net.weesli.rclaim.config;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.CustomKey;
import eu.okaeri.configs.annotation.Header;
import lombok.Getter;
import lombok.Setter;
import net.weesli.rclaim.config.adapter.model.MenuItem;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@Header("################################################################")
@Header("#                                                              #")
@Header("#    Author by @Weesli                                         #")
@Header("#                                                              #")
@Header("################################################################")
public class Config extends OkaeriConfig {

    @CustomKey("language")
    @Comment("Language for messages and commands. Available languages: 'en', 'tr'")
    private String language = "en";

    @CustomKey("prefix")
    private String prefix = "&6Claim &8>> ";

    @CustomKey("viewerMode")
    @Comment({"System showing the claim zone for players, 'Particle' and 'Border' are available."})
    private String viewerMode = "particle";

    @CustomKey("storageType")
    @Comment({"Storage type can be 'MySQL' or 'SQLite' or 'RozsDBLite", "Default: 'RozsDBLite'"})
    private String storageType = "RozsDBLite";

    @CustomKey("maxTrustedPlayer")
    @Comment({"The maximum number of people a beneficiary can trust.", "Deprecated!!!"})
    private int maxTrustedPlayer = 10;

    @CustomKey("minBetweenClaim")
    @Comment({"Minimum distance from a claim that does not have its own ownership to a claim to be taken", "type of Block size"})
    private int minBetweenClaim = 250;

    @CustomKey("worldGuard")
    private WorldGuardConfig worldGuard = new WorldGuardConfig();

    @CustomKey("mapSupport")
    private MapSupportConfig mapSupport = new MapSupportConfig();

    @CustomKey("combatSystem")
    @Comment("Prevents players from fleeing to their own space when fighting within the server")
    private boolean combatSystem = true;

    @CustomKey("effects")
    private EffectsConfig effects = new EffectsConfig();

    @CustomKey("blockTypes")
    private List<String> blockTypes = Arrays.asList("BEDROCK", "STONE", "DIAMOND_BLOCK", "GLOWSTONE");

    @CustomKey("publicMenuSettings")
    private PublicMenuSettings publicMenu = new PublicMenuSettings();

    @CustomKey("status")
    private StatusConfig status = new StatusConfig();

    @CustomKey("economyType")
    @Comment("Economy type can be 'VAULT' or 'none' or 'PLAYER_POINTS'")
    private String economyType = "VAULT";

    @CustomKey("claimTimeoutMessage")
    private ClaimTimeoutMessageConfig claimTimeoutMessage = new ClaimTimeoutMessageConfig();

    @CustomKey("activeWorlds")
    private List<String> activeWorlds = Arrays.asList("world");

    @CustomKey("enterMessage")
    private EnterMessageConfig enterMessage = new EnterMessageConfig();

    @CustomKey("timeFormat")
    private String timeFormat = "%week%w %day%d %hour%h %minute%m %second%s";

    @CustomKey("database")
    private DatabaseConfig database = new DatabaseConfig();

    @CustomKey("hologram")
    private HologramConfig hologram = new HologramConfig();

    @CustomKey("claimBlock")
    private ClaimBlockConfig claimBlock = new ClaimBlockConfig();

    @CustomKey("claimSettings")
    private ClaimSettingsConfig claimSettings = new ClaimSettingsConfig();

    @CustomKey("ClaimPermissionNames")
    private ClaimPermissionNames claimPermissions = new ClaimPermissionNames();

    @Getter@Setter
    public static class WorldGuardConfig extends OkaeriConfig {
        @CustomKey("enabled")
        private boolean enabled = true;

        @CustomKey("bannedRegions")
        private List<String> bannedRegions = Arrays.asList("example", "Weesli");
    }
    @Getter@Setter
    public static class MapSupportConfig extends OkaeriConfig {
        @CustomKey("dynmapSupport")
        private boolean dynmapSupport = false;
    }
    @Getter@Setter
    public static class EffectsConfig extends OkaeriConfig{
        @CustomKey("enabled")
        private boolean enabled = true;

        @CustomKey("maxLevelMessage")
        private String maxLevelMessage = "&6MAX LEVEL";

        @CustomKey("speed")
        private EffectConfig speed = new EffectConfig(1500, 3500);

        @CustomKey("jump")
        private EffectConfig jump = new EffectConfig(1500, 3500);

        @CustomKey("haste")
        private EffectConfig haste = new EffectConfig(1500, 3500);
        @Getter@Setter
        public static class EffectConfig extends OkaeriConfig {
            @CustomKey("buyCost")
            private int buyCost;

            @CustomKey("upgradeCost")
            private int upgradeCost;

            public EffectConfig(int buyCost, int upgradeCost) {
                this.buyCost = buyCost;
                this.upgradeCost = upgradeCost;
            }
        }
    }
    @Getter@Setter
    public static class StatusConfig extends OkaeriConfig{
        @CustomKey("active")
        private String active = "Active";

        @CustomKey("nonActive")
        private String nonActive = "non-active";
    }

    @Getter@Setter
    public static class ClaimTimeoutMessageConfig extends OkaeriConfig{
        @CustomKey("enabled")
        private boolean enabled = true;

        @CustomKey("text")
        private List<String> text = Arrays.asList("&aThe claim region of &b%player% at coordinates &b%x% &b%z% has been destroyed!");
    }

    @Getter@Setter
    public static class EnterMessageConfig extends OkaeriConfig {
        @CustomKey("enabled")
        private boolean enabled = true;

        @CustomKey("format")
        @Comment("available format 'actionbar' and 'title'")
        private String format = "title";

        @CustomKey("text")
        private String text = "&cClaim owner &f%player%";
    }

    @Getter@Setter
    public static class DatabaseConfig  extends OkaeriConfig{
        @CustomKey("host")
        private String host = "localhost";

        @CustomKey("port")
        private int port = 3306;

        @CustomKey("username")
        private String username = "root";

        @CustomKey("password")
        private String password = "";

        @CustomKey("database")
        private String database = "rclaims";
    }

    @Getter@Setter
    public static class HologramConfig extends OkaeriConfig {
        @CustomKey("enabled")
        private boolean enabled = false;

        @CustomKey("hologramModule")
        private String hologramModule = "DecentHologram";

        @CustomKey("hologramSettings")
        private HologramSettingsConfig hologramSettings = new HologramSettingsConfig();
        @Getter@Setter
        public static class HologramSettingsConfig  extends OkaeriConfig{
            @CustomKey("hologramLines")
            private List<String> hologramLines = Arrays.asList(
                    "&e&lClaim | &f%rclaim_<id>_name%",
                    "&eOwner: &f<player>",
                    "&e",
                    "&eRemaining time: &f%rclaim_<id>_time%",
                    "&eProgressBar: &f%rclaim_<id>_progressbar%",
                    "&e",
                    "&eClick and manage claim!"
            );
        }
    }

    @Getter@Setter
    public static class ClaimBlockConfig extends OkaeriConfig {
        @CustomKey("enabled")
        private boolean enabled = true;

        @CustomKey("item")
        private ClaimBlockItemConfig item = new ClaimBlockItemConfig();
        @Getter@Setter
        public static class ClaimBlockItemConfig extends OkaeriConfig {
            @CustomKey("material")
            private String material = "STONE";

            @CustomKey("customModelData")
            private int customModelData = 0;

            @CustomKey("title")
            private String title = "&aClaim Block";

            @CustomKey("lore")
            private List<String> lore = Arrays.asList(
                    "&7",
                    "&7It allows you to claim the area where you put it.",
                    "&cFor single use only, Operation is irreversible!",
                    "&7",
                    "&7Place and create claim"
            );
        }
    }

    @Getter@Setter
    public static class ClaimSettingsConfig extends OkaeriConfig {
        @CustomKey("claim-cost-per-day")
        @Comment({"Required money for buy claim, 0 for free, -1 for disabled", "day * claimCost"})
        private int claimCostPerDay = 250;

        @CustomKey("claimDuration")
        private int claimDuration = 30;

        @CustomKey("defaultClaimStatus")
        private ClaimStatusConfig defaultClaimStatus = new ClaimStatusConfig();
        @Getter@Setter
        public static class ClaimStatusConfig extends OkaeriConfig {

            @CustomKey("SPAWN_ANIMAL")
            private boolean spawnAnimal = true;

            @CustomKey("SPAWN_MONSTER")
            private boolean spawnMonster = false;

            @CustomKey("EXPLOSION")
            private boolean explosion = false;

            @CustomKey("PVP")
            private boolean pvp = false;
        }
    }

    @Getter@Setter
    public static class PublicMenuSettings extends OkaeriConfig {
        @Comment({"This item will be valid in all paged menus!"})
        private MenuItem previousItem = new MenuItem(
                "&8Previous Page",
                "ARROW",
                0,
                List.of("&8", "&aClick and view the previous page!")
        );
        @Comment({"This item will be valid in all paged menus!"})
        private MenuItem nextItem = new MenuItem(
                "&8Next Page",
                "ARROW",
                0,
                List.of("&8", "&aClick and view the next page!"));
    }

    @Getter@Setter
    public static class ClaimPermissionNames extends OkaeriConfig {

        private String BLOCK_BREAK = "Block Break";
        private String BLOCK_PLACE = "Block Place";
        private String PICKUP_ITEM = "Pickup Item";
        private String DROP_ITEM = "Drop Item";
        private String CONTAINER_OPEN = "Container open";
        private String INTERACT_ENTITY = "Interact Entity";
        private String ATTACK_ANIMAL = "Attack Animal";
        private String ATTACK_MONSTER = "Attack Monster";
        private String BREAK_CONTAINER = "Break Container";
        private String USE_DOOR = "Use Door";
        private String USE_PORTAL = "Use Portal";
        private String USE_POTION = "Use Potion";

    }
}
