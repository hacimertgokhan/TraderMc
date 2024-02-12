package net.mixium.tradermc.storage.yaml;

import net.mixium.tradermc.TraderMc;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public class YamlDataStock {
    private static TraderMc plugin;
    private static String name;
    private static File file;
    private static FileConfiguration fileConfiguration;

    public YamlDataStock(TraderMc plugin, String name) {
        YamlDataStock.plugin = plugin;
        YamlDataStock.name = name;
        file = null;
        fileConfiguration = null;
    }

    public void create () {
        file = new File(plugin.getDataFolder(), (name+".yml"));
        if(!file.exists()) {
            getStock().options().copyDefaults(true);
            saveStock();
        }
    }

    public static FileConfiguration getStock() {
        if(fileConfiguration == null) {
            reload();
        }
        return fileConfiguration;
    }

    public static void reload() {
        if(fileConfiguration == null) {
            file = new File(plugin.getDataFolder(), (name+".yml"));
        }

        fileConfiguration = YamlConfiguration.loadConfiguration(file);

        try {
            Reader yds = new InputStreamReader(plugin.getResource(name+".yml"), StandardCharsets.UTF_8);
            if(yds != null) {
                YamlConfiguration stocks = YamlConfiguration.loadConfiguration(yds);
                fileConfiguration.setDefaults(stocks);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


    public static void saveStock() {
        try {
            fileConfiguration.save(file);
        } catch (IOException var1) {
            var1.printStackTrace();
        }

    }

}
