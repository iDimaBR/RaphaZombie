package com.github.idimabr.raphazombie;

import com.github.idimabr.raphazombie.commands.InfectCommand;
import com.github.idimabr.raphazombie.database.MySQL;
import com.github.idimabr.raphazombie.listeners.PlayerListener;
import com.github.idimabr.raphazombie.listeners.ZombieListener;
import com.github.idimabr.raphazombie.managers.CacheManager;
import com.github.idimabr.raphazombie.objects.ZPlayer;
import com.github.idimabr.raphazombie.placeholders.ZombiePlaceholder;
import com.github.idimabr.raphazombie.utils.ActionBar;
import com.github.idimabr.raphazombie.utils.ConfigUtil;
import com.github.idimabr.raphazombie.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class RaphaZombie extends JavaPlugin {

    public static ItemStack morfina;
    public static ItemStack medico;
    public static ItemStack soro;
    public static ItemStack drop;

    public ConfigUtil config;
    public ConfigUtil messages;
    public MySQL sql;

    public static RaphaZombie getInstance() {
        return getPlugin(RaphaZombie.class);
    }

    @Override
    public void onEnable() {
        config = new ConfigUtil(null, "config.yml", false);
        config.saveConfig();
        messages = new ConfigUtil(null, "messages.yml", false);
        messages.saveConfig();

        sql = new MySQL();
        sql.createTable();
        if( Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")){
            new ZombiePlaceholder().register();
        }
        loadItens();
        getCommand("infect").setExecutor(new InfectCommand());
        Bukkit.getPluginManager().registerEvents(new ZombieListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        CacheManager.loadCache(true, null);

        Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            @Override
            public void run() {
                World world = Bukkit.getWorld(config.getString("Mundo"));
               world.getPlayers().forEach(player -> {
                    world.setTime(13700);
                    if(CacheManager.cache.containsKey(player.getUniqueId())){
                        ZPlayer zplayer = CacheManager.cache.get(player.getUniqueId());

                        String txtSede = "";
                        String txtBlood = "";
                        String txtInfected = "";

                        if(zplayer == null) return;
                        if(zplayer.getSede() < 50){
                            txtSede = " §c ⚠Sede⚠ ";
                        }

                        if(zplayer.isBlooding()){
                            txtBlood = " §c ☠Sangrando☠ ";
                            player.removePotionEffect(PotionEffectType.SLOW);
                            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 25, 1));
                            if(player.getHealth() > 6 && CacheManager.random.nextInt(100) < 15){
                                player.damage(1);
                                player.getWorld().playEffect(player.getLocation().add(0.0D, 1.0D, 0.0D), Effect.STEP_SOUND, 152);
                            }
                        }

                        if(zplayer.isInfected()){
                            txtInfected = " §c ☢Infectado☢ ";
                            player.removePotionEffect(PotionEffectType.POISON);
                            player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 25, 1));
                        }

                        if(zplayer.getSede() < 10) {
                            player.damage(1);
                            if(zplayer.getSede() <= 0) {
                                Bukkit.broadcastMessage(messages.getString("Morte.Sede").replace("&","§").replace("%player%", player.getName()));
                                zplayer.resetPlayer();
                                player.setHealth(0);
                            }
                        }

                        new ActionBar(RaphaZombie.getInstance().messages.getString("ActionBAR").replace("&","§").replace("%player%", player.getName()).replace("%sede%", zplayer.getSede()+"").replace("%avisos%", txtSede + txtBlood + txtInfected)).send(player);
                    }
                });
            }
        }, 20L, 20L);
    }

    @Override
    public void onDisable() {
        CacheManager.saveCache();
    }

    public void loadItens(){
        morfina = new ItemBuilder(
                Material.valueOf(config.getString("Itens.Morfina.Material"))
        ).setName(config.getString("Itens.Morfina.Nome").replace("&","§"))
                .setLore(config.getStringList("Itens.Morfina.Lore")).toItemStack();

        medico = new ItemBuilder(
                Material.valueOf(config.getString("Itens.KitMedico.Material"))
        ).setName(config.getString("Itens.KitMedico.Nome").replace("&","§"))
                .setLore(config.getStringList("Itens.KitMedico.Lore")).toItemStack();

        soro = new ItemBuilder(
                Material.valueOf(config.getString("Itens.Soro.Material"))
        ).setName(config.getString("Itens.Soro.Nome").replace("&","§"))
                .setLore(config.getStringList("Itens.Soro.Lore")).toItemStack();

        drop = new ItemBuilder(
                Material.valueOf(config.getString("Itens.DropZombie.Material"))
        ).setName(config.getString("Itens.DropZombie.Nome").replace("&","§"))
                .setLore(config.getStringList("Itens.DropZombie.Lore")).toItemStack();

        registerRecipes();
    }

    public void registerRecipes(){
        ShapedRecipe morfinaRecipe = new ShapedRecipe(morfina);
        morfinaRecipe.shape(config.getString("Crafting.Morfina.Recipe.Row1"), config.getString("Crafting.Morfina.Recipe.Row2"), config.getString("Crafting.Morfina.Recipe.Row3"));
        for(String ingredient : config.getStringList("Crafting.Morfina.Ingredientes")){
            String[] split = ingredient.split(";");
            morfinaRecipe.setIngredient(split[0].charAt(0), Material.valueOf(split[1]));
        }
        getServer().addRecipe(morfinaRecipe);

        ShapedRecipe medicoRecipe = new ShapedRecipe(medico);
        medicoRecipe.shape(config.getString("Crafting.KitMedico.Recipe.Row1"), config.getString("Crafting.KitMedico.Recipe.Row2"), config.getString("Crafting.KitMedico.Recipe.Row3"));
        for(String ingredient : config.getStringList("Crafting.KitMedico.Ingredientes")){
            String[] split = ingredient.split(";");
            medicoRecipe.setIngredient(split[0].charAt(0), Material.valueOf(split[1]));
        }
        getServer().addRecipe(medicoRecipe);

        ShapedRecipe soroRecipe = new ShapedRecipe(soro);
        soroRecipe.shape(config.getString("Crafting.Soro.Recipe.Row1"), config.getString("Crafting.Soro.Recipe.Row2"), config.getString("Crafting.Soro.Recipe.Row3"));
        for(String ingredient : config.getStringList("Crafting.Soro.Ingredientes")){
            String[] split = ingredient.split(";");
            soroRecipe.setIngredient(split[0].charAt(0), Material.valueOf(split[1]));
        }
        getServer().addRecipe(soroRecipe);
    }
}
