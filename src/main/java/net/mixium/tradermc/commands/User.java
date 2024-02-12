package net.mixium.tradermc.commands;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.mixium.tradermc.TraderMc;
import net.mixium.tradermc.storage.yaml.TraderMcPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static net.mixium.tradermc.TraderMc.get;
import static net.mixium.tradermc.maps.Indices.indices;
import static net.mixium.tradermc.maps.Indices.prices;
import static net.mixium.tradermc.storage.Storage.getType;
import static net.mixium.tradermc.storage.Storage.isInteger;
import static net.mixium.tradermc.storage.mysql.Database.*;
import static net.mixium.tradermc.storage.yaml.YamlDataStock.getStock;
import static net.mixium.tradermc.vault.VaultEconomy.getEconomy;

public class User implements CommandExecutor {
    public User(TraderMc traderMc) {}

    public void Help(CommandSender commandSender) {
        for(String a : get().getConfig().getStringList("trader-mc.messages.help.user")) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', a.replace("%version%", get().getDescription().getVersion()).replace("%devs%", get().getDescription().getAuthors().toString().replace("[", "").replace("]", "").replace("_", " "))));
        }
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player) {
            Player player = ((Player) commandSender).getPlayer();
            TraderMcPlayer traderMcPlayer = new TraderMcPlayer(player);
            if(player.hasPermission("tradermc.user")) {
                if(strings.length == 0 || strings[0].equalsIgnoreCase("help")) {
                    Help(commandSender);
                } else if (strings.length == 1) {
                    if (strings[0].equalsIgnoreCase("indices")) {
                        if(getType().equalsIgnoreCase("mysql")) {

                            JsonParser jsonParser = new JsonParser();
                            List<String> indicesList = get().getConfig().getStringList("trader-mc.indices");
                            List<String> ownedIndices = new ArrayList<>();
                            JsonObject parsed = jsonParser.parse(getIndices(player.getUniqueId())).getAsJsonObject();
                            for (String inds : indicesList) {
                                if (parsed.get(inds) != null) {
                                    String editedInds = String.format(get().getConfig().getString("trader-mc.messages.user.indices-list.format"), inds, parsed.get(inds));
                                    ownedIndices.add(editedInds);
                                }
                            }
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', get().getConfig().getString("trader-mc.messages.user.indices-list.title")));
                            for (int a = 0; a < ownedIndices.size(); a++) {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', get().getConfig().getString("trader-mc.messages.user.indices-list.elements").replace("%indices%", ownedIndices.get(a))));
                            }
                        } else if(getType().equalsIgnoreCase("yaml")) {
                            List<String> indicesList = getStock().getStringList("user-data." + player.getUniqueId().toString() + ".indices-list");
                            List<String> ownedIndices = new ArrayList<>();
                            for (String inds : indicesList) {
                                String userinds = getStock().getString("user-data." + player.getUniqueId().toString() + ".indices." + inds + ".amount");
                                int indsAmount = getStock().getInt("user-data." + player.getUniqueId().toString() + ".indices." + inds + ".amount");
                                if (userinds != null) {
                                    String editedInds = String.format(get().getConfig().getString("trader-mc.messages.user.indices-list.format"), inds, indsAmount);
                                    ownedIndices.add(editedInds);
                                }
                            }
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', get().getConfig().getString("trader-mc.messages.user.indices-list.title-yaml").replace("%total_value%", String.valueOf(traderMcPlayer.getIndicesTotalValue()))));
                            for (int a = 0; a < ownedIndices.size(); a++) {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', get().getConfig().getString("trader-mc.messages.user.indices-list.elements").replace("%indices%", ownedIndices.get(a))));
                            }
                        }
                    } else {
                        Help(commandSender);
                    }
                } else if (strings.length == 3) {
                    if(strings[0].equalsIgnoreCase("buy")) {
                        int amount = Integer.parseInt(strings[1]);
                        String inds = strings[2];
                        if(isInteger(strings[1])) {
                            if (amount > 0) {
                                if (indices.get(inds) != null) {
                                    if (getType().equalsIgnoreCase("mysql")) {
                                        double calculate = prices.get(inds) * amount;
                                        double balance = getEconomy().getBalance(player);
                                        if (balance >= calculate) {
                                            getEconomy().withdrawPlayer(player, calculate);
                                            addIndices(player.getUniqueId(), inds, amount);
                                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', get().getConfig().getString("trader-mc.prefix") + " &r" + get().getConfig().getString("trader-mc.messages.buy.success").replace("%total%", String.valueOf(calculate)).replace("%amount%", strings[1]).replace("%indices%", strings[2])));
                                            ;
                                        } else {
                                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', get().getConfig().getString("trader-mc.prefix") + " &r" + get().getConfig().getString("trader-mc.messages.buy.not-enough-money").replace("%total%", String.valueOf(calculate)).replace("%indices%", strings[2])));
                                            ;
                                        }
                                    } else if (getType().equalsIgnoreCase("yaml")) {
                                        traderMcPlayer.BuyIndices(inds, amount);
                                    }
                                } else {
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', get().getConfig().getString("trader-mc.prefix") + " &r" + get().getConfig().getString("trader-mc.messages.buy.indices-not-found").replace("%indices%", strings[2])));;
                                }
                            } else {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', get().getConfig().getString("trader-mc.messages.user.buy.integer-error")));
                            }
                        } else {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', get().getConfig().getString("trader-mc.prefix") + " &r" +get().getConfig().getString("trader-mc.messages.must-be-number")));
                        }
                    } else if(strings[0].equalsIgnoreCase("sell")) {
                        int amount = Integer.parseInt(strings[1]);
                        String inds = strings[2];
                        if(isInteger(strings[1])) {
                            if (amount > 0) {
                                if (indices.get(inds) != null) {
                                    if (getType().equalsIgnoreCase("mysql")) {
                                        JsonParser jsonParser = new JsonParser();
                                        JsonObject parsed = jsonParser.parse(getIndices(player.getUniqueId())).getAsJsonObject();
                                        if (parsed.get(inds) != null) {
                                            int indsAmount = parsed.get(inds).getAsInt();
                                            double calculate = prices.get(inds) * indsAmount;
                                            if (indsAmount >= amount) {
                                                getEconomy().depositPlayer(player, calculate);
                                                removeIndices(player.getUniqueId(), inds, amount);
                                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', get().getConfig().getString("trader-mc.prefix") + " &r" + get().getConfig().getString("trader-mc.messages.sell.success").replace("%total%", String.valueOf(calculate)).replace("%amount%", strings[1]).replace("%indices%", strings[2])));
                                                ;
                                            } else {
                                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', get().getConfig().getString("trader-mc.prefix") + " &r" + get().getConfig().getString("trader-mc.messages.sell.not-enough-indices").replace("%total%", String.valueOf(calculate)).replace("%indices%", strings[2])));
                                                ;
                                            }
                                        }
                                    } else if (getType().equalsIgnoreCase("yaml")) {
                                        traderMcPlayer.SellIndices(inds, amount);
                                    }
                                } else {
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', get().getConfig().getString("trader-mc.prefix") + " &r" + get().getConfig().getString("trader-mc.messages.sell.indices-not-found").replace("%indices%", strings[2])));
                                    ;
                                }
                            } else {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', get().getConfig().getString("trader-mc.prefix") + " &r" +get().getConfig().getString("trader-mc.messages.user.sell.integer-error")));
                            }
                        } else {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', get().getConfig().getString("trader-mc.prefix") + " &r" +get().getConfig().getString("trader-mc.messages.must-be-number")));
                        }
                    } else {
                        Help(commandSender);
                    }
                } else {
                    Help(commandSender);
                }
            } else {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', get().getConfig().getString("trader-mc.prefix") + " &r" + get().getConfig().getString("trader-mc.messages.no-permission")));;
            }
        } else {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', get().getConfig().getString("trader-mc.prefix") + " &r" + get().getConfig().getString("trader-mc.messages.no-console")));;
        }
        return false;
    }
}
