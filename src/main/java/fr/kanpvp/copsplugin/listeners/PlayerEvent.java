package fr.kanpvp.copsplugin.listeners;

import fr.kanpvp.copsplugin.PlayerStar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerEvent implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();

        if(!PlayerStar.pDataList.containsKey(player.getUniqueId())){
            new PlayerStar(player);
        }

    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event){
        Player player = event.getPlayer();

        //Supprésion du player de la list + enregistrement de ces etoile sur sa data Persistantes s
        if(PlayerStar.pDataList.containsKey(player.getUniqueId())){
            PlayerStar.pDataList.get(player.getUniqueId()).setDataStar();
            PlayerStar.pDataList.remove(player.getUniqueId());
        }

    }

}
