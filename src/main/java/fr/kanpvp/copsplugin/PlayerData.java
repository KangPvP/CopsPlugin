package fr.kanpvp.copsplugin;

import org.bukkit.entity.Player;

public class PlayerData {

    public int star;
    public boolean isWanted;

    public PlayerData(Player player){
        this.star = 0;
        this.isWanted = false;
    }


}
