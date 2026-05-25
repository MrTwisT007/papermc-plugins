package me.bunnycat.warpsgui.Commands;

import com.flowpowered.math.vector.Vector3d;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import me.bunnycat.warpsgui.Utils;
import me.bunnycat.warpsgui.Warps;

import java.util.Map;

public class sethome implements CommandExecutor
{
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if ((sender instanceof Player p)) {
            if (command.getName().equalsIgnoreCase("sethome") || command.getName().equalsIgnoreCase("sh")) {

                ConfigurationSection homeSection = Warps.plugin.getConfig().getConfigurationSection("homes." + p.getUniqueId());
                int numHomes = (homeSection != null) ? homeSection.getKeys(false).size() : 0;

                if (p.hasPermission("warps.sethome")) {
                    if (args.length == 1) {
                        if ((numHomes < 54)) {
                            Location l = p.getLocation();

                            Warps.plugin.getConfig().set("homes." + p.getUniqueId() + "." + args[0] + ".x", (float) l.getBlockX());
                            Warps.plugin.getConfig().set("homes." + p.getUniqueId() + "." + args[0] + ".y", (float) l.getBlockY());
                            Warps.plugin.getConfig().set("homes." + p.getUniqueId() + "." + args[0] + ".z", (float) l.getBlockZ());
                            Warps.plugin.getConfig().set("homes." + p.getUniqueId() + "." + args[0] + ".v", l.getPitch());
                            Warps.plugin.getConfig().set("homes." + p.getUniqueId() + "." + args[0] + ".w", l.getYaw());
                            Warps.plugin.getConfig().set("homes." + p.getUniqueId() + "." + args[0] + ".world", p.getWorld().getName());
                            Warps.plugin.getConfig().set("homes." + p.getUniqueId() + "." + args[0] + ".playername", p.getName());
                            Warps.plugin.saveConfig();
                            Warps.plugin.reloadConfig();

                            if(Warps.plugin.getConfig().getBoolean("BluemapIntegration.enabled")){
                                if (Warps.plugin.getConfig().getBoolean("BluemapIntegration.showhomes")){
                                    Warps.blueMap.getWorld(l.getWorld().getName()).ifPresent(blueWorld -> blueWorld.getMaps().forEach(map -> {
                                        String markerID = p.getUniqueId()+args[0];
                                        String markerName = p.getName()+"'s "+args[0];
                                        Vector3d markerCoords = Vector3d.from(l.getBlockX(), l.getBlockY(), l.getBlockZ());
                                        Utils.addMarker(l.getWorld().getName(), markerID, markerName, markerCoords,"homes");
                                    }));
                                }
                            }

                            Utils.showTitleWrapper(p, Warps.plugin.getConfig().getString("Messages.HomeCreated"), "", 5);
                            Location location1 = p.getLocation();
                            p.playSound(location1, Sound.valueOf(Warps.plugin.getConfig().getString("Sounds.HomeCreatedSound")), 1.0F, 0.0F);
                        } else {
                            Location location = p.getLocation();
                            p.playSound(location, Sound.valueOf(Warps.plugin.getConfig().getString("Sounds.NoMoreHomesSound")), 1.0F, 0.0F);
                            Utils.showTitleWrapper(p, Warps.plugin.getConfig().getString("Messages.NoMoreHomes"), "", 5);
                        }
                    } else {
                        Location location = p.getLocation();
                        p.playSound(location, Sound.valueOf(Warps.plugin.getConfig().getString("Sounds.ToSetHomeSound")), 1.0F, 0.0F);
                        Utils.showTitleWrapper(p, Warps.plugin.getConfig().getString("Messages.ToSetHome"), "", 5);
                    }
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