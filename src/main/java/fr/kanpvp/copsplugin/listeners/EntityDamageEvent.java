package fr.kanpvp.copsplugin.listeners;

import fr.kanpvp.copsplugin.cops.Cops;
import me.deecaad.weaponmechanics.weapon.weaponevents.WeaponDamageEntityEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityDamageEvent implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event){
        Entity entity = event.getEntity();
        Entity damager = event.getDamager();
        if(damager instanceof Player){


            //Si l'entity is a Cops
            if(Cops.copsList.containsKey(entity.getUniqueId())){
                Cops cop = Cops.copsList.get(entity.getUniqueId());
                if(cop.entityCop.isInvisible()){
                    event.setCancelled(true);
                } else {
                    //Si le cop n'est pas occupé
                    if(cop.entityCop.getTarget() == null){
                        cop.setTarget((Player) damager);
                    }
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
    public void onDamageWeapon(WeaponDamageEntityEvent event){
        Entity shooter = event.getShooter();
        Entity victim = event.getVictim();

        // If player Shoot a Cops Invisible
        if(shooter instanceof Player){
            if(Cops.copsList.containsKey(victim.getUniqueId())){
                if(Cops.copsList.get(victim.getUniqueId()).entityCop.isInvisible()){
                    event.setCancelled(true);
                }
            }
        }


        //Check if a cop Shoot an other cop
        if(Cops.copsList.containsKey(shooter.getUniqueId())){
            if(Cops.copsList.containsKey(victim.getUniqueId())){
                event.setCancelled(true);
            }
        }
    }

}
