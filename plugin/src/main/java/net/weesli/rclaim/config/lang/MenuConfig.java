package net.weesli.rclaim.config.lang;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.Variable;
import lombok.Getter;
import lombok.Setter;
import net.weesli.rclaim.config.adapter.model.Menu;
import net.weesli.rclaim.config.adapter.model.MenuItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Getter@Setter
public class MenuConfig extends OkaeriConfig {

    @Variable("main-menu")
    @Comment("Main menu configuration settings")
    public Menu mainMenu = new Menu(
            "&8Claim Management",
            27,
            Map.of(
                    "claims", new MenuItem(4, "&eYour Claims", "GRASS_BLOCK", 0, List.of("&8", "&aClick to view your claims!")),
                    "upgrade-claim", new MenuItem(10, "&eUpgrade Claim", "BEACON", 0, List.of("&8", "&aClick and upgrade your claim!")),
                    "members", new MenuItem(13, "&eManage Members", "PLAYER_HEAD", 0, List.of("&8", "&aClick to manage your members!")),
                    "options", new MenuItem(16, "&eClaim Options", "REDSTONE_BLOCK", 0, List.of("&8", "&aClick and settings your claims!")),
                    "effects", new MenuItem(22, "&eClaim Effects", "BREWING_STAND", 0, List.of("&8", "&aClick to enable or disable claim effects!")),
                    "block", new MenuItem(12, "&eClaim Block", "BEDROCK", 0, List.of("&8", "&aClick to manage claim block!")),
                    "tags", new MenuItem(14, "&eClaim Tags", "NAME_TAG", 0, List.of("&8", "&aClick to manage claim tags!"))
            )
    );

    @Variable("claims-menu")
    @Comment("Claims menu configuration settings")
    public Menu claimsMenu = new Menu(
            "&8Your claims",
            54,
            Map.of(
                    "item-settings", new MenuItem(null,
                            "&eYour claim #<count>",
                            "GRASS_BLOCK",
                            0,
                            List.of("&8",
                                    "&aX: <x>",
                                    "&aZ: <z>",
                                    "&aCountdown: <time>",
                                    "&a",
                                    "&aClick to upgrade time this claim!",
                                    "&aClick Shift + RightClick to unClaim this claim.",
                                    "&aClick Shift + LeftClick to teleport the claim."
                            )
                    )
            )
    );

    @Variable("upgrade-menu")
    @Comment("Upgrade menu configuration settings")
    public Menu upgradeMenu = new Menu(
            "&8Upgrade time your claim",
            9,
            Map.of(
                    "item-settings", new MenuItem(4,
                            "&eUpgrade claim",
                            "BEACON",
                            0,
                            List.of("&8", "&aCost : &f<cost>", "&8", "&aClick to upgrade time your claim!")
                    )
            )
    );


    @Variable("members-menu")
    @Comment("Members menu configuration settings")
    public Menu membersMenu = new Menu(
            "&8Claim members",
            54,
            Map.of(
                    "add-member", new MenuItem(49, "&eAdd Member", "NAME_TAG", 0, List.of("&8", "&aEnter player's name to add them!")),
                    "item-settings", new MenuItem(null,
                            "&eMember, <name>",
                            "PLAYER_HEAD",
                            0,
                            List.of("&a", "&aClick to manage this member!", "&aShift + Click to untrust player")
                    )
            )
    );

    @Variable("options-menu")
    @Comment("Options menu configuration settings")
    public Menu optionsMenu = new Menu(
            "&8Claim options",
            27,
            Map.of(
                    "spawn-animal", new MenuItem(10, "&eSpawn Animal", "SHEEP_SPAWN_EGG", 0, List.of("&7status: &f%status%", "&7", "&7Click to spawn animals on your claim!")),
                    "spawn-monster", new MenuItem(12, "&aSpawn Monster", "SKELETON_SPAWN_EGG", 0, List.of("&7status: &f%status%", "&7", "&7Click to spawn monsters on your claim!")),
                    "pvp", new MenuItem(14, "&aEnable PVP", "DIAMOND_SWORD", 0, List.of("&7status: &f%status%", "&7", "&7Click to enable PVP on your claim!")),
                    "explosion", new MenuItem(16, "&aEnable Explosion", "TNT", 0, List.of("&7status: &f%status%", "&7", "&7Click to enable explosions on your claim!")),
                    "spread", new MenuItem(22, "&aEnable Spread", "FIRE_CHARGE", 0, List.of("&7status: &f%status%", "&7", "&7Click to enable spread on your claim!")),
                    "weather", new MenuItem(19, "&aChange Weather Status", "WATER_BUCKET", 0, List.of("&7status: &f%status%", "&7", "&7Click to change weather status!")),
                    "time", new MenuItem(25, "&aChange Time Status", "CLOCK", 0, List.of("&7status: &f%status%", "&7", "&7Click to change time status!"))
            )
    );

    @Variable("resize-menu")
    @Comment("Resize menu configuration settings")
    public Menu resizeMenu = new Menu(
            "&8Resize your claim",
            54,
            Map.of(
                    "empty-claim", new MenuItem(0, "&eEmpty claim", "WHITE_WOOL", 0, List.of("&8", "&eX : &f<x>", "&eZ : &f<z>", "&e", "&eCost : &f<cost>", "&e", "&aClick to buy this claim")),
                    "self-claim", new MenuItem("&eActive claim", "GREEN_WOOL", 0, List.of("&8", "&eX : &f<x>", "&eZ : &f<z>", "&e")),
                    "not-available-claim", new MenuItem("&eNot available claim", "RED_WOOL", 0, List.of("&8", "&eX : &f<x>", "&eZ : &f<z>")),
                    "starter-claim", new MenuItem("&eYour claim", "BLACK_WOOL", 0, List.of("&8", "&eX : &f<x>", "&eZ : &f<z>"))
            )
    );

    @Variable("permissions-menu")
    @Comment("Permissions menu configuration settings")
    public Menu permissionsMenu = new Menu(
            "Permissions Menu",
            27,
            createPermissionsMenuItems()
    );

    private static Map<String, MenuItem> createPermissionsMenuItems() {
        HashMap<String, MenuItem> items = new HashMap<>();
        items.put("block-break", new MenuItem(1, "&aBlock Break", "STONE_PICKAXE", 0, List.of("&7status: &f%status%", "&7", "&7Toggle block breaking permission")));
        items.put("block-place", new MenuItem(10, "&aBlock Place", "STONE", 0, List.of("&7status: &f%status%", "&7", "&7Toggle block placing permission")));
        items.put("pickup-item", new MenuItem(19, "&aPickup Item", "DIAMOND", 0, List.of("&7status: &f%status%", "&7", "&7Toggle item pickup permission")));
        items.put("drop-item", new MenuItem(3, "&aDrop Item", "IRON_INGOT", 0, List.of("&7status: &f%status%", "&7", "&7Toggle item dropping permission")));
        items.put("container-open", new MenuItem(12, "&aOpen Container", "CHEST", 0, List.of("&7status: &f%status%", "&7", "&7Toggle container opening permission")));
        items.put("interact-entity", new MenuItem(21, "&aInteract Entity", "IRON_SWORD", 0, List.of("&7status: &f%status%", "&7", "&7Toggle entity interaction permission")));
        items.put("attack-animal", new MenuItem(5, "&aAttack Animal", "BOW", 0, List.of("&7status: &f%status%", "&7", "&7Toggle attacking animals permission")));
        items.put("attack-monster", new MenuItem(14, "&aAttack Monster", "DIAMOND_SWORD", 0, List.of("&7status: &f%status%", "&7", "&7Toggle attacking monsters permission")));
        items.put("break-container", new MenuItem(23, "&aBreak Container", "BARREL", 0, List.of("&7status: &f%status%", "&7", "&7Toggle breaking containers permission")));
        items.put("use-door", new MenuItem(7, "&aUse Door", "LEVER", 0, List.of("&7status: &f%status%", "&7", "&7Toggle using doors permission")));
        items.put("use-portal", new MenuItem(16, "&aUse Portal", "OBSIDIAN", 0, List.of("&7status: &f%status%", "&7", "&7Toggle using portals permission")));
        items.put("use-potion", new MenuItem(25, "&aUse Potion", "POTION", 0, List.of("&7status: &f%status%", "&7", "&7Toggle using potions permission")));
        return items;
    }

    @Variable("verify-menu")
    @Comment("Verify menu configuration settings")
    public Menu verifyMenu = new Menu(
            "&8Are you sure?",
            9,
            Map.of(
                    "confirm", new MenuItem(2, "&eYes", "GREEN_WOOL", 0, List.of("&8", "&aClick to confirm your action!")),
                    "deny", new MenuItem(6, "&cNo", "RED_WOOL", 0, List.of("&8", "&aClick to cancel your action!"))
            )
    );

    @Variable("effect-menu")
    @Comment("Effect menu configuration settings")
    public Menu effectMenu = new Menu(
            "&8Effect Menu",
            27,
            Map.of(
                    "speed", new MenuItem(11, "&aSpeed", "DIAMOND_BOOTS", 0, List.of("&7status: &f%status%", "&7Level: &f%level%", "&7", "&7Cost: &f%cost%", "&7", "&7Click and upgrade effect.", "&7Shift-Click and enable/disable effect.")),
                    "jump", new MenuItem(13, "&aJump", "IRON_BOOTS", 0, List.of("&7status: &f%status%", "&7Level: &f%level%", "&7", "&7Cost: &f%cost%", "&7", "&7Click and upgrade effect.", "&7Shift-Click and enable/disable effect.")),
                    "haste", new MenuItem(15, "&aHaste", "GOLDEN_PICKAXE", 0, List.of("&7status: &f%status%", "&7Level: &f%level%", "&7", "&7Cost: &f%cost%", "&7", "&7Click and upgrade effect.", "&7Shift-Click and enable/disable effect."))
            )
    );

    @Variable("block-menu")
    @Comment("Block menu configuration settings")
    public Menu blockMenu = new Menu(
            "&8Setting claim block",
            0,
            Map.of()
    );

    @Variable("tag-main-menu")
    @Comment("Tag main menu configuration settings")
    public Menu tagMainMenu = new Menu(
            "&8Tag Menu",
            0,
            Map.of(
                    "add-tag", new MenuItem(22, "&eAdd Tag", "OAK_SIGN", 0, List.of("&7", "&eClick to add a new tag.", "&7")),
                    "item-settings", new MenuItem(null, "&a<name>", "OAK_SIGN", 0, List.of("&7", "&eClick to change tag permission or users.", "&7Shift-Click and disable tag."))
            )
    );


    @Variable("tag-edit-menu")
    @Comment("Tag editor menu configuration settings")
    public Menu tagEditMenu = new Menu(
            "&8Tag Editor",
            27,
            Map.of(
                    "users", new MenuItem(12, "&eUsers", "PLAYER_HEAD", 0, List.of("&7", "&eAdd/remove users from this tag.", "&7")),
                    "permissions", new MenuItem(14, "&ePermissions", "BOOK", 0, List.of("&7", "&eAdd/remove permission from this tag.", "&7"))
            )
    );


    @Variable("tag-users-menu")
    @Comment("Tag users menu configuration settings")
    public Menu tagUsersMenu = new Menu(
            "&8Tag Users",
            0,
            Map.of(
                    "add-user", new MenuItem(22, "&eAdd User", "NAME_TAG", 0, List.of("&7", "&aClick to add a player to this tag.")),
                    "item-settings", new MenuItem(null, "&e<name>", "PLAYER_HEAD", 0, List.of("&7", "&eClick and remove player from this tag!"))
            )
    );


    @Variable("tag-permissions-menu")
    @Comment("Tag permissions menu configuration settings")
    public Menu tagPermissionsMenu = new Menu(
            "&8Tag Permissions",
            54,
            Map.of(
                    "item-settings", new MenuItem(null, "&a<permission>", "PAPER", 0, List.of("&7", "&aStatus: &f%status%", "&eClick to toggle permission for this tag.", "&7"))
            )
    );

}