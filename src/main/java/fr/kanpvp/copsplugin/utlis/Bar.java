package fr.kanpvp.copsplugin.utlis;

import fr.kanpvp.copsplugin.CopsPlugin;
import fr.kanpvp.copsplugin.PlayerStar;
import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class Bar {

    //HashMap<Integer, BossBar> listBossBar = createBossBarData();

    public HashMap<String, String> referenceBar = referenceBar();

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




    public void barActus() {
        new BukkitRunnable(){

            @Override
            public void run() {
                for(Player player : Bukkit.getServer().getOnlinePlayers()){
                    BossBar bar = Bar.dataPlayerBar.get(player);

                    PlayerStar playerStar = PlayerStar.playerDataFromPlayer(player);
                    assert playerStar != null;
                    double star = playerStar.getStar();

                    System.out.println(star);

                    String barName = referenceBar.get(bar.getTitle());
                    if(barName.equals(null)){
                        bar.getTitle();
                    }
                    boolean endsWithPoint4 = barName.endsWith(".4");

                    if(star != 0){   //If player is recherché
                        if(star % 1 != 0){  // ! Star Full

                            if(endsWithPoint4){ //if star finish by .4
                                String starTitle = " ";

                                for (Map.Entry<String, String> entry : referenceBar.entrySet()) {
                                    if (entry.getValue().equals("star" + star)) {
                                        starTitle = entry.getKey();
                                    }
                                }

                                bar.setTitle(starTitle);
                            } else { //if star finish by .5

                                String starTitle = " ";

                                for (Map.Entry<String, String> entry : referenceBar.entrySet()) {
                                    if (entry.getValue().equals("star" + (star - 0.1) )) {
                                        starTitle = entry.getKey();
                                    }
                                }

                                bar.setTitle(starTitle);
                            }
                        }
                    }

                    if(!endsWithPoint4){ //if star OFF don't change
                        if(!barName.equals("star" + star)){
                            String starTitle = " ";

                            for (Map.Entry<String, String> entry : referenceBar.entrySet()) {
                                if (entry.getValue().equals("star" + star)) {
                                    starTitle = entry.getKey();
                                }
                            }

                            bar.setTitle(starTitle);
                        }
                    }

                    //Rectification Bug
                    if(star == 0){
                        if(!barName.equals("star" + star)){
                            String starTitle = " ";

                            for (Map.Entry<String, String> entry : referenceBar.entrySet()) {
                                if (entry.getValue().equals("star" + star)) {
                                    starTitle = entry.getKey();
                                }
                            }

                            bar.setTitle(starTitle);
                        }
                    }



                }
            }
        }.runTaskTimer(CopsPlugin.getInstance(), 40, 20);
    }

    public HashMap<String, String> referenceBar(){
        HashMap<String, String> barNameMap = new HashMap<>();

        barNameMap.put(" ", "star0.0");
        barNameMap.put("aꑕꑕꑕꑕꑕ", "star1.0");
        barNameMap.put("bꑕꑕꑕꑕꑕ", "star1.4");
        barNameMap.put("bꑕꑕꑕꑕꑕ ", "star1.5");
        barNameMap.put("ccꑕꑕꑕꑕ", "star2.0");
        barNameMap.put("ddꑕꑕꑕꑕ", "star2.4");
        barNameMap.put("ddꑕꑕꑕꑕ ", "star2.5");
        barNameMap.put("e", "star3.0");
        barNameMap.put("f", "star3.4");
        barNameMap.put("fh", "star3.5");
        barNameMap.put("g", "star4.0");
        barNameMap.put("h", "star4.4");
        barNameMap.put("hh", "star4.5");
        barNameMap.put("i", "star5.0");
        barNameMap.put("j", "star5.4");
        barNameMap.put("jh", "star5.5");

        return barNameMap;
    }


}
