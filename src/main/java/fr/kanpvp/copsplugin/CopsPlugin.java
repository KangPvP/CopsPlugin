package fr.kanpvp.copsplugin;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class CopsPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        System.out.println(ChatColor.GREEN + "The plugin CopsPlugin is onEnables");

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
