package me.deadlight.loginsender;

import fr.xephi.authme.events.LoginEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import redis.clients.jedis.Jedis;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class LoginListener implements Listener {

    @EventHandler
    public void onLogin(LoginEvent event) {
        String server = calculateTheRightBedwarsLobby(LoginSender.rJedis);
        sendPlayerToServer(event.getPlayer(), server);
    }


    public static String calculateTheRightBedwarsLobby(Jedis jedis) {

        List<String> lobbyServers = LoginSender.config.getStringList("servers");
        List<ServerObject> servers = new ArrayList<>();
        for (String server : lobbyServers) {
            if (jedis.get("count-" + server) == null) {
                continue;
            }
            int count = Integer.parseInt(jedis.get("count-" + server));
            ServerObject object = new ServerObject(server, count);
            servers.add(object);
        }
        Collections.sort(servers);
        Collections.reverse(servers);

        if (servers.size() == 0) {
            return "auth";
        }

        return servers.get(0).name;


    }



    public static void sendPlayerToServer(Player player, String server) {
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            out.writeUTF("Connect");
            out.writeUTF(server);
            player.sendPluginMessage(LoginSender.getInstance(), "BungeeCord", b.toByteArray());
            b.close();
            out.close();
        }
        catch (Exception e) {
            player.sendMessage(ChatColor.RED+"Error when trying to connect to "+server);
        }
    }


}
