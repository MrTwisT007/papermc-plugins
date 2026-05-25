package me.bunnycat.warpsgui.Commands;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.bunnycat.warpsgui.Utils;
import me.bunnycat.warpsgui.Warps;

public class removewarp implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ((sender instanceof Player p)) {
            if (command.getName().equalsIgnoreCase("removewarp")) {
                if (args.length == 1) {
                    if (p.hasPermission("warps.removewarp")) {
                        if (Warps.plugin.getConfig().get("warps." + args[0]) == null) {
                            Utils.showTitleWrapper(p, Warps.plugin.getConfig().getString("Messages.NoWarpWithName") + " " + args[0], "", 5);
                            Location location = p.getLocation();
                            p.playSound(location, Sound.valueOf(Warps.plugin.getConfig().getString("Sounds.NoWarpWithNameSound")), 1.0F, 0.0F);
                        } else {
                            if(Warps.plugin.getConfig().getBoolean("BluemapIntegration.enabled")){
                                String warpWorld = Warps.plugin.getConfig().getString("warps."+args[0]+".world");
                                String markerID = args[0];
                                Utils.removeMarker(warpWorld,markerID,"warps");
                            }

                            Warps.plugin.getConfig().getConfigurationSection("warps").set(args[0], null);
                            Warps.plugin.saveConfig();
                            Utils.showTitleWrapper(p, Warps.plugin.getConfig().getString("Messages.WarpRemoved"), "", 5);
                        }
                    } else {
                        Location location = p.getLocation();
                        p.playSound(location, Sound.valueOf(Warps.plugin.getConfig().getString("Sounds.NoPermissionsSound")), 1.0F, 0.0F);
                        Utils.showTitleWrapper(p, Warps.plugin.getConfig().getString("Messages.NoPermissions"), "", 5);
                    }
                } else {
                    Location location = p.getLocation();
                    p.playSound(location, Sound.valueOf(Warps.plugin.getConfig().getString("Sounds.ToRemoveWarpSound")), 1.0F, 0.0F);
                    Utils.showTitleWrapper(p, Warps.plugin.getConfig().getString("Messages.ToRemoveWarp"), "", 5);
                }
            }
        } else {
            sender.sendMessage(Warps.plugin.getConfig().getString("Messages.NoPlayer").replaceAll("&", "§"));
        }
        return true;
    }
}