package fr.kanpvp.copsplugin;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class PlayerData {


    public static HashMap<UUID, PlayerData> pDataList = new HashMap<>();
    public Player player;
    public int star;

    public PlayerData(Player player){
        this.player = player;
        this.star = 0;
        pDataList.put(player.getUniqueId(), this);
    }

    public void setStar(int star){
        this.star = star;
        pDataList.put(this.player.getUniqueId(), this);
    }

    public static PlayerData playerDataFromPlayer(Player player){
        if( pDataList.containsKey(player.getUniqueId()) ){
            return pDataList.get(player.getUniqueId());
        }
        return null;
    }




}
