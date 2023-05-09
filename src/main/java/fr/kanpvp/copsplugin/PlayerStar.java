package fr.kanpvp.copsplugin;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class PlayerStar {
    public static HashMap<UUID, PlayerStar> pDataList = new HashMap<>();
    public Player player;
    public int star;

    public PlayerStar(Player player){
        this.player = player;
        this.star = 0;
        pDataList.put(player.getUniqueId(), this);
    }

    public int getStar(){
        return this.star;
    }

    public void setStar(int star){
        this.star = star;
        pDataList.put(player.getUniqueId(), this);
    }


    public static PlayerStar playerDataFromPlayer(Player player){
        if( pDataList.containsKey(player.getUniqueId()) ){
            return pDataList.get(player.getUniqueId());
        }
        return null;
    }

    /*public int getDataStar(Player player){
        NamespacedKey namespacedKey = new NamespacedKey(CopsPlugin.getInstance(), "star");

        PersistentDataContainer data = player.getPersistentDataContainer();

        if (!data.has(namespacedKey, PersistentDataType.INTEGER)){
            data.set(namespacedKey, PersistentDataType.INTEGER, 0);
            return 0;
        }

        return data.get(namespacedKey, PersistentDataType.INTEGER);
    }

    public void setDataStar(){

        Player player = this.player;
        int star = this.star;

        NamespacedKey namespacedKey = new NamespacedKey(CopsPlugin.getInstance(), "star");

        PersistentDataContainer data = player.getPersistentDataContainer();

        if (!data.has(namespacedKey, PersistentDataType.INTEGER)){
            data.set(namespacedKey, PersistentDataType.INTEGER, 0);
        }

        data.set(namespacedKey, PersistentDataType.INTEGER, star);
    }*/

    public void addStar(){
        int star = this.star;

        if(star < 5){
            this.setStar(star + 1);
        }

    }





}
