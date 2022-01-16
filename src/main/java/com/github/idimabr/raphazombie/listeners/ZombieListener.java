package com.github.idimabr.raphazombie.listeners;

import com.github.idimabr.raphazombie.RaphaZombie;
import com.github.idimabr.raphazombie.managers.CacheManager;
import com.github.idimabr.raphazombie.objects.ZPlayer;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

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
    public void event(CreatureSpawnEvent e) {
        if(e.getLocation().getWorld().getName().equalsIgnoreCase(RaphaZombie.getInstance().config.getString("Mundo")))
            if(!e.getEntityType().toString().contains("ZOMBIE"))
                e.setCancelled(true);
    }
}
