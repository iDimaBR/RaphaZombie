package com.github.idimabr.raphazombie.placeholders;

import com.github.idimabr.raphazombie.managers.CacheManager;
import com.github.idimabr.raphazombie.objects.ZPlayer;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ZombiePlaceholder extends PlaceholderExpansion {

    /*
    The identifier, shouldn't contain any _ or %
     */
    public String getIdentifier() {
        return "raphazombie";
    }

    /*
     The author of the Placeholder
     This cannot be null
     */
    public String getAuthor() {
        return "iDimaBR";
    }

    /*
     Same with #getAuthor() but for versioon
     This cannot be null
     */

    public String getVersion() {
        return "1.0";
    }

    /*
    Use this method to setup placeholders
    This is somewhat similar to EZPlaceholderhook
     */
    public String onPlaceholderRequest(Player player, String identifier) {
        /*
         %tutorial_onlines%
         Returns the number of online players
          */

        ZPlayer zplayer = CacheManager.cache.get(player.getUniqueId());

        if(identifier.equalsIgnoreCase("estado")){
            if(zplayer.isInfected()){
                return "Infectado";
            }else{
                return "Saudável";
            }
        }
 
        /*
        %tutorial_name%
        Returns the player name
         */
        if(identifier.equalsIgnoreCase("abates")){
            return zplayer.getAbates()+"";
        }
 
 
        return null;
    }
}