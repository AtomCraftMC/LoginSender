package me.deadlight.loginsender;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public final class LoginSender extends JavaPlugin {

    private static LoginSender plugin;
    public static LoginSender getInstance() {
        return plugin;
    }
    public static JedisPool pool;
    public static Jedis rJedis;
    public static FileConfiguration config;


    @Override
    public void onEnable() {
        plugin = this;
        config = getConfig();
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        pool = new JedisPool("127.0.0.1", 6379);
        getServer().getPluginManager().registerEvents(new LoginListener(), this);
        Jedis j = null;
        try {
            j = pool.getResource();
            // If you want to use a password, use
            j.auth("piazcraftmc");
            rJedis = j;
        } finally {
            // Be sure to close it! It can and will cause memory leaks.
            j.close();
        }
        System.out.println("Login Sender initialized. <----------");
        // Plugin startup logic

    }

    @Override
    public void onDisable() {
        pool.close();
        // Plugin shutdown logic
    }
}
