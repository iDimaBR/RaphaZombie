package com.github.idimabr.raphazombie.objects;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ZPlayer {

    private boolean infected;
    private int sede;
    private boolean blood;
    private int abates;
    private UUID uuid;

    public ZPlayer(UUID uuid) {
        this.infected = false;
        this.sede = 100;
        this.blood = false;
        this.uuid = uuid;
        this.abates = 0;
    }

    public boolean isInfected() {
        return infected;
    }

    public void setInfected(boolean infected) {
        this.infected = infected;
    }

    public void resetPlayer(){
        this.setSede(100);
        this.setInfected(false);
        this.setBlooding(false);
    }

    public int getAbates() {
        return abates;
    }

    public void setAbates(int abates) {
        this.abates = abates;
    }

    public int getSede() {
        return sede;
    }

    public void setSede(int sede) {
        this.sede = sede;
    }

    public boolean isBlooding() {
        return blood;
    }

    public void setBlooding(boolean blood) {
        this.blood = blood;
    }

    public UUID getUUID() {
        return uuid;
    }
}
