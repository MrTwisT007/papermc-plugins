package me.bunnycat.warpsgui.Commands;

import com.flowpowered.math.vector.Vector3d;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.bunnycat.warpsgui.Utils;
import me.bunnycat.warpsgui.Warps;

import java.util.Map;
import java.util.Vector;

public class setspawn implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if ((sender instanceof Player p)) {
            if (command.getName().equalsIgnoreCase("setspawn")) {
                if (p.hasPermission("warps.setspawn")) {
                    Location l = p.getLocation();

                    String spawnWorldOld = Warps.plugin.getConfig().getString("spawn.world");

                    Warps.plugin.getConfig().set("spawn.x", (float) l.getBlockX());
                    Warps.plugin.getConfig().set("spawn.y", (float) l.getBlockY());
                    Warps.plugin.getConfig().set("spawn.z", (float) l.getBlockZ());
                    Warps.plugin.getConfig().set("spawn.pitch", l.getPitch());
                    Warps.plugin.getConfig().set("spawn.yaw", l.getYaw());
                    Warps.plugin.getConfig().set("spawn.world", p.getWorld().getName());
                    Warps.plugin.saveConfig();
                    Warps.plugin.reloadConfig();

                    if (Warps.plugin.getConfig().getBoolean("BluemapIntegration.enabled")) {
                        if (Warps.plugin.getConfig().getBoolean("BluemapIntegration.showspawn")) {
                            Vector3d markerCoords = Vector3d.from(l.getBlockX(), l.getBlockY(), l.getBlockZ());
                            if (spawnWorldOld != null) {
                                Utils.removeMarker(spawnWorldOld, "spawn", "spawn");
                            }
                            Utils.addMarker(l.getWorld().getName(), "spawn", "Spawn", markerCoords, "spawn");
                        }
                    }

                    Location location = p.getLocation();
                    p.playSound(location, Sound.valueOf(Warps.plugin.getConfig().getString("Sounds.SpawnCreatedSound")), 1.0F, 0.0F);
                    Utils.showTitleWrapper(p, Warps.plugin.getConfig().getString("Messages.SpawnCreated"), "", 5);
                } else {
                    Location location = p.getLocation();
                    p.playSound(location, Sound.valueOf(Warps.plugin.getConfig().getString("Sounds.NoPermissionsSound")), 1.0F, 0.0F);
                    Utils.showTitleWrapper(p, Warps.plugin.getConfig().getString("Messages.NoPermissions"), "", 5);
                }
            }
        } else {
            sender.sendMessage(Warps.plugin.getConfig().getString("Messages.NoPlayer").replaceAll("&", "§"));
        }

        return true;
    }
}