package com.github.idimabr.raphazombie.managers;

import com.github.idimabr.raphazombie.RaphaZombie;
import com.github.idimabr.raphazombie.objects.ZPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

public class CacheManager {

    public static Random random = new Random();
    private static RaphaZombie instance = RaphaZombie.getInstance();
    public static HashMap<UUID, ZPlayer> cache = new HashMap<UUID, ZPlayer>();

    public static void saveCache() {
        cache.forEach((uuid, zplayer) -> {
            Connection connection = instance.sql.getConnectionMySQL();
            String query = "UPDATE players SET infected = ?, blood = ?, sede = ?, abates = ? WHERE UUID = ?;";
            if(!instance.sql.contains(uuid.toString()))
                query = "INSERT INTO players(`UUID`,`infected`,`blood`,`sede`,`abates`) VALUES (?,?,?,?,?);";
            try(PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setBoolean(1, zplayer.isInfected());
                ps.setBoolean(2, zplayer.isBlooding());
                ps.setInt(3, zplayer.getSede());
                ps.setInt(4, zplayer.getAbates());
                ps.setString(5, zplayer.getUUID().toString());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static void loadCache(boolean allPlayers, UUID uuidPlayer) {
        Connection connection = instance.sql.getConnectionMySQL();
        String query = "SELECT * FROM players";
        if(!allPlayers){
            query = "SELECT * FROM players WHERE UUID = '"+ uuidPlayer +"';";
        }
        try(PreparedStatement ps = connection.prepareStatement(query)) {
            try(ResultSet rs = ps.executeQuery()) {
                while(rs.next()){
                    UUID uuid = UUID.fromString(rs.getString("UUID"));
                    ZPlayer zplayer = new ZPlayer(uuid);
                    zplayer.setBlooding(rs.getBoolean("blood"));
                    zplayer.setInfected(rs.getBoolean("infected"));
                    zplayer.setAbates(rs.getInt("abates"));
                    zplayer.setSede(rs.getInt("sede"));
                    cache.put(uuid, zplayer);
                }
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }


}
