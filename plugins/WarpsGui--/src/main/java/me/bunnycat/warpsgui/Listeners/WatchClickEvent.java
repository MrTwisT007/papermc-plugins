package me.bunnycat.warpsgui.Listeners;
import me.bunnycat.warpsgui.Warps;
import me.bunnycat.warpsgui.Inventory.Inv;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class WatchClickEvent implements org.bukkit.event.Listener
{
    private Inv inv = new Inv();

    public WatchClickEvent(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void OnClick(PlayerInteractEvent event) {
        HumanEntity ent = event.getPlayer();
        if (ent instanceof Player p) {
            if (event.getItem() != null) {
                if (event.getItem().getType() == Material.COMPASS) {
                    if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        this.inv.mainInv(p);
                        Location location = p.getLocation();
                        p.playSound(location, Sound.valueOf(Warps.plugin.getConfig().getString("Sounds.MenuOpenSound")), 1.0F, 0.0F);
                    }
                }
            }
        }
    }
}