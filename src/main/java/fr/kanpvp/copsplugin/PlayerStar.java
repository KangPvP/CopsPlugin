package fr.kanpvp.copsplugin;

import fr.kanpvp.copsplugin.cops.Cops;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class PlayerStar {
    public static HashMap<UUID, PlayerStar> pDataList = new HashMap<>();
    public Player player;
    public double star;

    public Long lastTimeChange;

    public PlayerStar(Player player){
        this.player = player;
        this.star = 0;
        this.lastTimeChange = System.currentTimeMillis();

        pDataList.put(player.getUniqueId(), this);
    }

    public double getStar(){
        return this.star;
    }

    public void setStar(double star){
        this.star = star;
        this.lastTimeChange = System.currentTimeMillis();
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
        double star = this.star;

        if(star < 5.5){
            if(star % 1 == 0){
                this.setStar(star + 1.0);
            } else {
                this.setStar(star + 0.5);
            }
        }
    }

    public void endStar(){
        double star = this.star;

        if(star < 5){
            this.setStar(star + 0.5);
        }
    }

    public void endStarCancel(){
        double star = this.star;

        if(star < 5){
            this.setStar(star - 0.5);
        }
    }

    public static void starActus(){
        new BukkitRunnable(){
            @Override
            public void run() {
                for(Player player : Bukkit.getServer().getOnlinePlayers()){
                    ArrayList<Cops> recherche = Cops.cobsSeekPlayerReel(player); //If hash map vide => Player n'est plus recherché || BUG when Police spawn, they don't have a Target
                    PlayerStar playerStar = PlayerStar.playerDataFromPlayer(player);
                    assert playerStar != null;
                    double star = playerStar.getStar();

                    if(star != 0){   //If player is recherché
                        if(star % 1 == 0){  //Star Full
                            if(recherche.size() == 0){ //If player n'est plus recherché
                                playerStar.endStar();

                                //Changé le titre le la boss Bar
                                //Changé de boss bar
                            }

                        } else {  //Star end
                            if(recherche.size() != 0){  //player is find by cop
                                playerStar.endStarCancel();
                            }
                        }
                    }

                    if(star != 0 && star % 1 != 0){ //If star != 0 and star is .. .5
                        if(playerStar.lastTimeChange + 10*1000 < System.currentTimeMillis()){ //If 10 de Star End
                            playerStar.setStar(0);
                            player.sendMessage("Vous avez perdu vos étoiles");
                        }
                    }
                }
            }
        }.runTaskTimer(CopsPlugin.getInstance(), 40, 20);
    }





}
