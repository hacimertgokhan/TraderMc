package net.mixium.tradermc.storage.yaml;

import net.mixium.tradermc.storage.yaml.interfaces.StockAdapter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

import static net.mixium.tradermc.TraderMc.get;
import static net.mixium.tradermc.maps.Indices.indices;
import static net.mixium.tradermc.maps.Indices.prices;
import static net.mixium.tradermc.storage.mysql.Database.addIndices;
import static net.mixium.tradermc.storage.mysql.Database.removeIndices;
import static net.mixium.tradermc.storage.yaml.YamlDataStock.getStock;
import static net.mixium.tradermc.storage.yaml.YamlDataStock.saveStock;
import static net.mixium.tradermc.vault.VaultEconomy.getEconomy;

public class TraderMcPlayer implements StockAdapter {
    private static Player player;

    public TraderMcPlayer(Player player) {
        this.player=player;
    }

    @Override
    public boolean isUserLoaded() {
        return (getStock().getString("user-data." + player.getUniqueId().toString()) != null);
    }

    @Override
    public List<String> getUserIndicesList() {
        if(isUserLoaded()) {
            return (getStock().getStringList("user-data." + player.getUniqueId().toString() + ".indices-list"));
        }
        return null;
    }

    @Override
    public int getUserIndicesAmount(String inds) {
        if(isUserLoaded()) {
            if(getStock().getString("user-data." + player.getUniqueId().toString() + ".indices." + inds) != null) {
                return getStock().getInt("user-data." + player.getUniqueId().toString() + ".indices." + inds + ".amount");
            }
        }
        return 0;
    }

    @Override
    public int getUserIndicesValue(String inds) {
        return 0;
    }

    @Override
    public double getIndicesTotalValue() {
        List<String> indicesList = getStock().getStringList("user-data." + player.getUniqueId().toString() + ".indices-list");
        int indicesListLen = indicesList.size();
        double total = 0.0;
        if(isUserLoaded()) {
            for(int a = 0; a<indicesListLen; a++) {
                String str = getStock().getString("user-data." +player.getUniqueId().toString() + ".indices." + indicesList.get(a));
                if(str != null) {
                    if(prices.get(str) != null) {
                        total = (total+prices.get(str));
                        return total;
                    }
                }

            }
        }
        return 0;
    }

    @Override
    public void createNewUser() {
        List<String> indicesList = get().getConfig().getStringList("trader-mc.indices");
        if(!isUserLoaded()) {
            getStock().set("user-data." + player.getUniqueId().toString() + ".indices-list", "[]");
            getStock().set("user-data." + player.getUniqueId().toString() + ".total-value", 0);
            getStock().set("user-data." + player.getUniqueId().toString() + ".name", player.getName().toString());
            for(String inds : indicesList) {
                getStock().set("user-data." + player.getUniqueId().toString() + ".indices." + inds + ".amount", 0);
            }
            saveStock();
        }
    }

    @Override
    public void BuyIndices(String inds, int amount) {
        if(indices.get(inds) != null) {
            if (prices.get(inds) != null) {
                if(getStock().getString("user-data." + player.getUniqueId().toString() + ".indices." + inds) != null) {
                    double calculate = prices.get(inds)*amount;
                    double balance = getEconomy().getBalance(player);
                    int indsAmount = getStock().getInt("user-data." + player.getUniqueId().toString() + ".indices." + inds + ".amount");
                    List<String> indsList = getStock().getStringList("user-data." + player.getUniqueId().toString() + ".indices-list");
                    if(balance >= calculate) {
                        getEconomy().withdrawPlayer(player, calculate);
                        indsList.add(inds);
                        getStock().set("user-data." + player.getUniqueId().toString() + ".indices." + inds + ".amount", indsAmount+amount);
                        getStock().set("user-data." + player.getUniqueId().toString() + ".indices-list", indsList);
                        saveStock();
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', get().getConfig().getString("trader-mc.prefix") + " &r" + get().getConfig().getString("trader-mc.messages.buy.success").replace("%total%", String.valueOf(calculate)).replace("%amount%", String.valueOf(amount)).replace("%indices%", inds)));
                    } else {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', get().getConfig().getString("trader-mc.prefix") + " &r" + get().getConfig().getString("trader-mc.messages.buy.not-enough-money").replace("%total%", String.valueOf(calculate)).replace("%indices%", inds)));;
                    }
                }
            }
        }
    }

    @Override
    public void SellIndices(String inds, int amount) {
        if(indices.get(inds) != null) {
            if (prices.get(inds) != null) {
                if (getStock().getString("user-data." + player.getUniqueId().toString() + ".indices." + inds) != null) {
                    int indsAmount = getStock().getInt("user-data." + player.getUniqueId().toString() + ".indices." + inds + ".amount");
                    double calculate = prices.get(inds)*indsAmount;
                    List<String> indsList = getStock().getStringList("user-data." + player.getUniqueId().toString() + ".indices-list");
                    if (indsAmount >= amount) {
                        indsList.remove(inds);
                        getEconomy().depositPlayer(player, calculate);
                        getStock().set("user-data." + player.getUniqueId().toString() + ".indices." + inds + ".amount", indsAmount-amount);
                        getStock().set("user-data." + player.getUniqueId().toString() + ".indices-list", indsList);
                        saveStock();
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', get().getConfig().getString("trader-mc.prefix") + " &r" + get().getConfig().getString("trader-mc.messages.sell.success").replace("%total%", String.valueOf(calculate)).replace("%amount%", String.valueOf(amount)).replace("%indices%", inds)));;
                    } else {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', get().getConfig().getString("trader-mc.prefix") + " &r" + get().getConfig().getString("trader-mc.messages.sell.not-enough-indices").replace("%total%", String.valueOf(calculate)).replace("%indices%", inds)));;
                    }
                }
            }
        }
    }
}
