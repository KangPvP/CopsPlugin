package fr.kanpvp.copsplugin.commands;

import fr.kanpvp.copsplugin.PlayerStar;
import fr.kanpvp.copsplugin.cops.Cops;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdSetStars implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(args.length == 2){
            Player target = Bukkit.getPlayer(args[0]);

            if(target == null){
                return false;
            }

            if(!isNumeric(args[1]) || Integer.parseInt(args[1]) < 1 || Integer.parseInt(args[1]) > 5){
                return false;
            }

            int star = Integer.parseInt(args[1]);

            PlayerStar playerStar = PlayerStar.playerDataFromPlayer(target);

            assert playerStar != null;
            playerStar.setStar(star);

            Cops.spawnCopsSection(target, target.getLocation());
        }

        return false;
    }

    public static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

}
