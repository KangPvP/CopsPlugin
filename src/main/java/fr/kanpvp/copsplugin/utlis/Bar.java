package fr.kanpvp.copsplugin.utlis;

import fr.kanpvp.copsplugin.CopsPlugin;
import fr.kanpvp.copsplugin.PlayerStar;
import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class Bar {

    //HashMap<Integer, BossBar> listBossBar = createBossBarData();

    public static HashMap<Player, BossBar> dataPlayerBar = new HashMap<>();

    public Bar(){
        barActus();
    }


    /*public BossBar getBar(int idBar) {
        return listBossBar.get(idBar);
    }

    public HashMap<Integer, BossBar> createBossBarData(){
        HashMap<Integer, BossBar> map = new HashMap<Integer, BossBar>();

        for(int i = 0; i < 10; i++){
            map.put(i, Bukkit.createBossBar("star" + i, BarColor.BLUE, BarStyle.SOLID));
        }

        return map;
    }*/




    public void barActus(){

        new BukkitRunnable(){

            @Override
            public void run() {
                for(Player player : Bukkit.getServer().getOnlinePlayers()){
                    BossBar bar = Bar.dataPlayerBar.get(player);
                    PlayerStar playerStar = PlayerStar.playerDataFromPlayer(player);
                    assert playerStar != null;
                    double star = playerStar.getStar();

                    if(!bar.getTitle().equals("star" + star)){
                        bar.setTitle("star" + star);
                    }
                }
            }
        }.runTaskTimer(CopsPlugin.getInstance(), 40, 20);
    }


}
