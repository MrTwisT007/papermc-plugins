package me.bunnycat.xpmodifier;
import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.logging.Logger;

//public class PenaltyModifier extends JavaPlugin {
	// Fired when plugin is first enabled
//    @Override
//    public void onEnable() {
//    }
    // Fired when plugin is disabled
//    @Override
//    public void onDisable() {

//    }
//}

 
public class XPModifier extends JavaPlugin implements Listener {
    Logger myPluginLogger = Bukkit.getLogger();
    FileConfiguration config = this.getConfig();
 
    public void onEnable() {
        myPluginLogger.info("XP Modifier Started");
        getServer().getPluginManager().registerEvents(this, this);
        
        config.addDefault("XP Multiplier", 10);
        config.options().copyDefaults(true);
        saveConfig();
    }
 
    @EventHandler
    public void onPlayerExpChange(PlayerExpChangeEvent e) {
    	int xp_multiplier = config.getInt("XP Multiplier");
        int xp = e.getAmount();
        e.setAmount(xp * xp_multiplier);
    }
}