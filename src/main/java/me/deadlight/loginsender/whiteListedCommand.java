package me.deadlight.loginsender;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;

public class whiteListedCommand implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (LoginSender.verifiedPlayers.contains(args[0])) {
            System.out.println("yes it is verified");
        } else {
            System.out.println("no it is not verified");
        }

        return false;
    }
}
