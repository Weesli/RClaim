package net.weesli.rclaim.config.adapter.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter@Setter
public class Menu {

    private String title;
    private int size;
    private Map<String, MenuItem> items;

    public Menu(String title, int size, Map<String, MenuItem> items) {
        this.title = title;
        this.size = size;
        this.items = items;
    }

    public Menu addItem(String title, MenuItem item) {
        items.put(title, item);
        return this;
    }
}
