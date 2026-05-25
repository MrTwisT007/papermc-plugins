package me.bunnycat.oplevelshifter;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;

import org.bukkit.entity.Player;

public class OPLevelShifter extends JavaPlugin implements Listener {
    Logger myPluginLogger = Bukkit.getLogger();
 
    public void onEnable() {
        myPluginLogger.info("SHIFTER Started");
        getServer().getPluginManager().registerEvents(this, this);
    }
 
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
    	Player p = e.getPlayer();
    	p.sendOpLevel((byte)4);
    }
    
    @EventHandler
    public void onRespawn(PlayerPostRespawnEvent e) {
    	Player p = e.getPlayer();
    	p.sendOpLevel((byte)4);
    }
    
    @EventHandler
    public void onDiffWorld(PlayerTeleportEvent e) {
    	Player p = e.getPlayer();
    	new BukkitRunnable() {
 		   @Override
 		   public void run() {
 			   p.sendOpLevel((byte)4);
 		   }
    	}.runTaskLater(this, 1);
    }

}