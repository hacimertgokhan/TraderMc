package net.mixium.tradermc.storage.mysql;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.sql.*;
import java.util.List;
import java.util.UUID;

import static net.mixium.tradermc.TraderMc.get;
import static net.mixium.tradermc.config.Config.getLanguage;
import static net.mixium.tradermc.maps.Indices.indices;

public class Database {
    public Database(){}
    public static Connection getConnection() {
        try {
            String url = "jdbc:mysql://"+get().getConfig().getString("mysql.address")+"/"+get().getConfig().getString("mysql.database");
            String user = get().getConfig().getString("mysql.username");
            String password = get().getConfig().getString("mysql.password");
            return DriverManager.getConnection(url, user, password).createStatement().getConnection();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public void initializeDatabase() throws SQLException {
        Statement statement = getConnection().createStatement();
        String sql = "CREATE TABLE IF NOT EXISTS `" + get().getConfig().getString("mysql.database") + "`.`TraderMc` (`uuid` VARCHAR(255) NOT NULL , `name` VARCHAR(255) NOT NULL , `indices` VARCHAR(255) NOT NULL , `total` VARCHAR(255) NOT NULL ) ENGINE = InnoDB;";
        statement.execute(sql);
        statement.close();
        if(getLanguage().equalsIgnoreCase("tr")) {
            Bukkit.getLogger().info("[TraderMc] Bağlantı başarılı.");
        } else if(getLanguage().equalsIgnoreCase("en")) {
            Bukkit.getLogger().info("[TraderMc] Connection successfull.");
        }
    }

    public static boolean playerExists(String uuid) {
        try {
            PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM TraderMc WHERE uuid=?");
            statement.setString(1, uuid);
            ResultSet results = statement.executeQuery();

            if (results.next()) {
                return true;
            }
            results.close();
            getConnection().close();
            statement.close();
        } catch (Exception var3) {
            var3.printStackTrace();
        }

        return false;
    }

    public static void createDataWithoutCheck(String uuid, String name) {
        try {
            PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM TraderMc WHERE uuid=?");
            statement.setString(1, uuid);
            ResultSet results = statement.executeQuery();
            results.next();
            JSONObject playerIndices = new JSONObject();
            List<String> indicesList = get().getConfig().getStringList("trader-mc.indices");
            for(String inds : indicesList) {
                if(indices.get(inds) != null) {
                    playerIndices.put(inds, 0);
                }
            }
            double total = 0.0;
            PreparedStatement insert = getConnection().prepareStatement("INSERT INTO TraderMc (uuid,name,indices,total) VALUES (?,?,?,?)");
            insert.setString(1, uuid);
            insert.setString(2, name);
            insert.setString(3, String.valueOf(playerIndices));
            insert.setString(4, String.valueOf(total));
            insert.executeUpdate();
            insert.close();
            getConnection().close();
            statement.close();
        } catch (SQLException var10) {
            var10.printStackTrace();
        }
    }

    public static void UpdateIndices(Player player) {
        UUID uuid = player.getUniqueId();
        JsonParser jsonParsergs = new JsonParser();
        JSONParser jsonParser = new JSONParser();
        List<String> indicesList = get().getConfig().getStringList("trader-mc.indices");
        JSONObject parsed;
        JsonObject parsedgs;
        try {
            parsed = (JSONObject) jsonParser.parse(getIndices(uuid));
            parsedgs = jsonParsergs.parse(getIndices(uuid)).getAsJsonObject();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        JSONObject playerIndices = new JSONObject();
        for(String inds : indicesList) {

            /*

                Eklentiye başlangıç harici yeni bir endeks eklendiğinde burası hata veriyor.
                Methodu baştan sona anlayarak oku aga yoksa işin yaş.

             */

            if(parsedgs.get(inds) == null) {

            }

            if(parsed.get(inds) == null) {
                playerIndices.put(inds, 0);
            } else if (parsed.get(inds) != null) {
                playerIndices.put(inds, parsed.get(inds));
            }
        }
        setIndices(uuid, String.valueOf(playerIndices));

    }

    public static void createDataWithCheck(String uuid, String name) {
        try {
            PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM TraderMc WHERE uuid=?");
            statement.setString(1, uuid);
            ResultSet results = statement.executeQuery();
            results.next();
            JSONObject playerIndices = new JSONObject();
            List<String> indicesList = get().getConfig().getStringList("trader-mc.indices");
            for(String inds : indicesList) {
                if(indices.get(inds) != null) {
                    playerIndices.put(inds, 0);
                }
            }
            double total = 0.0;
            if (!playerExists(uuid)) {
                PreparedStatement insert = getConnection().prepareStatement("INSERT INTO TraderMc (uuid,name,indices,total) VALUES (?,?,?,?)");
                insert.setString(1, uuid);
                insert.setString(2, name);
                insert.setString(3, String.valueOf(playerIndices));
                insert.setString(4, String.valueOf(total));
                insert.executeUpdate();
                insert.close();
            }
            getConnection().close();
            statement.close();
        } catch (SQLException var10) {
            var10.printStackTrace();
        }
    }

    public static String getIndices(UUID uuid) {
        try {
            PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM TraderMc WHERE uuid=?");
            statement.setString(1, uuid.toString());
            ResultSet results = statement.executeQuery();
            results.next();
            return results.getString("indices");
        } catch (SQLException var4) {
            return null;
        }
    }
    public static void removeIndices(UUID uuid, String str, int amount) {
        try {
            PreparedStatement statement = getConnection().prepareStatement("UPDATE TraderMc SET indices=? WHERE uuid=?");

            JSONParser jsonParser = new JSONParser();
            JSONObject parsed;

            JsonParser jsonParsergs = new JsonParser();
            JsonObject parsedgs;
            try {
                parsed = (JSONObject) jsonParser.parse(getIndices(uuid));
                parsedgs = jsonParsergs.parse(getIndices(uuid)).getAsJsonObject();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            JSONObject playerIndices = parsed;
            if(indices.get(str) != null) {
                if(parsed.get(str) != null) {
                    if(parsedgs.get(str).getAsInt() >= amount) {
                        playerIndices.replace(str, parsedgs.get(str).getAsInt()-amount);
                    } else {
                        playerIndices.replace(str, 0);
                    }
                }
            }
            statement.setString(1, String.valueOf(playerIndices));
            statement.setString(2, uuid.toString());
            statement.executeUpdate();
            getConnection().close();
            statement.close();
        } catch (Exception var3) {
            var3.printStackTrace();
        }
    }
    public static void addIndices(UUID uuid, String str, int amount) {
        try {
            PreparedStatement statement = getConnection().prepareStatement("UPDATE TraderMc SET indices=? WHERE uuid=?");

            JSONParser jsonParser = new JSONParser();
            JSONObject parsed;

            JsonParser jsonParsergs = new JsonParser();
            JsonObject parsedgs;
            try {
                parsed = (JSONObject) jsonParser.parse(getIndices(uuid));
                parsedgs = jsonParsergs.parse(getIndices(uuid)).getAsJsonObject();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            JSONObject playerIndices = parsed;
            if(indices.get(str) != null) {
                if(parsed.get(str) != null) {
                    playerIndices.replace(str, parsedgs.get(str).getAsInt()+amount);
                }
            }
            statement.setString(1, String.valueOf(playerIndices));
            statement.setString(2, uuid.toString());
            statement.executeUpdate();
            getConnection().close();
            statement.close();
        } catch (Exception var3) {
            var3.printStackTrace();
        }
    }

    public static void setIndices(UUID uuid, String str) {
        try {
            PreparedStatement statement = getConnection().prepareStatement("UPDATE TraderMc SET indices=? WHERE uuid=?");
            statement.setString(1, str);
            statement.setString(2, uuid.toString());
            statement.executeUpdate();
            getConnection().close();
            statement.close();
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }

}
