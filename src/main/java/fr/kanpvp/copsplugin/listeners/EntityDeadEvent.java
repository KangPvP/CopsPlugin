package fr.kanpvp.copsplugin.listeners;

import fr.kanpvp.copsplugin.CopsPlugin;
import fr.kanpvp.copsplugin.PlayerStar;
import fr.kanpvp.copsplugin.cops.Cops;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class EntityDeadEvent implements Listener {

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

            PlayerStar pData = PlayerStar.playerDataFromPlayer(killer);

            assert pData != null;

            UUID idSection = UUID.randomUUID();

            Location locPlayer = killer.getLocation();

            for(Cops.CopsRole role : Cops.selectCopsGroup(star)){
                Cops cop = new Cops(role, killer, idSection, locPlayer);
                cop.entityCop.setInvisible(true);
                cop.entityCop.setGlowing(true);

                Vector ejectVec = getLookDirection(killer);
                cop.entityCop.setVelocity(ejectVec.multiply(3 + new Random().nextInt(1)));

            }

            Cops.getCopsSection(idSection);
            Bukkit.getScheduler().runTaskLater(CopsPlugin.getInstance(), new Runnable(){
                @Override
                public void run() {
                    Location loc = new Location(Bukkit.getWorld("world"), 0,0,0);
                    for(Cops cop : Cops.getCopsSection(idSection)){
                        cop.entityCop.setInvisible(false);

                        cop.entityCop.setTarget(cop.target);

                        Cops.entityEquipement(cop.entityCop, cop.equipement);

                        loc = cop.entityCop.getLocation();
                    }
                    Bukkit.getWorld("world").playSound(loc, Sound.ITEM_TOTEM_USE, 1F, 1F);
                }
            }, 20);
        }
    }
    public org.bukkit.util.Vector getLookDirection(Player player) {
        double yaw = Math.toRadians(player.getLocation().getYaw() + (new Random().nextDouble() + (-5)) );

        double x = -Math.sin(yaw);
        double z = Math.cos(yaw);

        return new org.bukkit.util.Vector(x, 0,z).normalize();
    }

    @EventHandler
    public void onEntityDead(EntityDeathEvent event){
        LivingEntity entity = event.getEntity();
        Player killer = entity.getKiller();

        if(Cops.copsList.containsKey(entity.getUniqueId())){
            Cops.copsList.remove(entity.getUniqueId());

            if(killer != null){

            }


        }
    }


}
