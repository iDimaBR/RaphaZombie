package com.github.idimabr.raphazombie.managers;

import com.github.idimabr.raphazombie.RaphaZombie;
import com.github.idimabr.raphazombie.objects.ZPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class CacheManager {

    public static Random random = new Random();
    private static RaphaZombie instance = RaphaZombie.getInstance();
    public static Map<UUID, ZPlayer> cache = new HashMap<>();

    public static void saveCache() {
        for(Map.Entry<UUID, ZPlayer> values : cache.entrySet()){
            UUID uuid = values.getKey();
            ZPlayer zplayer = values.getValue();
            if(zplayer == null) return;

            Connection connection = RaphaZombie.getInstance().sql.getConnectionMySQL();
            String query = "UPDATE players SET UUID = ?, infected = ?, blood = ?, sede = ?, abates = ? WHERE UUID = ?";
            if(!RaphaZombie.getInstance().sql.contains(uuid.toString()))
                query = "INSERT INTO players(`UUID`,`infected`,`blood`,`sede`,`abates`) VALUES (?,?,?,?,?)";

            try(PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, zplayer.getUUID().toString());
                ps.setBoolean(2, zplayer.isInfected());
                ps.setBoolean(3, zplayer.isBlooding());
                ps.setInt(4, zplayer.getSede());
                ps.setInt(5, zplayer.getAbates());
                if(query.contains("WHERE"))
                    ps.setString(6, zplayer.getUUID().toString());
                ps.execute();
            } catch (SQLException error) {
                error.printStackTrace();
            }
            cache.remove(uuid);
        }
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
                    System.out.println("Carregou o jogador " + uuid.toString() + " com " + zplayer.getAbates() + " abates");
                    cache.put(uuid, zplayer);
                }
            }
        } catch (Exception error) {
            error.printStackTrace();
        }
    }


}
