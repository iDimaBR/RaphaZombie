package com.github.idimabr.raphazombie.listeners;

import com.github.idimabr.raphazombie.RaphaZombie;
import com.github.idimabr.raphazombie.managers.CacheManager;
import com.github.idimabr.raphazombie.objects.ZPlayer;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class ZombieListener implements Listener {

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent e){
        if(!e.getDamager().getType().toString().contains("ZOMBIE")) return;
        if(e.getEntity().getType() != EntityType.PLAYER) return;
        if(e.isCancelled()) return;
        Player player = (Player) e.getEntity();
        if(!player.getWorld().getName().equalsIgnoreCase(RaphaZombie.getInstance().config.getString("Mundo"))) return;

        ZPlayer zplayer = CacheManager.cache.get(player.getUniqueId());
        if(zplayer == null) return;
        if(zplayer.isInfected()) return;

        int chance = RaphaZombie.getInstance().config.getInt("Chance.Membro");
        if(player.hasPermission("zombie.vip"))
            chance = RaphaZombie.getInstance().config.getInt("Chance.VIP");

        if(CacheManager.random.nextInt(100) < chance){
            zplayer.setInfected(true);
            player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * RaphaZombie.getInstance().config.getInt("Efeitos.Confusao.Tempo"), RaphaZombie.getInstance().config.getInt("Efeitos.Confusao.Nivel")));
        }
    }

    @EventHandler
    public void event(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player player = (Player)e.getEntity();
        if(e.isCancelled()) return;
        if(!player.getWorld().getName().equalsIgnoreCase(RaphaZombie.getInstance().config.getString("Mundo"))) return;
        ZPlayer zplayer = CacheManager.cache.get(player.getUniqueId());
        if(zplayer == null) return;
        if(zplayer.isBlooding()) return;

        if (player.getGameMode().equals(GameMode.SURVIVAL) && e.getCause() == EntityDamageEvent.DamageCause.FALL || e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK){
            if(e.getDamage() >= 6){
                zplayer.setBlooding(true);
                return;
            }

            int chance = RaphaZombie.getInstance().config.getInt("Chance.Membro");
            if(player.hasPermission("zombie.vip"))
                chance = RaphaZombie.getInstance().config.getInt("Chance.VIP");

            if(CacheManager.random.nextInt(100) < chance){
                zplayer.setBlooding(true);
            }
        }
    }

    @EventHandler
    public void onZombieDeath(EntityDeathEvent e){
        if(e.getEntityType() != EntityType.ZOMBIE) return;
        if(e.getEntity().getKiller() == null) return;
        if(e.getEntity().getKiller().getType() != EntityType.PLAYER) return;
        if(!e.getEntity().getWorld().getName().equalsIgnoreCase(RaphaZombie.getInstance().config.getString("Mundo"))) return;

        Player player = e.getEntity().getKiller();
        ZPlayer zplayer = CacheManager.cache.get(player.getUniqueId());
        if(zplayer == null) return;

        int chance = RaphaZombie.getInstance().config.getInt("Chance.Membro");
        if(player.hasPermission("zombie.vip"))
            chance = RaphaZombie.getInstance().config.getInt("Chance.VIP");

        if(CacheManager.random.nextInt(100) < chance){
            e.getEntity().getWorld().dropItemNaturally(e.getEntity().getLocation(), RaphaZombie.drop);
        }

        zplayer.setAbates(zplayer.getAbates() + 1);
    }

    @EventHandler
    public void onEntityCombust(EntityCombustEvent e) {
        if(e.getEntity().getLocation().getWorld().getName().equalsIgnoreCase(RaphaZombie.getInstance().config.getString("Mundo")))
            if (e.getEntity() instanceof Zombie)
                e.setCancelled(true);
    }


    @EventHandler
    public void event(EntitySpawnEvent e) {
        if(!e.getLocation().getWorld().getName().equalsIgnoreCase(RaphaZombie.getInstance().config.getString("Mundo"))) return;

        if (e.getEntity() instanceof org.bukkit.entity.Skeleton) {
            e.setCancelled(true);
            randomZombie(e.getLocation());
            return;
        }
        if (e.getEntity() instanceof org.bukkit.entity.Creeper) {
            randomZombie(e.getLocation());
            e.setCancelled(true);
        }
        if (e.getEntity() instanceof org.bukkit.entity.Spider) {
            randomZombie(e.getLocation());
            e.setCancelled(true);
        }
        if (e.getEntity() instanceof org.bukkit.entity.CaveSpider) {
            randomZombie(e.getLocation());
            e.setCancelled(true);
        }
        if (e.getEntity() instanceof org.bukkit.entity.Enderman) {
            randomZombie(e.getLocation());
            e.setCancelled(true);
        }
        if (e.getEntity() instanceof org.bukkit.entity.Slime) {
            randomZombie(e.getLocation());
            e.setCancelled(true);
        }
        if (e.getEntity() instanceof org.bukkit.entity.Witch) {
            randomZombie(e.getLocation());
            e.setCancelled(true);
        }
        if (e.getEntity() instanceof org.bukkit.entity.Cow) {
            int chance = (new Random()).nextInt(100);
            if (chance <= 50) {
                e.setCancelled(true);
                randomZombie(e.getLocation());
            }
        }
        if (e.getEntity() instanceof org.bukkit.entity.Chicken) {
            int chance = (new Random()).nextInt(100);
            if (chance <= 50) {
                e.setCancelled(true);
                randomZombie(e.getLocation());
            }
        }
        if (e.getEntity() instanceof org.bukkit.entity.Pig) {
            int chance = (new Random()).nextInt(100);
            if (chance <= 50) {
                e.setCancelled(true);
                randomZombie(e.getLocation());
            }
        }
        if (e.getEntity() instanceof org.bukkit.entity.Sheep) {
            int chance = (new Random()).nextInt(100);
            if (chance <= 50) {
                e.setCancelled(true);
                randomZombie(e.getLocation());
            }
        }
    }

    public void randomZombie(Location loc) {
        int quantidade = (new Random()).nextInt(5);
        if (quantidade == 0)
            quantidade = 1;
        for (int i = 0; i <= quantidade; i++) {
            int pot = (new Random()).nextInt(2);
            Zombie zombie = (Zombie)loc.getWorld().spawnEntity(loc, EntityType.ZOMBIE);
            zombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 9999, pot));
        }
    }
}
