package net.mixium.tradermc.maps;

import net.mixium.tradermc.bist.BISTIndices;
import net.mixium.tradermc.bist.BISTIndicesSymbols;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.List;

import static net.mixium.tradermc.TraderMc.get;
import static net.mixium.tradermc.config.Config.getLanguage;

public class Indices {
    public static HashMap<String, String> indices = new HashMap<>();
    public static HashMap<String, Double> prices = new HashMap<>();
    public static int skipped = 0;
    public static int added = 0;
    public static int updated = 0;
    public static void loadIndices() {
        int listLen = get().getConfig().getStringList("trader-mc.indices").size();
        List<String> indicesList = get().getConfig().getStringList("trader-mc.indices");
        int totalProcess = listLen;
        for(int i=0;i<listLen;i++) {
            for(String ind : indicesList) {
                if(indices.get(ind) == null) {
                    added++;
                    BISTIndicesSymbols BISTIndicesSymbols = new BISTIndicesSymbols(indicesList.get(i));
                    BISTIndices BISTIndices = new BISTIndices(BISTIndicesSymbols);
                    indices.put(indicesList.get(i), (BISTIndices.getCurrentlyPrice()));
                    if(indices.containsKey(ind)) {
                        prices.put(indicesList.get(i), BISTIndices.getCurrentlyPriceAsDouble());
                    }
                } else {
                    skipped++;
                }
            }
        }
        int getProcessAsPercentage = ((totalProcess*100)/(added+skipped));
        if(getLanguage().equalsIgnoreCase("tr")) {
            Bukkit.getLogger().info(String.format("[TraderMc] %s endeks yüklendi, geriye kalan %s endeks işlenemedi.", added, skipped));
            Bukkit.getLogger().info(String.format("[TraderMc] Endeks yükleme başarı oranı (%s) olarak hesaplandı.", getProcessAsPercentage));
            Bukkit.getLogger().info(String.format("[TraderMc] Endekslerin matematiksel listesi: (%s)", prices.toString().replace("{", "").replace("}", "")));
            Bukkit.getLogger().info(String.format("[TraderMc] Yüklenen Endeksler: (%s)", indices.toString().replace("{", "").replace("}", "")));
            if(skipped>0) {
                Bukkit.getLogger().warning("[TraderMc:BETA] Bazı endeksler birden kez yazılmış olabilir, lütfen config.yml dosyasında 'trader-mc.indices' bilgilerini kontrol ediniz.");
            }
        } else if(getLanguage().equalsIgnoreCase("en")) {
            Bukkit.getLogger().info(String.format("[TraderMc] Added %s indices and skipped %s indices.", added, skipped));
            Bukkit.getLogger().info(String.format("[TraderMc] Indices: (%s)", indices.toString().replace("{", "").replace("}", "")));
            if(skipped>0) {
                Bukkit.getLogger().warning("[TraderMc:BETA] Some indices may have been written more than once.");
            }
        }

        skipped=0;
        added=0;
    }

    public static void updateIndices() {
        skipped=0;
        updated=0;
        int listLen = get().getConfig().getStringList("trader-mc.indices").size();
        List<String> indicesList = get().getConfig().getStringList("trader-mc.indices");
        for(int i=0;i<listLen;i++) {
            for (String ind : indicesList) {
                if(indices.get(ind) != null) {
                    BISTIndicesSymbols BISTIndicesSymbols = new BISTIndicesSymbols(indicesList.get(i));
                    BISTIndices BISTIndices = new BISTIndices(BISTIndicesSymbols);
                    indices.put(indicesList.get(i), (BISTIndices.getCurrentlyPrice()));
                    updated++;
                } else {
                    skipped++;
                }
            }
        }
        if(get().getConfig().getBoolean("trader-mc.warnings.update")) {
            if(getLanguage().equalsIgnoreCase("tr")) {
                Bukkit.getLogger().info(String.format("[TraderMc] %s endeks güncellendi, geriye kalan %s endeks işlenemedi.", (updated), (skipped)));
                Bukkit.getLogger().info(String.format("[TraderMc] Endeksler: (%s)", indices.toString().replace("{", "").replace("}", "")));
                if(skipped>0) {
                    Bukkit.getLogger().warning("[TraderMc:BETA] Bazı endeksler birden kez yazılmış olabilir, lütfen config.yml dosyasında 'trader-mc.indices' bilgilerini kontrol ediniz.");
                }
            } else if(getLanguage().equalsIgnoreCase("en")) {
                Bukkit.getLogger().info(String.format("[TraderMc] Updated %s indices and skipped %s indices.", (updated), (skipped)));
                Bukkit.getLogger().info(String.format("[TraderMc] Indices: (%s)", indices.toString().replace("{", "").replace("}", "")));
                if(skipped>0) {
                    Bukkit.getLogger().warning("[TraderMc] Some indices may have been written more than once. [BETA]");
                }
            }

        }
    }

}
