package fr.kanpvp.copsplugin.utlis;

import fr.kanpvp.copsplugin.PlayerStar;
import fr.kanpvp.copsplugin.cops.Cops;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class Bar {



    public void changeBar(int idBar) {

    }

    public HashMap<Integer, BossBar> createBossBarData(){
        HashMap<Integer, BossBar> map = new HashMap<Integer, BossBar>();

        for(int i = 0; i < 10; i++){
            map.put(i, Bukkit.createBossBar("star" + i, BarColor.BLUE, BarStyle.SOLID));
        }

        return map;
    }

    public void testReseek(Player player){

        //Ajoute d'une boucle pour chaque joueurs

        ArrayList<Cops> recherche = Cops.cobsSeekPlayerReel(player); //If hash map vide => Player n'est plus recherché

        int star = PlayerStar.playerDataFromPlayer(player).getStar();

        if(star != 0 ){ //If player is recherché
            if(recherche.size() == 0){ //If player n'est plus recherché
                //Changé le titre le la boss Bar
                //Changé de boss bar
                // bar.setTitle("star " + star+0.1)
            }
        }


    }


}
