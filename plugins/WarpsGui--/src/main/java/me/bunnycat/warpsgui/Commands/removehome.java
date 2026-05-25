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

public class removehome implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ((sender instanceof Player p)) {
            if (command.getName().equalsIgnoreCase("removehome")) {
                if (args.length == 1) {
                    if (p.hasPermission("warps.removehome")) {
                        if (Warps.plugin.getConfig().get("homes." + p.getUniqueId() + "." + args[0]) == null) {
                            Utils.showTitleWrapper(p, Warps.plugin.getConfig().getString("Messages.NoHomeWithName") + " " + args[0], "", 5);
                            Location location = p.getLocation();
                            p.playSound(location, Sound.valueOf(Warps.plugin.getConfig().getString("Sounds.NoHomeWithNameSound")), 1.0F, 0.0F);
                        } else {
                            if(Warps.plugin.getConfig().getBoolean("BluemapIntegration.enabled")){
                                String homeWorld = Warps.plugin.getConfig().getString("homes."+p.getUniqueId()+"."+args[0]+".world");
                                String markerID = p.getUniqueId()+args[0];
                                Utils.removeMarker(homeWorld,markerID,"homes");
                            }

                            Warps.plugin.getConfig().getConfigurationSection("homes." + p.getUniqueId()).set(args[0], null);
                            Warps.plugin.saveConfig();
                            Utils.showTitleWrapper(p, Warps.plugin.getConfig().getString("Messages.HomeRemoved"), "", 5);
                        }
                    } else {
                        Location location = p.getLocation();
                        p.playSound(location, Sound.valueOf(Warps.plugin.getConfig().getString("Sounds.NoPermissionsSound")), 1.0F, 0.0F);
                        Utils.showTitleWrapper(p, Warps.plugin.getConfig().getString("Messages.NoPermissions"), "", 5);
                    }
                } else {
                    Location location = p.getLocation();
                    p.playSound(location, Sound.valueOf(Warps.plugin.getConfig().getString("Sounds.ToRemoveHomeSound")), 1.0F, 0.0F);
                    Utils.showTitleWrapper(p, Warps.plugin.getConfig().getString("Messages.ToRemoveHome"), "", 5);
                }
            }
        } else {
            sender.sendMessage(Warps.plugin.getConfig().getString("Messages.NoPlayer").replaceAll("&", "§"));
        }
        return true;
    }
}