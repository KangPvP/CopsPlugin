package fr.kanpvp.copsplugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdSpawnCop implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(args.length == 5){
            World world = Bukkit.getWorld(args[0]);
            double x = tryDoubleParse(args[1]);
            double y = tryDoubleParse(args[2]);
            double z = tryDoubleParse(args[3]);
            int type = tryIntParse(args[4]);

            if(world != null && x != -1.234 && y != -1.234 && z != -1.234 && type != -1){
                Location loc = new Location(world, x,y,z);

                for(Player player : Bukkit.getOnlinePlayers()){

                }

                //spawnCopsSection(killer, loc);


            }




        } else {
            System.out.println("/copsspawnset [world] [x] [y] [z] [type 1]");
        }
        return false;
    }

    public Integer tryIntParse(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public Double tryDoubleParse(String text) {
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return -1.234;
        }
    }

}
