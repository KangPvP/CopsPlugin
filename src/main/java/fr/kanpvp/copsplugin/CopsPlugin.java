package fr.kanpvp.copsplugin;

import fr.kanpvp.copsplugin.commands.CmdOffCop;
import fr.kanpvp.copsplugin.cops.Cops;
import fr.kanpvp.copsplugin.listeners.EntityCopsEvent;
import fr.kanpvp.copsplugin.listeners.EntityDamageEvent;
import fr.kanpvp.copsplugin.listeners.EntityDeadEvent;
import fr.kanpvp.copsplugin.listeners.PlayerEventJ;
import fr.kanpvp.copsplugin.utlis.Bar;
import fr.kanpvp.copsplugin.utlis.VectorCal;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class CopsPlugin extends JavaPlugin {
    public FileConfiguration config = getConfig();
    public static CopsPlugin instance;
    public static VectorCal vectorCal;

    @Override
    public void onEnable() {
        instance = this;
        vectorCal = new VectorCal();

        saveDefaultConfig();
        config.addDefault("langue", true);
        config.options().copyDefaults(true);
        saveConfig();

        PlayerStar.starActus();
        new Bar();

        System.out.println(ChatColor.GREEN + "The plugin CopsPlugin is on Enables");

        Bukkit.getPluginCommand("offcop").setExecutor(new CmdOffCop());

        Bukkit.getServer().getPluginManager().registerEvents(new PlayerEventJ(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new EntityCopsEvent(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new EntityDamageEvent(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new EntityDeadEvent(), this);
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
