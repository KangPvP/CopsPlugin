package fr.kanpvp.copsplugin.listeners;

import fr.kanpvp.copsplugin.cops.Cops;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;

import java.util.UUID;

public class EntityCopsEvent implements Listener {

    @EventHandler
    public void onPlace(BlockPlaceEvent event){
        new Cops(Cops.CopsRole.SWATT, (Player) event.getPlayer(), UUID.randomUUID(), new Location(Bukkit.getWorld("world"),5,100,5));
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.NATURAL)) event.setCancelled(true);
    }

    @EventHandler
    public void onSpawn(EntitySpawnEvent event) {
        Entity entity = event.getEntity();

        if(entity.getType().equals(EntityType.ZOMBIE) && entity.getType().equals(EntityType.PILLAGER)){

        }
    }

    @EventHandler
    public void onBurn(EntityCombustEvent event){
        if(Cops.copsList.containsKey(event.getEntity().getUniqueId())){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onTarget(EntityTargetLivingEntityEvent event){

        Entity entity = event.getEntity();

        if(Cops.copsList.containsKey(entity.getUniqueId())){
            if(event.getReason().equals(EntityTargetEvent.TargetReason.CLOSEST_PLAYER)){ //Attaque le joueur le plus proche si target NULL
                event.setCancelled(true);
            } else if(event.getReason().equals(EntityTargetEvent.TargetReason.COLLISION)){ //Collision Nouveau calcul du Target
                event.setCancelled(true);
            }
        }
    }




}
