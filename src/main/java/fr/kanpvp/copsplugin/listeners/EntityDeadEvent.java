package fr.kanpvp.copsplugin.listeners;

import fr.kanpvp.copsplugin.CopsPlugin;
import fr.kanpvp.copsplugin.PlayerStar;
import fr.kanpvp.copsplugin.cops.Cops;
import fr.kanpvp.copsplugin.utlis.randomdraw.ManagerDraw;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class EntityDeadEvent implements Listener {

    @EventHandler
    public void onPlayerDead(PlayerDeathEvent event){
        Player player = event.getEntity();


        //Reset Recherche Cop + Star
        ArrayList<Cops> copsSeek = Cops.cobsSeekPlayer(player);

        for (Cops cop : copsSeek){
            cop.setTarget(null);
            cop.entityCop.setTarget(null);
        }

        //PlayerStar.playerDataFromPlayer(player).setStar(0); //Reset Star


        //Spawn Section Cop
        Player killer = player.getKiller();
        if(killer != null) {
            //boolean stats = new ManagerDraw().getRandomBoolean(30);
            //if(stats){

                spawnCopsSection(killer, killer.getLocation());
            //}


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
            event.setDroppedExp(0);
            event.getDrops().clear();

            Cops cop = Cops.copsList.get(entity.getUniqueId());
            ItemStack itemLoot = new ManagerDraw().getRandomItem(cop.copsRole.loots);
            entity.getLocation().getWorld().dropItem(entity.getLocation(), itemLoot);

            if(killer != null && killer instanceof Player){
                boolean stats = new ManagerDraw().getRandomBoolean(30);
                if(stats){
                    killer.getInventory().addItem(new ItemStack(Material.ACACIA_TRAPDOOR));

                    //Star + 1
                    PlayerStar.playerDataFromPlayer(killer).addStar();
                    killer.sendMessage("Vous avez " + PlayerStar.playerDataFromPlayer(killer).getStar());
                    //Spawn Section Cops
                    spawnCopsSection(killer, entity.getLocation());
                }
            }
            Cops.copsList.remove(entity.getUniqueId());
        }
    }

    public void spawnCopsSection(Player target, Location loc){
        UUID idSection = UUID.randomUUID();

        //PlayerStar playerStar = PlayerStar.playerDataFromPlayer(target);
        //assert playerStar != null;
        double star = PlayerStar.playerDataFromPlayer(target).getStar();



        for(Cops.CopsRole role : Cops.selectCopsGroup(1)){
            Cops cop = new Cops(role, target, idSection, loc); //Spawn Cops
            cop.entityCop.setInvisible(true);
            cop.entityCop.setGlowing(true);

            Vector ejectVec = getLookDirection(target);
            cop.entityCop.setVelocity(ejectVec.multiply(3 + new Random().nextInt(1))); //Ejection
        }

        Bukkit.getScheduler().runTaskLater(CopsPlugin.getInstance(), new Runnable(){
            final UUID section = idSection;
            @Override
            public void run() {
                Location loc = new Location(Bukkit.getWorld("world"), 0,0,0);
                for(Cops cop : Cops.getCopsSection(section)){
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
