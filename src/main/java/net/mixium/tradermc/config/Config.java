package net.mixium.tradermc.config;

import static net.mixium.tradermc.TraderMc.get;

public class Config {
    public Config() {}
    public void makeConfig() {
        get().saveDefaultConfig();
    }

    public static String getLanguage() {
        return get().getConfig().getString("trader-mc.lang");
    }


}
