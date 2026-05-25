package me.bunnycat.warpsgui.Commands;

import me.bunnycat.warpsgui.Utils;
import me.bunnycat.warpsgui.Warps;
import me.bunnycat.warpsgui.Inventory.Inv;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class home implements CommandExecutor {
    private Inv inv = new Inv();

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player p) {

            if (command.getName().equalsIgnoreCase("home")) {
                if (p.hasPermission("warps.home")) {
                    if (args.length == 0) {
                        if (Warps.plugin.getConfig().getConfigurationSection("homes." + p.getUniqueId()) != null) {
                            if (Warps.plugin.getConfig().getConfigurationSection("homes." + p.getUniqueId()).getKeys(false).size() != 0) {
                                inv.homeInv(p);
                            } else {
                                Location location = p.getLocation();
                                p.playSound(location, Sound.valueOf(Warps.plugin.getConfig().getString("Sounds.SetHomeFirstSound")), 1.0F, 0.0F);
                                Utils.showTitleWrapper(p, Warps.plugin.getConfig().getString("Messages.SetHomeFirst"), "", 5);
                            }
                        } else {
                            Location location = p.getLocation();
                            p.playSound(location, Sound.valueOf(Warps.plugin.getConfig().getString("Sounds.SetHomeFirstSound")), 1.0F, 0.0F);
                            Utils.showTitleWrapper(p, Warps.plugin.getConfig().getString("Messages.SetHomeFirst"), "", 5);
                        }
                    } else {
                        if (args.length == 1) {
                            if (args.length == 1) {
                                if (Warps.plugin.getConfig().get("homes." + p.getUniqueId() + "." + args[0]) == null) {
                                    Utils.showTitleWrapper(p, Warps.plugin.getConfig().getString("Messages.NoHomeWithName") + " " + args[0], "", 5);
                                    Location location = p.getLocation();
                                    p.playSound(location, Sound.valueOf(Warps.plugin.getConfig().getString("Sounds.NoHomeWithNameSound")), 1.0F, 0.0F);
                                } else {
                                    Utils.tpToHome(args[0], p);
                                }
                            }
                        }
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