package me.deadlight.loginsender;

import fr.xephi.authme.events.LoginEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import redis.clients.jedis.Jedis;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public class LoginListener implements Listener {

    @EventHandler
    public void onLogin(LoginEvent event) {
        String server = calculateTheRightBedwarsLobby(LoginSender.rJedis);
        sendPlayerToServer(event.getPlayer(), server);
    }


    public static String calculateTheRightBedwarsLobby(Jedis jedis) {

        String result1 = jedis.get("count-blobby1");
        String result2 = jedis.get("count-blobby2");

        if (result1 != null || result2 != null) {

            if (result1 == null) {
                return "blobby2";
            }
            if (result2 == null) {
                return "blobby1";
            }
            int count1 = Integer.parseInt(result1);
            int count2 = Integer.parseInt(result2);
            if (count1 == count2) {
                return "blobby1";
            }

            if (count1 > count2) {
                return "blobby2";
            } else {
                return "blobby1";
            }

        } else {
            return "auth";
        }

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
