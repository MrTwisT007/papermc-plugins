package me.bunnycat.warpsgui.Inventory;

import me.bunnycat.warpsgui.Warps;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class Inv {
    public void mainInv(Player p) {
        Inventory inv = Bukkit.createInventory(null, 9, Component.text("" + Warps.plugin.getConfig().getString("Messages.Menu")));

        ItemStack warpitem = new ItemStack(Material.valueOf("" + Warps.plugin.getConfig().getString("Icons.Warps")));
        ItemMeta warpmeta = warpitem.getItemMeta();
        warpmeta.displayName(Component.text("" + Warps.plugin.getConfig().getString("Messages.Warps")));
        warpitem.setItemMeta(warpmeta);

        ItemStack homeitem = new ItemStack(Material.valueOf("" + Warps.plugin.getConfig().getString("Icons.Homes")));
        ItemMeta homemeta = homeitem.getItemMeta();
        homemeta.displayName(Component.text("" + Warps.plugin.getConfig().getString("Messages.Homes")));
        homeitem.setItemMeta(homemeta);

        ItemStack spawnitem = new ItemStack(Material.valueOf("" + Warps.plugin.getConfig().getString("Icons.Spawn")));
        ItemMeta spawnmeta = spawnitem.getItemMeta();
        spawnmeta.displayName(Component.text("" + Warps.plugin.getConfig().getString("Messages.Spawn")));
        spawnitem.setItemMeta(spawnmeta);

        ItemStack nopermitem = new ItemStack(Material.valueOf("" + Warps.plugin.getConfig().getString("Icons.NoPermissions")));
        ItemMeta nopermmeta = nopermitem.getItemMeta();
        nopermmeta.displayName(Component.text("" + Warps.plugin.getConfig().getString("Messages.NoPermissions")));
        nopermitem.setItemMeta(nopermmeta);

        ItemStack closeitem = new ItemStack(Material.valueOf("" + Warps.plugin.getConfig().getString("Icons.CloseMenu")));
        ItemMeta closemeta = closeitem.getItemMeta();
        closemeta.displayName(Component.text("" + Warps.plugin.getConfig().getString("Messages.CloseMenu")));
        closeitem.setItemMeta(closemeta);

        if (p.hasPermission("warps.warp")) inv.setItem(1, warpitem);
        else
            inv.setItem(1, nopermitem);
        if (p.hasPermission("warps.home")) inv.setItem(3, homeitem);
        else
            inv.setItem(3, nopermitem);
        if (p.hasPermission("warps.spawn")) inv.setItem(5, spawnitem);
        else {
            inv.setItem(5, nopermitem);
        }
        inv.setItem(7, closeitem);
        p.openInventory(inv);
    }

    public void warpInv(Player p) {
        Warps.plugin.reloadConfig();
        ConfigurationSection warpSection = Warps.plugin.getConfig().getConfigurationSection("warps");

        int slots = 9;

        int numWarps = warpSection != null ? warpSection.getKeys(false).size() : 0;

        if (numWarps < 9) slots = 9;
        if ((numWarps >= 9) && (numWarps < 18)) slots = 18;
        if ((numWarps >= 18) && (numWarps < 27)) slots = 27;
        if ((numWarps >= 27) && (numWarps < 36)) slots = 36;
        if ((numWarps >= 36) && (numWarps < 45)) slots = 45;
        if ((numWarps >= 45) && (numWarps < 54)) slots = 54;
        Inventory inv = Bukkit.createInventory(null, slots, Component.text(Warps.plugin.getConfig().getString("Messages.Warps")));

        for (String warpEntry : warpSection.getKeys(false)) {
            ItemStack warpItem = new ItemStack(Material.valueOf(Warps.plugin.getConfig().getString("warps." + warpEntry + ".item")));
            ItemMeta warpMeta = warpItem.getItemMeta();
            warpMeta.displayName(Component.text(warpEntry));

            List<Component> lores = new ArrayList<Component>();
            lores.add(Component.text("§r§aClick §7to teleport!"));
            lores.add(Component.text("§r§eX: §5" + Warps.plugin.getConfig().getInt("warps." + "." + warpEntry + ".x")));
            lores.add(Component.text("§r§eY: §5" + Warps.plugin.getConfig().getInt("warps." + "." + warpEntry + ".y")));
            lores.add(Component.text("§r§eZ: §5" + Warps.plugin.getConfig().getInt("warps." + "." + warpEntry + ".z")));
            lores.add(Component.text("§r§eWorld: §5" + Warps.plugin.getConfig().getString("warps." + "." + warpEntry + ".world")));
            warpMeta.lore(lores);

            warpItem.setItemMeta(warpMeta);
            inv.addItem(warpItem);
        }
        p.openInventory(inv);
    }

    public void createWarpIconChoiceInv(Player p, String warpname) {
        Inventory inv = Bukkit.createInventory(null, 54, Component.text("" + Warps.plugin.getConfig().getString("Messages.Icons")));
        List<String> warpiconlist = Warps.plugin.getConfig().getStringList("WarpIcons");
        for(String warpicon : warpiconlist) {
            inv.addItem(new ItemStack(Material.valueOf(warpicon)));
        }
        //Need to remember the warpname to know where to put the selected icon when the WarpIcon menu is invoked. Hide it in the first item's NBT tag
        ItemStack warpNameItem = inv.getItem(0);
        ItemMeta warpNameItemMeta = warpNameItem.getItemMeta();
        warpNameItemMeta.getPersistentDataContainer().set(NamespacedKey.fromString("warpname"), PersistentDataType.STRING, warpname);
        warpNameItem.setItemMeta(warpNameItemMeta);

        p.openInventory(inv);
    }

    public void homeInv(Player p) {
        ConfigurationSection homeSection = Warps.plugin.getConfig().getConfigurationSection("homes." + p.getUniqueId());

        int slots = 9;

        int numHomes = homeSection != null ? homeSection.getKeys(false).size() : 0;

        if (numHomes <= 9) slots = 9;
        if ((numHomes >= 9) && (numHomes <= 18)) slots = 18;
        if ((numHomes >= 18) && (numHomes <= 27)) slots = 27;
        if ((numHomes >= 27) && (numHomes <= 36)) slots = 36;
        if ((numHomes >= 36) && (numHomes <= 45)) slots = 45;
        if ((numHomes >= 45) && (numHomes <= 54)) slots = 54;
        Inventory inv = Bukkit.createInventory(null, slots, Component.text("" + Warps.plugin.getConfig().getString("Messages.Homes")));

        for (String homeEntry : homeSection.getKeys(false)) {
            ItemStack homeItem = new ItemStack(Material.valueOf("" + Warps.plugin.getConfig().getString("Icons.HomeIcon")));
            ItemMeta homeMeta = homeItem.getItemMeta();
            homeMeta.displayName(Component.text(homeEntry));

            List<Component> lores = new ArrayList<Component>();
            lores.add(Component.text("§r§aClick §7to teleport!"));
            lores.add(Component.text("§r§eX: §5" + Warps.plugin.getConfig().getInt("homes." + p.getUniqueId() + "." + homeEntry + ".x")));
            lores.add(Component.text("§r§eY: §5" + Warps.plugin.getConfig().getInt("homes." + p.getUniqueId() + "." + homeEntry + ".y")));
            lores.add(Component.text("§r§eZ: §5" + Warps.plugin.getConfig().getInt("homes." + p.getUniqueId() + "." + homeEntry + ".z")));
            lores.add(Component.text("§r§eWorld: §5" + Warps.plugin.getConfig().getString("homes." + p.getUniqueId() + "." + homeEntry + ".world")));

            homeMeta.lore(lores);
            homeItem.setItemMeta(homeMeta);
            inv.addItem(homeItem);
        }
        p.openInventory(inv);
    }
}