package net.weesli.rclaim.config.lang;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.weesli.rozsconfig.annotations.Comment;
import net.weesli.rozsconfig.annotations.ConfigKey;
import net.weesli.rozslib.inventory.ClickableItemStack;
import net.weesli.rozslib.inventory.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;


@Getter@Setter
public class MenuConfig {

    @Comment("Main menu configuration settings")
    public Menu mainMenu;

    @Comment("Claims menu configuration settings")
    public PageableMenu claimsMenu;

    @Comment("Upgrade menu configuration settings")
    public Menu upgradeMenu;

    @Comment("Members menu configuration settings")
    public PageableMenu membersMenu;

    @Comment("Options menu configuration settings")
    public PageableMenu optionsMenu;

    @Comment("Resize menu configuration settings")
    public Menu resizeMenu;

    @Comment("Permissions menu configuration settings")
    public PageableMenu permissionsMenu;

    @Comment("Verify menu configuration settings")
    public Menu verifyMenu;

    @Comment("Effect menu configuration settings")
    public Menu effectMenu;

    @Comment("Block menu configuration settings")
    public PageableMenu blockMenu;

    @Comment("Tag main menu configuration settings")
    public PageableMenu tagMainMenu;

    @Comment("Tag editor menu configuration settings")
    public Menu tagEditMenu;

    @Comment("Tag users menu configuration settings")
    public PageableMenu tagUsersMenu;

    @Comment("Tag permissions menu configuration settings")
    public PageableMenu tagPermissionsMenu;

    @Getter@Setter
    @NoArgsConstructor
    public static class PageableMenu{
        private String title;
        private int size;
        private Map<String, MenuItem> items;
        private boolean autoFill;
        private List<Integer> fillerSlots;
        private NavigationItem previousItem;
        private NavigationItem nextItem;
    }
    @Getter@Setter
    @NoArgsConstructor
    public static class MenuItem {

        private String title;
        private String material;
        private int customModelData;
        private List<String> lore;
        private Integer index;
        public boolean hasIndex(){
            return index != null;
        }
    }
    @NoArgsConstructor
    public static class NavigationItem {
        private MenuItem menuItem;
        private int index;
        public ClickableItemStack asClickableItemStack(@Nullable Player player, TagResolver... tags) {
            Material material = Material.getMaterial(menuItem.getMaterial());
            return ItemBuilder.of(material != null ? material : Material.BEDROCK)
                    .name(menuItem.getTitle(),player, tags)
                    .lore(menuItem.getLore(),player, tags)
                    .amount(1)
                    .hideFlags()
                    .customModelData(menuItem.getCustomModelData())
                    .asClickableItemStack(index);
        }
    }
    @Getter@Setter
    @NoArgsConstructor
    public static class Menu  {

        private String title;
        private int size;
        private Map<String, MenuItem> items;
        private boolean autoFill;
        private List<Integer> fillerSlots;

        public List<Integer> getFillerSlots() {
            if (fillerSlots == null) {
                return List.of();
            }
            return fillerSlots;
        }
    }


}