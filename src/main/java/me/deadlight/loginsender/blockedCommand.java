package me.deadlight.loginsender;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class blockedCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        System.out.println(LoginSender.blockedNumber);
        System.out.println(LoginSender.blockedUsernames);

        return false;
    }
}
