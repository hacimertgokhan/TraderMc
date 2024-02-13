package net.mixium.tradermc;

import net.mixium.tradermc.commands.Administrator;
import net.mixium.tradermc.commands.User;
import net.mixium.tradermc.config.Config;
import net.mixium.tradermc.events.Join;
import net.mixium.tradermc.storage.mysql.Database;
import net.mixium.tradermc.storage.yaml.YamlDataStock;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

import static net.mixium.tradermc.config.Config.getLanguage;
import static net.mixium.tradermc.maps.Indices.*;
import static net.mixium.tradermc.storage.Storage.getType;
import static net.mixium.tradermc.storage.yaml.YamlDataStock.saveStock;
import static net.mixium.tradermc.vault.VaultEconomy.setupEconomy;

public final class TraderMc extends JavaPlugin {
    private static TraderMc instance;
    public static synchronized TraderMc get() { return instance;}
    public static synchronized void set(TraderMc traderMc) {instance = traderMc;}

    // non
    YamlDataStock yamlDataStock = new YamlDataStock(this, "storage");

    // Statics
    static Config config = new Config();
    static Database database = new Database();
    static int refresh = 0;

    // Loaders
    public void LoadEssentials() {
        getCommand("TraderMc").setExecutor(new Administrator(this));
        getCommand("TMC").setExecutor(new User(this));
        Bukkit.getPluginManager().registerEvents(new Join(this), this);
    }


    @Override
    public void onEnable() {
        set(this);
        config.makeConfig();
        refresh = get().getConfig().getInt("trader-mc.update");
        loadIndices();
        LoadEssentials();
        if(getType().equalsIgnoreCase("mysql")) {
            try {
                database.initializeDatabase();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else if (getType().equalsIgnoreCase("yaml")) {
            yamlDataStock.create();
        }
        if (!setupEconomy() ) {
            if(getConfig().getBoolean("trader-mc.warnings.api")) {
                if(getLanguage().equalsIgnoreCase("tr")) {
                    getLogger().severe(String.format("[%s] - Eklenti kapatıldı. Vault gereksinimleri bulunamadı ! (Ekonomi, Sohbet, Yetki vb.)", getDescription().getName()));
                } else if(getLanguage().equalsIgnoreCase("en")) {
                    getLogger().severe(String.format("[%s] - Disabled, Vault dependency not found ! (Economy, Chat, Permission etc.)", getDescription().getName()));
                }
            }
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        if(getConfig().getBoolean("trader-mc.warnings.api")) {
            if(getLanguage().equalsIgnoreCase("tr")) {
                Bukkit.getLogger().warning("\nEndeks verileri sürekli olarak iletişim halinde olunan apiler aracılığı ile sağlanır.\nBu apide sağlanan bağlantı kaybı eklentinin sağlıklı çalışmamasına yol açar.\nBu hata TraderMc kaynaklı değildir, apinin bağlantısı farklı nedenlerden dolayı kopmuş olabilir.\nDestek almak için geliştirici ile iletişime geçebilirsiniz.");
            } else if(getLanguage().equalsIgnoreCase("en")) {
                Bukkit.getLogger().warning("\nIndex data is provided by constantly communicating APIs.\n" +
                        "The connection distribution of this API leads to the healthy functioning of the plugin.\n" +
                        "This error is not caused by TraderMc, the API connection may be broken in different ways.\n" +
                        "You can contact the developer to get support.");
            }
        }
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            updateIndices();
            if(getConfig().getBoolean("trader-mc.broadcast")) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    for (String broadcast : getConfig().getStringList("trader-mc.messages.broadcast")) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', broadcast.replace("%indices%", indices.toString().replace("{", "").replace("}", "").replace("[", "").replace("]", ""))));
                    }
                }
            }
        }, refresh*20, refresh*40);
    }

    @Override
    public void onDisable() {
        if (getType().equalsIgnoreCase("yaml")) {
            saveStock();
            if(getConfig().getBoolean("trader-mc.warnings.disable")) {
                if(getLanguage().equalsIgnoreCase("tr")) {
                    Bukkit.getLogger().warning("[TraderMc] Eklenti kapatıldı, storage.yml dosyasındaki bütün veriler son kez okundu ve kayıt edildi.");
                } else if(getLanguage().equalsIgnoreCase("en")) {
                    Bukkit.getLogger().warning("[TraderMc] Plugin disabled, all datas in storage.yml saved last time.");
                }
            }
        }
    }

}
