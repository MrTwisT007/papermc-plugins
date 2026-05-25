package me.bunnycat.warpsgui;

import de.bluecolored.bluemap.api.BlueMapAPI;
import me.bunnycat.warpsgui.Commands.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStream;

public class Warps extends JavaPlugin {
    public static Warps plugin;
    public static BlueMapAPI blueMap;
    private Utils utils = new Utils();

    public void onEnable() {
        super.onEnable();
        plugin = this;
        new me.bunnycat.warpsgui.Listeners.InventoryEvent(plugin);
        new me.bunnycat.warpsgui.Listeners.WatchClickEvent(plugin);
        new me.bunnycat.warpsgui.Listeners.JoinEvent(plugin);
        getCommand("menu").setExecutor(new menu());
        getCommand("setspawn").setExecutor(new setspawn());
        getCommand("spawn").setExecutor(new spawn());
        getCommand("setwarp").setExecutor(new setwarp());
        getCommand("warp").setExecutor(new warp());
        getCommand("sethome").setExecutor(new sethome());
        getCommand("home").setExecutor(new home());
        getCommand("removehome").setExecutor(new removehome());
        getCommand("removewarp").setExecutor(new removewarp());
        //MetricsLite metrics = new MetricsLite(this);
        //System.out.println("[WarpsGui--] Plugin has been activated!");
        config();
        if (getConfig().getBoolean("BluemapIntegration.enabled")) {
            BlueMapAPI.onEnable(blueMapAPI -> {
                blueMap = blueMapAPI;
                utils.loadImages();
                Utils.addMarkers();
            });
        }
    }

    public void onDisable() {
        //System.out.println("[WarpsGui--] Plugin has been Disabled!");
    }

    private void config() {
        reloadConfig();
        getConfig().addDefault("warps", "");
        getConfig().addDefault("homes", "");
        getConfig().options().copyDefaults(true);
        saveConfig();
    }
}