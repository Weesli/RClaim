package net.weesli.rclaim.config.adapter.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter@Setter
public class MenuItem {

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
