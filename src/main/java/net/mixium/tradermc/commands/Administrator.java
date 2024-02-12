package net.mixium.tradermc.commands;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.mixium.tradermc.TraderMc;
import net.mixium.tradermc.bist.BISTIndices;
import net.mixium.tradermc.bist.BISTIndicesSymbols;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static net.mixium.tradermc.TraderMc.get;
import static net.mixium.tradermc.maps.Indices.*;
import static net.mixium.tradermc.storage.mysql.Database.*;

public class Administrator implements CommandExecutor {
    public Administrator(TraderMc traderMc) {}
    public void Help(CommandSender commandSender) {
        for(String a : get().getConfig().getStringList("trader-mc.messages.help.admin")) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', a.replace("%version%", get().getDescription().getVersion()).replace("%devs%", get().getDescription().getAuthors().toString().replace("[", "").replace("]", "").replace("_", " "))));
        }
    }
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender.hasPermission("tradermc.admin")) {
            if(strings.length == 4) {
                if (strings[0].equalsIgnoreCase("-a")) {
                    Player plyr = Bukkit.getPlayer(strings[1]);
                    String indicesName = strings[2];
                    int amount = Integer.parseInt(strings[3]);
                    if (plyr != null) {
                        if(indices.get(indicesName) != null) {
                            if(amount > 0) {
                                addIndices(plyr.getUniqueId(), indicesName, amount);
                                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', get().getConfig().getString("trader-mc.prefix") + " &r" + get().getConfig().getString("trader-mc.messages.add.added").replace("%amount%", strings[3]).replace("%indices%", strings[2]).replace("%player%", strings[1])));;
                            } else {
                                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', get().getConfig().getString("trader-mc.prefix") + " &r" + get().getConfig().getString("trader-mc.messages.add.integer-error").replace("%amount%", strings[3])));;
                            }
                        } else {
                            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', get().getConfig().getString("trader-mc.prefix") + " &r" + get().getConfig().getString("trader-mc.messages.add.indices-not-found").replace("%indices%", strings[2])));;
                        }
                    } else {
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', get().getConfig().getString("trader-mc.prefix") + " &r" + get().getConfig().getString("trader-mc.messages.not-online").replace("%player%", strings[1])));;
                    }
                } else if (strings[0].equalsIgnoreCase("-r")) {
                    Player plyr = Bukkit.getPlayer(strings[1]);
                    String indicesName = strings[2];
                    int amount = Integer.parseInt(strings[3]);
                    if (plyr != null) {
                        if(indices.get(indicesName) != null) {
                            if(amount > 0) {
                                removeIndices(plyr.getUniqueId(), indicesName, amount);
                                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', get().getConfig().getString("trader-mc.prefix") + " &r" + get().getConfig().getString("trader-mc.messages.remove.removed").replace("%amount%", strings[3]).replace("%indices%", strings[2]).replace("%player%", strings[1])));;
                            } else {
                                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', get().getConfig().getString("trader-mc.prefix") + " &r" + get().getConfig().getString("trader-mc.messages.remove.integer-error").replace("%amount%", strings[3])));;
                            }
                        } else {
                            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', get().getConfig().getString("trader-mc.prefix") + " &r" + get().getConfig().getString("trader-mc.messages.remove.indices-not-found").replace("%indices%", strings[2])));;
                        }
                    } else {
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', get().getConfig().getString("trader-mc.prefix") + " &r" + get().getConfig().getString("trader-mc.messages.not-online").replace("%player%", strings[1])));;
                    }
                } else {
                    Help(commandSender);
                }
            } else if(strings.length == 2) {
                if (strings[0].equalsIgnoreCase("-i")) {
                    String inds = strings[1].toUpperCase(Locale.ROOT);
                    if (indices.containsKey(inds)) {
                        BISTIndicesSymbols BISTIndicesSymbols = new BISTIndicesSymbols(inds);
                        BISTIndices BISTIndices = new BISTIndices(BISTIndicesSymbols);
                        for (String indsInfo : get().getConfig().getStringList("trader-mc.messages.commands.tradermc.indices-info")) {
                            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', indsInfo.replace("%indices_name%", BISTIndices.getStockSymbolName()).replace("%indices_rate%", BISTIndices.getCurrentlyRate()).replace("%indices_change%", BISTIndices.getCurrentlyChangeOfPrice()).replace("%indices_lastupdate%", BISTIndices.getLatestUpdateTime()).replace("%indices_price%", BISTIndices.getCurrentlyPrice())));
                        }
                    } else {
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', get().getConfig().getString("trader-mc.prefix") + " &r" + get().getConfig().getString("trader-mc.messages.indices-not-found")));;
                    }
                } else if (strings[0].equalsIgnoreCase("-t")) {
                    Player plyr = Bukkit.getPlayer(strings[1]);
                    if (plyr != null) {
                        addIndices(plyr.getUniqueId(), "TAVHL", 1);
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', get().getConfig().getString("trader-mc.prefix") + " &r" + get().getConfig().getString("trader-mc.messages.test-sended").replace("%player%", strings[1])));;
                    } else {
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', get().getConfig().getString("trader-mc.prefix") + " &r" + get().getConfig().getString("trader-mc.messages.not-online").replace("%player%", strings[1])));;
                    }
                } else if (strings[0].equalsIgnoreCase("-ui")) {
                    Player plyr = Bukkit.getPlayer(strings[1]);
                    if (plyr != null) {
                        JsonParser jsonParser = new JsonParser();
                        List<String> indicesList = get().getConfig().getStringList("trader-mc.indices");
                        List<String> ownedIndices = new ArrayList<>();
                        JsonObject parsed = jsonParser.parse(getIndices(plyr.getUniqueId())).getAsJsonObject();
                        for(String inds : indicesList) {
                            if(parsed.get(inds) != null) {
                                String editedInds = String.format(get().getConfig().getString("trader-mc.messages.admin.indices-list.format"), inds,parsed.get(inds));
                                ownedIndices.add(editedInds);
                            }
                        }
                        plyr.sendMessage(ChatColor.translateAlternateColorCodes('&', get().getConfig().getString("trader-mc.messages.admin.indices-list.title").replace("%player%", plyr.getName())));
                        for(int a = 0; a<ownedIndices.size(); a++) {
                            plyr.sendMessage(ChatColor.translateAlternateColorCodes('&', get().getConfig().getString("trader-mc.messages.admin.indices-list.elements").replace("%indices%", ownedIndices.get(a))));
                        }
                    } else {
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', get().getConfig().getString("trader-mc.prefix") + " &r" + get().getConfig().getString("trader-mc.messages.not-online").replace("%player%", strings[1])));;
                    }
                } else {
                    Help(commandSender);
                }
            } else if (strings.length == 1) {
                if (strings[0].equalsIgnoreCase("-rel")) {
                    get().reloadConfig();
                    get().saveConfig();
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', get().getConfig().getString("trader-mc.prefix") + " &r" + get().getConfig().getString("trader-mc.messages.reloaded")));
                } else if (strings[0].equalsIgnoreCase("-ir")) {
                    updateIndices();
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', get().getConfig().getString("trader-mc.prefix") + " &r" + get().getConfig().getString("trader-mc.messages.updated").replace("%updated%", String.valueOf(updated)).replace("%skipped%", String.valueOf(skipped))));
                } else if (strings[0].equalsIgnoreCase("-il")) {
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', get().getConfig().getString("trader-mc.prefix") + " &r" + get().getConfig().getString("trader-mc.messages.indices-list").replace("%indices%", indices.toString().replace("{", "").replace("}", ""))));
                } else if (strings[0].equalsIgnoreCase("-ctu")) {
                    Random r = new Random();
                    int low = 1000;
                    int high = 10000;
                    int result = r.nextInt(high-low) + low;
                    createDataWithCheck(String.valueOf(result), "Test_" + result);
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', get().getConfig().getString("trader-mc.prefix") + " &7Test verisi başarıyla oluşturuldu."));
                } else {
                    Help(commandSender);
                }
            } else {
                Help(commandSender);
            }
        } else {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', get().getConfig().getString("trader-mc.prefix") + " &r" + get().getConfig().getString("trader-mc.messages.no-permission")));;
        }
        return false;
    }
}
