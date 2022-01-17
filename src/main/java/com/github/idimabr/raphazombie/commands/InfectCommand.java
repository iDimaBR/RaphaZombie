package com.github.idimabr.raphazombie.commands;

import com.github.idimabr.raphazombie.RaphaZombie;
import com.github.idimabr.raphazombie.utils.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class InfectCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(!(sender instanceof Player)) return false;

        Player player = (Player) sender;

        if(!player.hasPermission("raphazombie.admin")){
            player.sendMessage("§cVocê não tem permissão.");
            return false;
        }

        if(args.length == 4){
            if(args[0].equalsIgnoreCase("give")){

                Player alvo = Bukkit.getPlayer(args[1]);
                if(alvo == null){
                    player.sendMessage("§cJogador não encontrado!");
                    return false;
                }

                int amount = 1;

                try{
                    amount = Integer.parseInt(args[2]);
                }catch(Exception e){
                    player.sendMessage("§cColoque um número válido!");
                    return false;
                }

                switch(args[3].toLowerCase()){
                    case "soro":
                        for(int i = 0;i < amount;i++)
                            alvo.getInventory().addItem(RaphaZombie.soro);
                        player.sendMessage("§aVocê recebeu §f" + amount + "§a soro.");
                        break;
                    case "kit":
                        for(int i = 0;i < amount;i++)
                            alvo.getInventory().addItem(RaphaZombie.medico);
                        player.sendMessage("§aVocê recebeu §f" + amount + "§a kit médico.");
                        break;
                    case "morfina":
                        for(int i = 0;i < amount;i++)
                            alvo.getInventory().addItem(RaphaZombie.morfina);
                        player.sendMessage("§aVocê recebeu §f" + amount + "§a morfina.");
                        break;
                    default:
                        player.sendMessage("§cItens disponíveis: §fsoro, kit §ce §fmorfina");
                        return false;
                }
            }
        }else{
            if(args.length == 1){
                if(args[0].equalsIgnoreCase("reload")){
                    RaphaZombie.getInstance().config = new ConfigUtil(null, "config.yml", true);
                    RaphaZombie.getInstance().config.saveConfig();
                    RaphaZombie.getInstance().messages = new ConfigUtil(null, "messages.yml", true);
                    RaphaZombie.getInstance().messages.saveConfig();

                    player.sendMessage("§aConfig reiniciada!");
                    return false;
                }else{
                    player.sendMessage("§cUtilize /infect reload");
                    return false;
                }
            }
            player.sendMessage("§cUtilize /infect give <jogador> <quantidade> <soro/kit/morfina>");
        }
        return false;
    }
}
