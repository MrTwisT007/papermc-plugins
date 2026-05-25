package me.bunnycat.warpsgui.Commands;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.bunnycat.warpsgui.Utils;
import me.bunnycat.warpsgui.Warps;

import static org.bukkit.Bukkit.getWorld;

public class spawn implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ((sender instanceof Player p)) {
            if (command.getName().equalsIgnoreCase("spawn")) {
                if (p.hasPermission("warps.spawn")) {
                    try {
                        int x = Warps.plugin.getConfig().getInt("spawn.x");
                        int y = Warps.plugin.getConfig().getInt("spawn.y");
                        int z = Warps.plugin.getConfig().getInt("spawn.z");
                        int pitch = Warps.plugin.getConfig().getInt("spawn.pitch");
                        int yaw = Warps.plugin.getConfig().getInt("spawn.yaw");
                        World world = getWorld(Warps.plugin.getConfig().getString("spawn.world"));
                        Location l = new Location(world, x, y, z, yaw, pitch);

                        p.teleport(l);

                        Location location = p.getLocation();
                        p.playSound(location, Sound.valueOf(Warps.plugin.getConfig().getString("Sounds.TeleportedToSpawnSound")), 1.0F, 0.0F);
                        Utils.showTitleWrapper(p, Warps.plugin.getConfig().getString("Messages.TeleportedToSpawn"), "", 5);

                        l.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, location, 10000);
                        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 40));
                        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 40));

                    } catch (Exception ex) {
                        Location location = p.getLocation();
                        p.playSound(location, Sound.valueOf(Warps.plugin.getConfig().getString("Sounds.SetSpawnFirstSound")), 1.0F, 0.0F);
                        Utils.showTitleWrapper(p, Warps.plugin.getConfig().getString("Messages.SetSpawnFirst"), "", 5);
                    }
                } else {
                    Location location = p.getLocation();
                    p.playSound(location, Sound.valueOf(Warps.plugin.getConfig().getString("Sounds.NoPermissionsSound")), 1.0F, 0.0F);
                    Utils.showTitleWrapper(p, Warps.plugin.getConfig().getString("Messages.NoPermissions"), "", 5);
                }
            } else {
                sender.sendMessage(Warps.plugin.getConfig().getString("Messages.NoPlayer").replaceAll("&", "§"));
            }
        }
        return true;
    }
}