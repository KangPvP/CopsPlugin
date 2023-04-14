package fr.kanpvp.copsplugin;

import fr.kanpvp.copsplugin.cops.Cops;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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

        Player killer = player.getKiller();

        if(killer != null){
            int star = 1;

            PlayerData pData = PlayerData.playerDataFromPlayer(killer);

            assert pData != null;


            UUID idSection = UUID.randomUUID();

            Location locPlayer = killer.getLocation();

            for(Cops.CopsRole role : Cops.selectCopsGroup(star)){
                Cops cop = new Cops(role, killer, idSection, locPlayer);
                cop.entityCop.setInvisible(true);

                Vector ejectVec = getLookDirection(killer);
                cop.entityCop.setVelocity(ejectVec.multiply(3 + new Random().nextInt(1)));

                Bukkit.getScheduler().runTaskLater(CopsPlugin.getInstance(), new Runnable(){
                    @Override
                    public void run() {
                        cop.entityCop.setInvisible(false);
                        Cops.entityEquipement(cop.entityCop, cop.equipement);
                    }
                }, 20);

            }



        }
    }
    public org.bukkit.util.Vector getLookDirection(Player player) {
        double yaw = Math.toRadians(player.getLocation().getYaw() + (new Random().nextDouble(5-(-5)) + (-5)) );

        double x = -Math.sin(yaw);
        double z = Math.cos(yaw);

        return new org.bukkit.util.Vector(x, 0,z).normalize();
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
