package fr.kanpvp.copsplugin;

import fr.kanpvp.copsplugin.cops.Cops;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class CopsPlugin extends JavaPlugin {

    public static CopsPlugin instance;

    @Override
    public void onEnable() {
        instance = this;

        System.out.println(ChatColor.GREEN + "The plugin CopsPlugin is onEnables");

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

}
