package me.bunnycat.twoolio;

import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

enum Optype {
    INCREMENT,
    DECREMENT,
    NONE,
    SET,
    RESET
}

public class Twoolio extends JavaPlugin implements Listener {
    public static Plugin plugin;
    Logger myPluginLogger = Bukkit.getLogger();
    TwoolioEventListener handlers;

    private static ConfigManager configManager;

    public static ConfigManager getConfigManager() {
        return Twoolio.configManager;
    }

    public void onEnable() {
        Twoolio.plugin = this;
        Twoolio.configManager = new ConfigManager(this);
        getCommand("twoolioupdate").setExecutor(new Commands());
        getCommand("settoolupgrades").setExecutor(new Commands());
        getCommand("settoolxp").setExecutor(new Commands());
        getCommand("settoollvl").setExecutor(new Commands());
        myPluginLogger.info("TWOOLIO Started");
        handlers = new TwoolioEventListener();
        getServer().getPluginManager().registerEvents(this.handlers, this);
    }

}