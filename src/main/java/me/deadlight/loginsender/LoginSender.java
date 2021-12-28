package me.deadlight.loginsender;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class LoginSender extends JavaPlugin {

    private static LoginSender plugin;
    public static LoginSender getInstance() {
        return plugin;
    }
    public static JedisPool pool;
    public static Jedis rJedis;
    public static FileConfiguration config;
    public static ConcurrentHashMap<UUID, Integer> playerCheck = new ConcurrentHashMap<>();
    public static List<String> verifiedPlayers = new ArrayList<>();
    public static int blockedNumber = 0;
    public static List<String> blockedUsernames = new ArrayList<>();

    @Override
    public void onEnable() {
        plugin = this;
        saveDefaultConfig();
        config = getConfig();
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getServer().getPluginCommand("blockednumber").setExecutor(new blockedCommand());
        getServer().getPluginCommand("whitelisted").setExecutor(new whiteListedCommand());
        pool = new JedisPool("127.0.0.1", 6379);
        getServer().getPluginManager().registerEvents(new LoginListener(), this);

        Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            @Override
            public void run() {
                for (UUID pUUID : playerCheck.keySet()) {
                    if (playerCheck.get(pUUID) + 1 == 10) {
                        playerCheck.remove(pUUID);
                        Player player = Bukkit.getPlayer(pUUID);
                        if (player == null) continue;
                        kickPlayer(player);
                        blockedNumber = blockedNumber + 1;
                        if (!blockedUsernames.contains(player.getName())) {
                            blockedUsernames.add(player.getName());
                        }
                    } else {
                        playerCheck.put(pUUID, playerCheck.get(pUUID) + 1);
                    }
                }
            }
        }, 0, 20);
        Jedis j = null;
        try {
            j = pool.getResource();
            // If you want to use a password, use
            j.auth("piazcraftmc");
            rJedis = j;
            List<String> servers = config.getStringList("servers");
            StringBuilder finalList = new StringBuilder();
            boolean started = false;
            for (String server : servers) {
                if (!started) {
                    started = true;
                    finalList = new StringBuilder(server);
                } else {
                    finalList.append(":").append(server);
                }
            }

            j.set("lobbylist", finalList.toString());
        } finally {
            // Be sure to close it! It can and will cause memory leaks.
            j.close();
        }
        System.out.println("Login Sender initialized. <----------");
        // Plugin startup logic

    }

    public void kickPlayer(Player player) {
        if (player.isOnline()) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                @Override
                public void run() {
                    player.kickPlayer(LoginListener.colorify("&cSorry :(, Couldn't verify you T_T, Try again..."));
                }
            }, 0);

        }
    }

    @Override
    public void onDisable() {
        pool.close();
        // Plugin shutdown logic
    }
}
