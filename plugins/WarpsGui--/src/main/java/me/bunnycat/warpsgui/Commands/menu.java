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

public class menu implements CommandExecutor {
    private Inv inv = new Inv();

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ((sender instanceof Player p)) {
            if (command.getName().equalsIgnoreCase("menu")) {

                if (p.hasPermission("warps.menu")) {
                    Location location = p.getLocation();
                    p.playSound(location, Sound.valueOf(Warps.plugin.getConfig().getString("Sounds.MenuOpenSound")), 1.0F, 0.0F);
                    inv.mainInv(p);
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