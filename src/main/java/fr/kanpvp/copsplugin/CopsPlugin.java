package fr.kanpvp.copsplugin;

import fr.kanpvp.copsplugin.cops.Cops;
import fr.kanpvp.copsplugin.listeners.EntityCopsEvent;
import fr.kanpvp.copsplugin.utlis.VectorCal;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class CopsPlugin extends JavaPlugin {

    public static CopsPlugin instance;
    public static VectorCal vectorCal;

    @Override
    public void onEnable() {
        instance = this;
        vectorCal = new VectorCal();

        System.out.println(ChatColor.GREEN + "The plugin CopsPlugin is on Enables");

        Bukkit.getServer().getPluginManager().registerEvents(new EntityCopsEvent(), this);
        Cops.copsIA();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


    public static CopsPlugin getInstance(){
        return instance;
    }
    public static VectorCal getVectorCal(){
        return vectorCal;
    }

}
