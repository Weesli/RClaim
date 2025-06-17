package net.weesli.rclaim.config.adapter;

import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.*;
import lombok.NonNull;
import net.weesli.rclaim.config.adapter.model.Menu;
import net.weesli.rclaim.config.adapter.model.MenuItem;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ClaimAdapter implements OkaeriSerdesPack {

    @Override
    public void register(@NonNull SerdesRegistry registry) {
        registry.register(new MenuItemRegistry());
        registry.register(new MenuRegistry());
    }

    static class MenuItemRegistry implements ObjectSerializer<MenuItem> {

        @Override
        public boolean supports(@NonNull Class<? super MenuItem> type) {
            return MenuItem.class.isAssignableFrom(type);
        }

        @Override
        public void serialize(@NonNull MenuItem object, @NonNull SerializationData data, @NonNull GenericsDeclaration generics) {
            data.add("title", object.getTitle());
            data.add("material", object.getMaterial());
            if (object.hasIndex()){
                data.add("index", object.getIndex(), Integer.class);
            }
            data.add("custom-model-data", object.getCustomModelData(), Integer.class);
            data.add("lore", object.getLore(), List.class);
        }

        @Override
        public MenuItem deserialize(@NonNull DeserializationData data, @NonNull GenericsDeclaration generics) {
            String title = data.get("title", String.class);
            String material = data.get("material", String.class);
            Integer customModelData = data.get("custom-model-data", Integer.class);
            List<String> lore = data.getAsList("lore", String.class);
            if (data.containsKey("index")){
                Integer index = data.get("index", Integer.class);
                return new MenuItem(index,title,material,customModelData,lore);
            }
            return new MenuItem(title, material, customModelData, lore); // if the item hansn't a index in menu then just return item
        }
    }

    static class MenuRegistry implements ObjectSerializer<Menu>{

        @Override
        public boolean supports(@NonNull Class<? super Menu> type) {
            return Menu.class.isAssignableFrom(type);
        }

        @Override
        public void serialize(@NonNull Menu object, @NonNull SerializationData data, @NonNull GenericsDeclaration generics) {
            data.add("title", object.getTitle());
            data.add("size", object.getSize(), Integer.class);
            data.add("items", object.getItems());
        }

        @Override
        public Menu deserialize(@NonNull DeserializationData data, @NonNull GenericsDeclaration generics) {
            return new Menu(
                    data.get("title", String.class),
                    data.get("size", Integer.class),
                    data.getAsMap("items", String.class, MenuItem.class)
            );
        }
    }
}
