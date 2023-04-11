package fr.kanpvp.copsplugin;

import fr.kanpvp.copsplugin.cops.Cops;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;

public class EntityCopsEvent implements Listener {

    @EventHandler
    public void onPlace(BlockPlaceEvent event){
        new Cops(Cops.CopsRole.SWATT, (Player) event.getPlayer());
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.NATURAL)) event.setCancelled(true);
    }
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event){
        Entity entity = event.getEntity();
        Entity damager = event.getDamager();
        if(damager instanceof Player){
            //Si l'entity is a Cops
            if(Cops.copsList.containsKey(entity.getUniqueId())){
                Cops cop = Cops.copsList.get(entity.getUniqueId());
                //Si le cop n'est pas occupé
                if(cop.entityCop.getTarget() == null){
                    cop.setTarget((Player) damager);
                }
            }

            //Si un joueurs Frappe un autre joueur les cops Au alentour interviennent
            if(entity instanceof Pig){
                damager.sendMessage("Pig was hit");

                //Si il y a des Cops a moins de 50 blocks du Joueurs
                for(Entity entity1 : damager.getNearbyEntities(50,50,50)){

                    if(Cops.copsList.containsKey(entity1.getUniqueId())){
                        Cops cop = Cops.copsList.get(entity1.getUniqueId());
                        //Si le cop n'est pas occupé
                        if(cop.entityCop.getTarget() == null){
                            cop.setTarget((Player) damager);
                            cop.entityCop.setTarget((LivingEntity) damager);

                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDead(PlayerDeathEvent event){
        Player player = event.getEntity();

        ArrayList<Cops> copsSeek = Cops.cobsSeekPlayer(player);

        for (Cops cop : copsSeek){
            cop.setTarget(null);
            cop.entityCop.setTarget(null);
        }

    }



    @EventHandler
    public void onSpawn(EntitySpawnEvent event) {
        Entity entity = event.getEntity();

        if(entity.getType().equals(EntityType.ZOMBIE) && entity.getType().equals(EntityType.PILLAGER)){

        }
    }
    @EventHandler
    public void onDamage(EntityDamageEvent event){

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
