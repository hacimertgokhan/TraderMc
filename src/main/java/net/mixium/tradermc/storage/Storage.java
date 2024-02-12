package net.mixium.tradermc.storage;

import static net.mixium.tradermc.TraderMc.get;

public class Storage {

    public static String getType() {
        return get().getConfig().getString("trader-mc.storage");
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
