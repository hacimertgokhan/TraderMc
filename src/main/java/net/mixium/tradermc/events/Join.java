package net.mixium.tradermc.events;

import net.mixium.tradermc.TraderMc;
import net.mixium.tradermc.storage.yaml.TraderMcPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import static net.mixium.tradermc.TraderMc.get;
import static net.mixium.tradermc.maps.Indices.indices;
import static net.mixium.tradermc.storage.Storage.getType;
import static net.mixium.tradermc.storage.mysql.Database.*;
import static net.mixium.tradermc.storage.yaml.YamlDataStock.saveStock;

public class Join implements Listener {
    public Join(TraderMc traderMc) {}

    @EventHandler
    public void PlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        TraderMcPlayer traderMcPlayer = new TraderMcPlayer(player);
        if(getType().equalsIgnoreCase("mysql")) {
            UpdateIndices(player);
            if(!playerExists(player.getUniqueId().toString())) {
                createDataWithoutCheck(player.getUniqueId().toString(), player.getName());
            }
        } else if (getType().equalsIgnoreCase("yaml")) {
            if(traderMcPlayer.isUserLoaded()) {
                if(get().getConfig().getBoolean("trader-mc.join-message")) {
                    for (String broadcast : get().getConfig().getStringList("trader-mc.messages.join-message")) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', broadcast.replace("%indices%", indices.toString().replace("{", "").replace("}", "").replace("[", "").replace("]", ""))));
                    }
                }
            } else {
                traderMcPlayer.createNewUser();
            }
        }
    }

    @EventHandler
    public void PlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        TraderMcPlayer traderMcPlayer = new TraderMcPlayer(player);
        if (getType().equalsIgnoreCase("yaml")) {
            if(traderMcPlayer.isUserLoaded()) {
                saveStock();
            }
        }
    }

}
