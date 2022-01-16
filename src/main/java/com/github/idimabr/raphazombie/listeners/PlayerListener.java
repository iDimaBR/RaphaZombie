package com.github.idimabr.raphazombie.listeners;

import com.github.idimabr.raphazombie.RaphaZombie;
import com.github.idimabr.raphazombie.managers.CacheManager;
import com.github.idimabr.raphazombie.objects.ZPlayer;
import com.github.idimabr.raphazombie.utils.ItemBuilder;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class PlayerListener implements Listener {

    private Inventory invConfirm = Bukkit.createInventory(null, 9 * 3, RaphaZombie.getInstance().config.getString("InventarioConfirmar.Titulo"));

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        UUID uuid = e.getPlayer().getUniqueId();
        ZPlayer status = new ZPlayer(uuid);
        if(!CacheManager.cache.containsKey(uuid))
            CacheManager.cache.put(uuid, status);
        CacheManager.loadCache(false, uuid);
    }

    @EventHandler
    public void onLeft(PlayerQuitEvent e){
        UUID uuid = e.getPlayer().getUniqueId();
        ZPlayer zplayer = CacheManager.cache.get(uuid);
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
        CacheManager.cache.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onLeft(PlayerKickEvent e){
        UUID uuid = e.getPlayer().getUniqueId();
        ZPlayer zplayer = CacheManager.cache.get(uuid);
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
        CacheManager.cache.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void event(PlayerDeathEvent e) {
        Player player = e.getEntity();
        if(!player.getWorld().getName().equalsIgnoreCase(RaphaZombie.getInstance().config.getString("Mundo"))) return;

        ZPlayer zplayer = CacheManager.cache.get(player.getUniqueId());
        if (zplayer == null) return;

        if (zplayer.isInfected()) {
            Bukkit.broadcastMessage(RaphaZombie.getInstance().messages.getString("Morte.Zumbi").replace("&","§").replace("%player%", player.getName()));
            zplayer.resetPlayer();
        }
    }

    @EventHandler
    public void onMove(FoodLevelChangeEvent e){
        Player player = (Player) e.getEntity();
        if(!player.getWorld().getName().equalsIgnoreCase(RaphaZombie.getInstance().config.getString("Mundo"))) return;
        if(e.isCancelled()) return;

        ZPlayer zplayer = CacheManager.cache.get(player.getUniqueId());
        if(zplayer == null) return;

        int chance = RaphaZombie.getInstance().config.getInt("Chance.Membro");
        if(player.hasPermission("zombie.vip"))
            chance = RaphaZombie.getInstance().config.getInt("Chance.VIP");

        if(player.getFoodLevel() <= 20 && CacheManager.random.nextInt(100) < chance){
            zplayer.setSede(zplayer.getSede() - 3);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e){
        if(!e.getAction().toString().contains("RIGHT_CLICK")) return;
        if(e.getItem() == null) return;
        Player player = e.getPlayer();
        if(!player.getWorld().getName().equalsIgnoreCase(RaphaZombie.getInstance().config.getString("Mundo"))) return;

        ItemStack item = e.getItem();
        if(!item.hasItemMeta()) return;
        if(!item.getItemMeta().hasDisplayName()) return;
        ZPlayer zplayer = CacheManager.cache.get(player.getUniqueId());
        if(zplayer == null) return;

        String nameItem = item.getItemMeta().getDisplayName();
        if(RaphaZombie.soro.getItemMeta().getDisplayName().equals(nameItem)){
            e.setCancelled(true);
            if(zplayer.getSede() == 100){
                player.sendMessage(RaphaZombie.getInstance().messages.getString("Erro.NaoSede").replace("&","§").replace("%player%", player.getName()));
                return;
            }
            zplayer.setSede(Math.min((zplayer.getSede() + 20), 100));
            player.sendMessage(RaphaZombie.getInstance().messages.getString("Utilizar.Soro").replace("&","§").replace("%player%", player.getName()));
            player.playSound(player.getLocation(), Sound.DRINK, 1, 1);
            removeItem(player);
        }else if(RaphaZombie.medico.getItemMeta().getDisplayName().equals(nameItem)){
            e.setCancelled(true);
            if(!zplayer.isBlooding()){
                player.sendMessage(RaphaZombie.getInstance().messages.getString("Erro.NaoSangrando").replace("&","§").replace("%player%", player.getName()));
                return;
            }
            player.sendMessage(RaphaZombie.getInstance().messages.getString("Utilizar.Kit").replace("&","§").replace("%player%", player.getName()));
            zplayer.setBlooding(false);
            player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
            removeItem(player);
        }else if(RaphaZombie.morfina.getItemMeta().getDisplayName().equals(nameItem)){
            e.setCancelled(true);
            if(!zplayer.isInfected()){
                player.sendMessage(RaphaZombie.getInstance().messages.getString("Erro.NaoInfectado").replace("&","§").replace("%player%", player.getName()));
                return;
            }
            player.sendMessage(RaphaZombie.getInstance().messages.getString("Utilizar.Morfina").replace("&","§").replace("%player%", player.getName()));
            zplayer.setInfected(false);
            player.playSound(player.getLocation(), Sound.DRINK, 1, 1);
            removeItem(player);
        }else if(RaphaZombie.drop.getItemMeta().getDisplayName().equalsIgnoreCase(nameItem)){
            e.setCancelled(true);
            invConfirm.setItem(11, new ItemBuilder(Material.STAINED_CLAY).setDurability((short) 5).setName("§aConfirmar").toItemStack());
            invConfirm.setItem(13, RaphaZombie.drop);
            invConfirm.setItem(15, new ItemBuilder(Material.STAINED_CLAY).setDurability((short) 14).setName("§cCancelar").toItemStack());
            player.openInventory(invConfirm);
        }
    }

    @EventHandler
    public void InventoryConfirmEvent(InventoryClickEvent e){
        if(e.getCurrentItem() == null) return;
        if(!e.getInventory().getTitle().equals(invConfirm.getTitle())) return;
        Player player = (Player) e.getWhoClicked();
        ItemStack item = e.getCurrentItem();
        if(!item.hasItemMeta()) return;
        if(!item.getItemMeta().hasDisplayName()) return;

        if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§aConfirmar")){
            for(String command : RaphaZombie.getInstance().config.getStringList("Inventario.Comandos"))
                Bukkit.dispatchCommand(player, command.replace("%player%", player.getName()));
            player.closeInventory();
            removeItem(player);
        }else if(item.getItemMeta().getDisplayName().equalsIgnoreCase("§cCancelar")){
            player.closeInventory();
        }
        e.setCancelled(true);
        player.updateInventory();
    }

    @EventHandler
    public void onEnterWorld(PlayerChangedWorldEvent e){
        Player player = e.getPlayer();
        if(!e.getFrom().getName().equalsIgnoreCase(RaphaZombie.getInstance().config.getString("Mundo"))){
            Bukkit.broadcastMessage(RaphaZombie.getInstance().messages.getString("Mundo.Entrou").replace("&","§").replace("%player%", player.getName()));
        }else{
            Bukkit.broadcastMessage(RaphaZombie.getInstance().messages.getString("Mundo.Saiu").replace("&","§").replace("%player%", player.getName()));
        }
    }

    public void removeItem(Player player){
        if(player.getItemInHand().getAmount() == 1) {
            player.setItemInHand(new ItemStack(Material.AIR));
        }else{
            ItemStack hand = player.getItemInHand();
            hand.setAmount(hand.getAmount() - 1);
            player.setItemInHand(hand);
        }
    }
}
