package fr.kanpvp.copsplugin.listeners;

import fr.kanpvp.copsplugin.PlayerStar;
import fr.kanpvp.copsplugin.utlis.Bar;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerEventJ implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();

        if(!PlayerStar.pDataList.containsKey(player.getUniqueId())){

            new PlayerStar(player);
        }

        BossBar bar = Bukkit.createBossBar(" ", BarColor.PINK, BarStyle.SOLID);
        bar.addPlayer(player);
        Bar.dataPlayerBar.put(player, bar);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event){
        Player player = event.getPlayer();

        PlayerStar.pDataList.remove(player.getUniqueId());
        //Bar.dataPlayerBar.remove(player);

    }

}
