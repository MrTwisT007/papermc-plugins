package me.bunnycat.warpsgui.Listeners;

import me.bunnycat.warpsgui.Inventory.Inv;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.bunnycat.warpsgui.Utils;
import me.bunnycat.warpsgui.Warps;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class InventoryEvent implements org.bukkit.event.Listener {
    private Inv inv = new Inv();

    public InventoryEvent(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (e.getSlot() >= 0) {
            String invtitle = PlainTextComponentSerializer.plainText().serialize(e.getView().title());
            if (invtitle.equals(Warps.plugin.getConfig().getString("Messages.Menu"))) {
                if (e.getSlot() == 1) {
                    if (Warps.plugin.getConfig().getConfigurationSection("warps") != null) {
                        if (Warps.plugin.getConfig().getConfigurationSection("warps").getKeys(false).size() != 0) {
                            e.setCancelled(true);
                            p.closeInventory();
                            Location location = p.getLocation();
                            p.playSound(location, Sound.valueOf(Warps.plugin.getConfig().getString("Sounds.ClickSound")), 1.0F, 0.0F);
                            inv.warpInv(p);
                        } else {
                            e.setCancelled(true);
                            p.closeInventory();
                            Location location = p.getLocation();
                            p.playSound(location, Sound.valueOf(Warps.plugin.getConfig().getString("Sounds.SetWarpFirstSound")), 1.0F, 0.0F);
                            Utils.showTitleWrapper(p, Warps.plugin.getConfig().getString("Messages.SetWarpFirst"), "", 5);
                        }
                    } else {
                        e.setCancelled(true);
                        p.closeInventory();
                        Location location = p.getLocation();
                        p.playSound(location, Sound.valueOf(Warps.plugin.getConfig().getString("Sounds.SetWarpFirstSound")), 1.0F, 0.0F);
                        Utils.showTitleWrapper(p, Warps.plugin.getConfig().getString("Messages.SetWarpFirst"), "", 5);
                    }
                    if (!p.hasPermission("warps.warp")) {
                        e.setCancelled(true);
                        p.closeInventory();
                        Location location = p.getLocation();
                        p.playSound(location, Sound.valueOf(Warps.plugin.getConfig().getString("Sounds.NoPermissionsSound")), 1.0F, 0.0F);
                        Utils.showTitleWrapper(p, Warps.plugin.getConfig().getString("Messages.NoPermissions"), "", 5);
                    }
                } else if (e.getSlot() == 3) {
                    if (Warps.plugin.getConfig().getConfigurationSection("homes." + p.getUniqueId()) != null) {
                        if (Warps.plugin.getConfig().getConfigurationSection("homes." + p.getUniqueId()).getKeys(false).size() != 0) {
                            e.setCancelled(true);
                            p.closeInventory();
                            Location location = p.getLocation();
                            p.playSound(location, Sound.valueOf(Warps.plugin.getConfig().getString("Sounds.ClickSound")), 1.0F, 0.0F);
                            inv.homeInv(p);
                        } else {
                            e.setCancelled(true);
                            p.closeInventory();
                            Location location = p.getLocation();
                            p.playSound(location, Sound.valueOf(Warps.plugin.getConfig().getString("Sounds.SetHomeFirstSound")), 1.0F, 0.0F);
                            Utils.showTitleWrapper(p, Warps.plugin.getConfig().getString("Messages.SetHomeFirst"), "", 5);
                        }
                    } else {
                        e.setCancelled(true);
                        p.closeInventory();
                        Location location = p.getLocation();
                        p.playSound(location, Sound.valueOf(Warps.plugin.getConfig().getString("Sounds.SetHomeFirstSound")), 1.0F, 0.0F);
                        Utils.showTitleWrapper(p, Warps.plugin.getConfig().getString("Messages.SetHomeFirst"), "", 5);
                    }
                    if (!p.hasPermission("warps.home")) {
                        e.setCancelled(true);
                        p.closeInventory();
                        Location location = p.getLocation();
                        p.playSound(location, Sound.valueOf(Warps.plugin.getConfig().getString("Sounds.NoPermissionsSound")), 1.0F, 0.0F);
                        Utils.showTitleWrapper(p, Warps.plugin.getConfig().getString("Messages.NoPermissions"), "", 5);
                    }
                } else if (e.getSlot() == 7) {
                    e.setCancelled(true);
                    p.closeInventory();
                    Location location = p.getLocation();
                    p.playSound(location, Sound.BLOCK_WOODEN_DOOR_CLOSE, 1.0F, 0.0F);
                } else if (e.getSlot() == 5) {
                    if (p.hasPermission("warps.spawn")) {
                        try {
                            int x = Warps.plugin.getConfig().getInt("spawn.x");
                            int y = Warps.plugin.getConfig().getInt("spawn.y");
                            int z = Warps.plugin.getConfig().getInt("spawn.z");
                            int pitch = Warps.plugin.getConfig().getInt("spawn.pitch");
                            int yaw = Warps.plugin.getConfig().getInt("spawn.yaw");

                            World world = Bukkit.getWorld(Warps.plugin.getConfig().getString("spawn.world"));

                            Location location = new Location(world, x, y, z, yaw, pitch);
                            p.playSound(location, Sound.valueOf(Warps.plugin.getConfig().getString("Sounds.TeleportedToSpawnSound")), 1.0F, 0.0F);
                            Utils.showTitleWrapper(p, Warps.plugin.getConfig().getString("Messages.TeleportedToSpawn"), "", 5);

                            p.teleport(location);

                            location.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, location, 10000);
                            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10, 40));
                            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10, 40));
                        } catch (Exception ex) {
                            e.setCancelled(true);
                            p.closeInventory();
                            Utils.showTitleWrapper(p, Warps.plugin.getConfig().getString("Messages.SetSpawnFirst"), "", 5);
                            Location location = p.getLocation();
                            p.playSound(location, Sound.valueOf(Warps.plugin.getConfig().getString("Sounds.SetSpawnFirstSound")), 1.0F, 0.0F);
                        }
                    } else {
                        Utils.showTitleWrapper(p, Warps.plugin.getConfig().getString("Messages.NoPermissions"), "", 5);
                        Location location = p.getLocation();
                        p.playSound(location, Sound.valueOf(Warps.plugin.getConfig().getString("Sounds.NoPermissionsSound")), 1.0F, 0.0F);
                    }
                } else {
                    e.setCancelled(true);
                }
            } else if (invtitle.equals(Warps.plugin.getConfig().getString("Messages.Warps"))) {
                if (p.hasPermission("warps.warp")) {
                    if (e.getCurrentItem() != null) {
                        String warpName = PlainTextComponentSerializer.plainText().serialize(e.getCurrentItem().getItemMeta().displayName());
                        Utils.tpToWarp(warpName,p);
                        //try {
                        //    int x = Warps.plugin.getConfig().getInt("warps." + warpName + ".x");
                        //    int y = Warps.plugin.getConfig().getInt("warps." + warpName + ".y");
                        //    int z = Warps.plugin.getConfig().getInt("warps." + warpName + ".z");
                        //    int yaw = Warps.plugin.getConfig().getInt("warps." + warpName + ".yaws");
                        //    int pitch = Warps.plugin.getConfig().getInt("warps." + warpName + ".pitch");
//
                        //    World world = Bukkit.getWorld(Warps.plugin.getConfig().getString("warps." + warpName + ".world"));
//
                        //    Location location = new Location(world, x, y, z, yaw, pitch);
                        //    p.playSound(location, Sound.valueOf(Warps.plugin.getConfig().getString("Sounds.TeleportedToWarpSound")), 1.0F, 0.0F);
                        //    Utils.showTitleWrapper(p, Warps.plugin.getConfig().getString("Messages.TeleportedToWarp") + " " + warpName, "", 5);
//
                        //    p.teleport(location);
//
                        //    location.getWorld().playEffect(location, Effect.MOBSPAWNER_FLAMES, 10000);
                        //    p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10, 40));
                        //    p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10, 40));
                        //} catch (Exception localException1) {
                        //    localException1.printStackTrace();
                        //}
                    }
                } else {
                    e.setCancelled(true);
                    p.closeInventory();
                    Utils.showTitleWrapper(p, Warps.plugin.getConfig().getString("Messages.NoPermissions"), "", 5);
                    Location location = p.getLocation();
                    p.playSound(location, Sound.valueOf(Warps.plugin.getConfig().getString("Sounds.NoPermissionsSound")), 1.0F, 0.0F);
                }
                e.setCancelled(true);
            } else if (invtitle.equals(Warps.plugin.getConfig().getString("Messages.Icons"))) {
                int slot = e.getSlot();
                if (slot >= 0) {
                    Location location = p.getLocation();
                    p.playSound(location, Sound.valueOf(Warps.plugin.getConfig().getString("Sounds.ClickSound")), 1.0F, 0.0F);

                    //Get the warpname from the first item in the WarpName inventory
                    String warpname = e.getClickedInventory().getItem(0).getItemMeta().getPersistentDataContainer().get(NamespacedKey.fromString("warpname"), PersistentDataType.STRING);
                    Warps.plugin.getConfig().set("warps." + warpname + ".item", e.getCurrentItem().getType().toString());
                    Warps.plugin.saveConfig();

                    p.closeInventory();
                    Warps.plugin.reloadConfig();
                } else {
                    p.closeInventory();
                    e.setCancelled(true);
                }
            } else if ((invtitle.equals(Warps.plugin.getConfig().getString("Messages.Homes"))) &&
                    (e.getCurrentItem() != null)) {
                if (Warps.plugin.getConfig().get("homes." + p.getUniqueId() + "." + PlainTextComponentSerializer.plainText().serialize(e.getCurrentItem().getItemMeta().displayName())) == null) {
                    e.setCancelled(true);
                } else if (p.hasPermission("warps.home")) {
                    if (e.getCurrentItem() != null) {
                        String homeName = PlainTextComponentSerializer.plainText().serialize(e.getCurrentItem().getItemMeta().displayName());
                        Utils.tpToHome(homeName,p);
                        //try {
                        //    int x = Warps.plugin.getConfig().getInt("homes." + p.getUniqueId() + "." + homeName + ".x");
                        //    int y = Warps.plugin.getConfig().getInt("homes." + p.getUniqueId() + "." + homeName + ".y");
                        //    int z = Warps.plugin.getConfig().getInt("homes." + p.getUniqueId() + "." + homeName + ".z");
                        //    int yaw = Warps.plugin.getConfig().getInt("homes." + p.getUniqueId() + "." + homeName + ".w");
                        //    int pitch = Warps.plugin.getConfig().getInt("homes." + p.getUniqueId() + "." + homeName + ".v");
//
                        //    World world = Bukkit.getWorld(Warps.plugin.getConfig().getString("homes." + p.getUniqueId() + "." + homeName + ".world"));
//
                        //    Location location = new Location(world, x, y, z, yaw, pitch);
                        //    p.playSound(location, Sound.valueOf(Warps.plugin.getConfig().getString("Sounds.TeleportedToHomeSound")), 1.0F, 0.0F);
                        //    Utils.showTitleWrapper(p, Warps.plugin.getConfig().getString("Messages.TeleportedToHome") + " " + homeName, "", 5);
//
                        //    p.teleport(location);
//
                        //    location.getWorld().playEffect(location, Effect.MOBSPAWNER_FLAMES, 10000);
                        //    p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10, 40));
                        //    p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10, 40));
                        //} catch (Exception localException2) {
                        //    localException2.printStackTrace();
                        //}
                    }
                } else {
                    p.closeInventory();
                    e.setCancelled(true);
                    Location location = p.getLocation();
                    p.playSound(location, Sound.valueOf(Warps.plugin.getConfig().getString("Sounds.NoPermissionsSound")), 1.0F, 0.0F);
                    Utils.showTitleWrapper(p, Warps.plugin.getConfig().getString("Messages.NoPermissions"), "", 5);
                }
            }
        }
    }
}