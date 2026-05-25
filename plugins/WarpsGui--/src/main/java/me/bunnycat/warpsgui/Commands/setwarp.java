package me.bunnycat.warpsgui.Commands;

import com.flowpowered.math.vector.Vector3d;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import me.bunnycat.warpsgui.Utils;
import me.bunnycat.warpsgui.Warps;
import me.bunnycat.warpsgui.Inventory.Inv;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class setwarp implements CommandExecutor {
    private Inv inv = new Inv();
    private Utils utils = new Utils();

    public boolean onCommand(@NotNull CommandSender sender, Command command, String label, String[] args) {
        if ((sender instanceof Player p)) {
            if (command.getName().equalsIgnoreCase("setwarp") || command.getName().equalsIgnoreCase("sw")) {

                ConfigurationSection warpSection = Warps.plugin.getConfig().getConfigurationSection("warps");
                int numWarps = warpSection == null ? 0 : warpSection.getKeys(false).size();

                if (p.hasPermission("warps.setwarp")) {
                    if (args.length == 1) {
                        if (numWarps < 54) {
                            Location l = p.getLocation();

                            Warps.plugin.getConfig().set("warps." + args[0] + ".x", (float) l.getBlockX());
                            Warps.plugin.getConfig().set("warps." + args[0] + ".y", (float) l.getBlockY());
                            Warps.plugin.getConfig().set("warps." + args[0] + ".z", (float) l.getBlockZ());
                            Warps.plugin.getConfig().set("warps." + args[0] + ".yaws", l.getYaw());
                            Warps.plugin.getConfig().set("warps." + args[0] + ".pitch", l.getPitch());
                            Warps.plugin.getConfig().set("warps." + args[0] + ".world", p.getWorld().getName());
                            Warps.plugin.getConfig().set("warps." + args[0] + ".item", Material.OAK_SIGN.toString());
                            Warps.plugin.saveConfig();
                            Warps.plugin.reloadConfig();

                            if(Warps.plugin.getConfig().getBoolean("BluemapIntegration.enabled")){
                                if (Warps.plugin.getConfig().getBoolean("BluemapIntegration.showwarps")){
                                    Warps.blueMap.getWorld(l.getWorld().getName()).ifPresent(blueWorld -> blueWorld.getMaps().forEach(map -> {;
                                        Vector3d markerCoords = Vector3d.from(l.getBlockX(), l.getBlockY(), l.getBlockZ());
                                        Utils.addMarker(l.getWorld().getName(), args[0], args[0], markerCoords,"warps");
                                    }));
                                }
                            }

                            inv.createWarpIconChoiceInv(p, args[0]);

                            Utils.showTitleWrapper(p, Warps.plugin.getConfig().getString("Messages.WarpCreated"), "", 5);
                            Location location = p.getLocation();
                            p.playSound(location, Sound.valueOf(Warps.plugin.getConfig().getString("Sounds.WarpCreatedSound")), 1.0F, 0.0F);
                        } else {
                            sender.sendMessage("Too many warps, remove one!");
                        }
                    } else {
                        Location location = p.getLocation();
                        p.playSound(location, Sound.valueOf(Warps.plugin.getConfig().getString("Sounds.ToSetWarpSound")), 1.0F, 0.0F);
                        Utils.showTitleWrapper(p, Warps.plugin.getConfig().getString("Messages.ToSetWarp"), "", 5);
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