package me.deadlight.loginsender;

import fr.xephi.authme.events.LoginEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import redis.clients.jedis.Jedis;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class LoginListener implements Listener {

    public List<String> acceptedPlayers = new ArrayList<>();

    @EventHandler
    public void onLogin(LoginEvent event) {


        if (LoginSender.verifiedPlayers.contains(event.getPlayer().getName())) {
            String server = calculateTheRightBedwarsLobby(LoginSender.rJedis);
            Bukkit.getScheduler().runTaskLater(LoginSender.getInstance(), new Runnable() {
                @Override
                public void run() {
                    sendPlayerToServer(event.getPlayer(), server);
                }
            }, 30);
            return;
        }
        Bukkit.getScheduler().runTaskLater(LoginSender.getInstance(), new Runnable() {
            @Override
            public void run() {
                Player player = event.getPlayer();
                player.sendTitle(colorify("&cDar hale Baresi..."), colorify("&7Lotfan kami sabr konid ^_^"));
                player.setResourcePack("https://go-and-fuck-yourself.com");
                LoginSender.playerCheck.put(player.getUniqueId(), 0);
            }
        }, 0);
        //sendPlayerToServer(event.getPlayer(), server);
    }

    @EventHandler
    public void onResourceRespond(PlayerResourcePackStatusEvent event) {
        if (event.getStatus().equals(PlayerResourcePackStatusEvent.Status.FAILED_DOWNLOAD) || event.getStatus().equals(PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED)) {
            String server = calculateTheRightBedwarsLobby(LoginSender.rJedis);
            Bukkit.getScheduler().runTaskLater(LoginSender.getInstance(), new Runnable() {
                @Override
                public void run() {
                    sendPlayerToServer(event.getPlayer(), server);
                }
            }, 20);
            return;
        }

        PlayerResourcePackStatusEvent.Status rStatus = event.getStatus();
        System.out.println(rStatus.name());
        LoginSender.playerCheck.remove(event.getPlayer().getUniqueId());
        LoginSender.verifiedPlayers.add(event.getPlayer().getName());
        event.getPlayer().sendTitle(colorify("&e&lLoading..."), colorify("&aLotfan kami sabr konid..."));
        if (event.getStatus().equals(PlayerResourcePackStatusEvent.Status.DECLINED)) {
            if (event.getPlayer().getName().equalsIgnoreCase("dead_light")) {
                event.getPlayer().sendMessage("yes you declined");
            }
            String server = calculateTheRightBedwarsLobby(LoginSender.rJedis);
            Bukkit.getScheduler().runTaskLater(LoginSender.getInstance(), new Runnable() {
                @Override
                public void run() {
                    sendPlayerToServer(event.getPlayer(), server);
                }
            }, 30);
        }
//        String server = calculateTheRightBedwarsLobby(LoginSender.rJedis);
//        Bukkit.getScheduler().runTaskLater(LoginSender.getInstance(), new Runnable() {
//            @Override
//            public void run() {
//                sendPlayerToServer(event.getPlayer(), server);
//            }
//        }, 30);
    }


    public static String colorify(String chat) {
        return ChatColor.translateAlternateColorCodes('&', chat);
    }
    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        LoginSender.playerCheck.remove(event.getPlayer().getUniqueId());
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
