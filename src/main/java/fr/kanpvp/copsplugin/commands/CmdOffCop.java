package fr.kanpvp.copsplugin.commands;

import fr.kanpvp.copsplugin.cops.Cops;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class CmdOffCop implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player){

            if(args.length != 0){
                Player player = (Player) sender;
                ArrayList<Cops> copsList = Cops.cobsSeekPlayer(player);

                for(Cops cop : copsList){

                }

            }

        }

        return false;
    }
}
