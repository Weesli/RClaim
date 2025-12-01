package net.weesli.rclaim;

import net.weesli.rclaim.api.module.Module;
import net.weesli.rozslib.color.ColorBuilder;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;

public final class ModuleLoader {
    public static void loadAddons(String addonsFolderPath) {
        File addonsFolder = new File(addonsFolderPath);
        if (!addonsFolder.exists()) {
            boolean created = addonsFolder.mkdirs();
            if (!created) {
                Bukkit.getLogger().log(Level.SEVERE, "Failed to create addons folder");
                return;
            }
        }

        File[] files = addonsFolder.listFiles((dir, name) -> name.endsWith(".jar"));
        if (files == null || files.length == 0) {
            Bukkit.getLogger().info("[RClaim] No module was activated");
            return;
        }
        Bukkit.getConsoleSender().sendMessage(ColorBuilder.convertColors("&aLoading modules...."));
        for (File file : files) {
            try {
                loadAddon(file);
            } catch (Exception e) {
                Bukkit.getLogger().log(Level.SEVERE, "Failed to load module " + file.getName(), e);
            }
        }
    }

    private static void loadAddon(File file) throws IOException, ReflectiveOperationException {
        try (JarFile jarFile = new JarFile(file)) {
            URL[] urls = { file.toURI().toURL() };
            try (URLClassLoader classLoader = new URLClassLoader(urls, RClaim.class.getClassLoader())) {
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    if (entry.getName().endsWith(".class")) {
                        String className = entry.getName().replace('/', '.').replace(".class", "");
                        Class<?> cls = classLoader.loadClass(className);
                        if (Module.class.isAssignableFrom(cls)) {
                            Module addon = (Module) cls.getDeclaredConstructor().newInstance();
                            Method onEnableMethod = cls.getMethod("enable");
                            onEnableMethod.invoke(addon);
                            Bukkit.getConsoleSender().sendMessage(ColorBuilder.convertColors("&bLoaded &a" + addon.getAddonName() + " &bVersion &a" + addon.getVersion()));
                        }
                    }
                }
            }
        }
    }
}
