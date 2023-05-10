package fr.kanpvp.copsplugin.utlis;

import fr.kanpvp.copsplugin.CopsPlugin;
import fr.kanpvp.copsplugin.PlayerStar;
import fr.kanpvp.copsplugin.cops.Cops;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;

public class Bar {

    HashMap<Integer, BossBar> listBossBar = createBossBarData();

    public static HashMap<Player, BossBar> dataPlayerBar = new HashMap<>();

    public Bar(){
        testReseek();
    }


    public BossBar getBar(int idBar) {
        return listBossBar.get(idBar);
    }

    public HashMap<Integer, BossBar> createBossBarData(){
        HashMap<Integer, BossBar> map = new HashMap<Integer, BossBar>();

        for(int i = 0; i < 10; i++){
            map.put(i, Bukkit.createBossBar("star" + i, BarColor.BLUE, BarStyle.SOLID));
        }

        return map;
    }




    public void testReseek(){

        new BukkitRunnable(){

            @Override
            public void run() {
                for(Player player : Bukkit.getServer().getOnlinePlayers()){
                    ArrayList<Cops> recherche = Cops.cobsSeekPlayerReel(player); //If hash map vide => Player n'est plus recherché
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
                        }
                    }
                }
            }
        }.runTaskTimer(CopsPlugin.getInstance(), 40, 20);
    }


}
