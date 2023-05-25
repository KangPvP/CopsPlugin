package fr.kanpvp.copsplugin.commands;

import fr.kanpvp.copsplugin.cops.Cops;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class CmdOffCop implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player){
            Player player = (Player) sender;
            if(!player.hasPermission("perm.copsclear")){
                return false;
            }
            if(args.length == 0){

                ArrayList<Cops> copsList = Cops.cobsSeekPlayer(player);

                for(Cops cop : copsList){
                    cop.entityCop.setTarget(null);
                    cop.setTarget(null);
                }
            } else {
                Player player1 = Bukkit.getPlayer(args[0]);

                if(player1 != null){
                    ArrayList<Cops> copsList = Cops.cobsSeekPlayer(player1);

                    for(Cops cop : copsList){
                        cop.entityCop.setTarget(null);
                        cop.setTarget(null);
                    }
                }

            }

        }

        return false;
    }
}
