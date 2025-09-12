package net.weesli.rclaim.config.lang;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.Variable;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.weesli.rclaim.config.ConfigLoader;
import net.weesli.rozslib.inventory.ClickableItemStack;
import net.weesli.rozslib.inventory.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.ArrayList;
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
            ),
            true,
            new ArrayList<>()
    );

    @Variable("claims-menu")
    @Comment("Claims menu configuration settings")
    public PageableMenu claimsMenu = new PageableMenu(
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
            ),
            false,
            new ArrayList<>(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 18, 27, 36, 44,46, 47, 48, 49, 50, 51, 52, 53, 17, 26, 35,45,53)),
            new NavigationItem(ConfigLoader.getConfig().getPublicMenu().getPreviousItem(), 45),
            new NavigationItem(ConfigLoader.getConfig().getPublicMenu().getNextItem(), 53)
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
            ),
            true,
            new ArrayList<>()
    );


    @Variable("members-menu")
    @Comment("Members menu configuration settings")
    public PageableMenu membersMenu = new PageableMenu(
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
            ),
            false,
            new ArrayList<>(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 18, 27, 36, 44,46, 47, 48, 50, 51, 52, 53, 17, 26, 35,45,53)),
            new NavigationItem(ConfigLoader.getConfig().getPublicMenu().getPreviousItem(), 45),
            new NavigationItem(ConfigLoader.getConfig().getPublicMenu().getNextItem(), 53)
    );

    @Variable("options-menu")
    @Comment("Options menu configuration settings")
    public PageableMenu optionsMenu = new PageableMenu(
            "&8Claim options",
            54,
            Map.of(
                    "SPAWN_ANIMAL", new MenuItem(0, "&eSpawn Animal", "SHEEP_SPAWN_EGG", 0, List.of("&7status: &f%status%", "&7", "&7Click to spawn animals on your claim!")),
                    "SPAWN_MONSTER", new MenuItem(0, "&aSpawn Monster", "SKELETON_SPAWN_EGG", 0, List.of("&7status: &f%status%", "&7", "&7Click to spawn monsters on your claim!")),
                    "PVP", new MenuItem(0, "&aEnable PVP", "DIAMOND_SWORD", 0, List.of("&7status: &f%status%", "&7", "&7Click to enable PVP on your claim!")),
                    "EXPLOSION", new MenuItem(0, "&aEnable Explosion", "TNT", 0, List.of("&7status: &f%status%", "&7", "&7Click to enable explosions on your claim!")),
                    "SPREAD", new MenuItem(0, "&aEnable Spread", "FIRE_CHARGE", 0, List.of("&7status: &f%status%", "&7", "&7Click to enable spread on your claim!")),
                    "WEATHER", new MenuItem(0, "&aChange Weather Status", "WATER_BUCKET", 0, List.of("&7status: &f%status%", "&7", "&7Click to change weather status!")),
                    "TIME", new MenuItem(0, "&aChange Time Status", "CLOCK", 0, List.of("&7status: &f%status%", "&7", "&7Click to change time status!"))
            ),
            false,
            new ArrayList<>(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17,18, 26,35,27, 36,44, 46, 47, 48, 49, 50, 51, 52,45,53)),
            new NavigationItem(ConfigLoader.getConfig().getPublicMenu().getPreviousItem(), 45),
            new NavigationItem(ConfigLoader.getConfig().getPublicMenu().getNextItem(), 53)
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
    public PageableMenu permissionsMenu = new PageableMenu(
            "Permissions Menu",
            27,
            createPermissionsMenuItems(),
            false,
            new ArrayList<>(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17,18, 26,35,27, 36,44, 46, 47, 48, 49, 50, 51, 52,45,53)),
            new NavigationItem(ConfigLoader.getConfig().getPublicMenu().getPreviousItem(), 45),
            new NavigationItem(ConfigLoader.getConfig().getPublicMenu().getNextItem(), 53)
    );

    private static Map<String, MenuItem> createPermissionsMenuItems() {
        HashMap<String, MenuItem> items = new HashMap<>();
        items.put("BLOCK_BREAK", new MenuItem(0, "&aBlock Break", "STONE_PICKAXE", 0, List.of("&7status: &f%status%", "&7", "&7Toggle block breaking permission")));
        items.put("BLOCK_PLACE", new MenuItem(0, "&aBlock Place", "STONE", 0, List.of("&7status: &f%status%", "&7", "&7Toggle block placing permission")));
        items.put("ITEM_PICKUP", new MenuItem(0, "&aPickup Item", "DIAMOND", 0, List.of("&7status: &f%status%", "&7", "&7Toggle item pickup permission")));
        items.put("DROP_ITEM", new MenuItem(0, "&aDrop Item", "IRON_INGOT", 0, List.of("&7status: &f%status%", "&7", "&7Toggle item dropping permission")));
        items.put("CONTAINER_OPEN", new MenuItem(0, "&aOpen Container", "CHEST", 0, List.of("&7status: &f%status%", "&7", "&7Toggle container opening permission")));
        items.put("INTERACT_ENTITY", new MenuItem(0, "&aInteract Entity", "IRON_SWORD", 0, List.of("&7status: &f%status%", "&7", "&7Toggle entity interaction permission")));
        items.put("ATTACK_ANIMAL", new MenuItem(0, "&aAttack Animal", "BOW", 0, List.of("&7status: &f%status%", "&7", "&7Toggle attacking animals permission")));
        items.put("ATTACK_MONSTER", new MenuItem(0, "&aAttack Monster", "DIAMOND_SWORD", 0, List.of("&7status: &f%status%", "&7", "&7Toggle attacking monsters permission")));
        items.put("BREAk_CONTAINER", new MenuItem(0, "&aBreak Container", "BARREL", 0, List.of("&7status: &f%status%", "&7", "&7Toggle breaking containers permission")));
        items.put("USE_DOOR", new MenuItem(0, "&aUse Door", "LEVER", 0, List.of("&7status: &f%status%", "&7", "&7Toggle using doors permission")));
        items.put("USE_PORTAL", new MenuItem(0, "&aUse Portal", "OBSIDIAN", 0, List.of("&7status: &f%status%", "&7", "&7Toggle using portals permission")));
        items.put("USE_POTION", new MenuItem(0, "&aUse Potion", "POTION", 0, List.of("&7status: &f%status%", "&7", "&7Toggle using potions permission")));
        items.put("BUCKET_FILL", new MenuItem(0, "&aBucket Fill", "BUCKET", 0, List.of("&7status: &f%status%", "&7", "&7Toggle using bucket fill permission")));
        items.put("BUCKET_EMPTY", new MenuItem(0, "&aBucket Empty", "LAVA_BUCKET", 0, List.of("&7status: &f%status%", "&7", "&7Toggle using bucket empty permission")));
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
            ),
            true,
            new ArrayList<>()
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
            ),
            true,
            new ArrayList<>()
    );

    @Variable("block-menu")
    @Comment("Block menu configuration settings")
    public PageableMenu blockMenu = new PageableMenu(
            "&8Setting claim block",
            27,
            Map.of(),
            false,
            new ArrayList<>(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 19, 20, 21, 22, 23, 24, 25, 17)),
            new NavigationItem(ConfigLoader.getConfig().getPublicMenu().getPreviousItem(), 18),
            new NavigationItem(ConfigLoader.getConfig().getPublicMenu().getNextItem(), 26)
    );

    @Variable("tag-main-menu")
    @Comment("Tag main menu configuration settings")
    public PageableMenu tagMainMenu = new PageableMenu(
            "&8Tag Menu",
            27,
            Map.of(
                    "add-tag", new MenuItem(22, "&eAdd Tag", "OAK_SIGN", 0, List.of("&7", "&eClick to add a new tag.", "&7")),
                    "item-settings", new MenuItem(null, "&a<name>", "OAK_SIGN", 0, List.of("&7", "&eClick to change tag permission or users.", "&7Shift-Click and disable tag."))
            ),
            false,
            new ArrayList<>(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 19, 20, 21, 22, 23, 24, 25, 17)),
            new NavigationItem(ConfigLoader.getConfig().getPublicMenu().getPreviousItem(), 18),
            new NavigationItem(ConfigLoader.getConfig().getPublicMenu().getNextItem(), 26)
    );


    @Variable("tag-edit-menu")
    @Comment("Tag editor menu configuration settings")
    public Menu tagEditMenu = new Menu(
            "&8Tag Editor",
            27,
            Map.of(
                    "users", new MenuItem(12, "&eUsers", "PLAYER_HEAD", 0, List.of("&7", "&eAdd/remove users from this tag.", "&7")),
                    "permissions", new MenuItem(14, "&ePermissions", "BOOK", 0, List.of("&7", "&eAdd/remove permission from this tag.", "&7"))
            ),
            true,
            new ArrayList<>()
    );


    @Variable("tag-users-menu")
    @Comment("Tag users menu configuration settings")
    public PageableMenu tagUsersMenu = new PageableMenu(
            "&8Tag Users",
            27,
            Map.of(
                    "add-user", new MenuItem(22, "&eAdd User", "NAME_TAG", 0, List.of("&7", "&aClick to add a player to this tag.")),
                    "item-settings", new MenuItem(null, "&e<name>", "PLAYER_HEAD", 0, List.of("&7", "&eClick and remove player from this tag!"))
            ),
            false,
            new ArrayList<>(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 19, 20, 21, 22, 23, 24, 25, 17)),
            new NavigationItem(ConfigLoader.getConfig().getPublicMenu().getPreviousItem(), 18),
            new NavigationItem(ConfigLoader.getConfig().getPublicMenu().getNextItem(), 26)
    );


    @Variable("tag-permissions-menu")
    @Comment("Tag permissions menu configuration settings")
    public PageableMenu tagPermissionsMenu = new PageableMenu(
            "&8Tag Permissions",
            54,
            Map.of(),
            false,
            new ArrayList<>(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 18, 27, 36,44, 46, 47, 48, 50, 51, 52,45,53)),
            new NavigationItem(ConfigLoader.getConfig().getPublicMenu().getPreviousItem(), 45),
            new NavigationItem(ConfigLoader.getConfig().getPublicMenu().getNextItem(), 53)
    );

    @Getter@Setter
    public static class PageableMenu extends OkaeriConfig{

        private String title;
        private int size;
        private Map<String, MenuItem> items;
        private boolean autoFill;
        private List<Integer> fillerSlots;
        private NavigationItem previousItem;
        private NavigationItem nextItem;


        public PageableMenu(String title, int size, Map<String, MenuItem> items, boolean autoFill, List<Integer> fillerSlots, NavigationItem previousItem, NavigationItem nextItem) {
            this.title = title;
            this.size = size;
            this.items = items;
            this.autoFill = autoFill;
            this.fillerSlots = fillerSlots;
            this.previousItem = previousItem;
            this.nextItem = nextItem;
        }
    }
    @Getter@Setter
    public static class MenuItem extends OkaeriConfig {

        private String title;
        private String material;
        private int customModelData;
        private List<String> lore;
        private Integer index;

        public MenuItem(Integer index, String title, String material, int customModelData, List<String> lore) {
            this.title = title;
            this.material = material;
            this.customModelData = customModelData;
            this.lore = lore;
            this.index = index;
        }

        public MenuItem(String title, String material, int customModelData, List<String> lore){
            this(null, title, material, customModelData, lore);
        }


        public boolean hasIndex(){
            return index != null;
        }
    }
    public static class NavigationItem extends OkaeriConfig {

        private MenuItem menuItem;
        private int index;

        public NavigationItem(MenuItem menuItem, int slot) {
            this.menuItem = menuItem;
            this.index = slot;
        }

        public ClickableItemStack asClickableItemStack(@Nullable Player player, TagResolver... tags) {
            return ItemBuilder.of(Material.getMaterial(menuItem.getMaterial()))
                    .name(menuItem.getTitle(),player, tags)
                    .lore(menuItem.getLore(),player, tags)
                    .amount(1)
                    .hideFlags()
                    .customModelData(menuItem.getCustomModelData())
                    .asClickableItemStack(index);
        }
    }
    @Getter@Setter
    public static class Menu extends OkaeriConfig {

        private String title;
        private int size;
        private Map<String, MenuItem> items;
        private boolean autoFill;
        private List<Integer> fillerSlots;


        public Menu(String title, int size) {
            this.title = title;
            this.size = size;
        }

        public Menu(String title, int size, Map<String, MenuItem> items, boolean autoFill, List<Integer> fillerSlots) {
            this.title = title;
            this.size = size;
            this.items = items;
            this.autoFill = autoFill;
            this.fillerSlots = fillerSlots;
        }

        public Menu(String title, int size, Map<String, MenuItem> items) {
            this.title = title;
            this.size = size;
            this.items = items;
        }

        public Menu addItem(String title, MenuItem item) {
            items.put(title, item);
            return this;
        }

        public List<Integer> getFillerSlots() {
            if (fillerSlots == null) {
                return new ArrayList<>();
            }
            return fillerSlots;
        }
    }


}