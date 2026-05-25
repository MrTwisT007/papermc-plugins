package me.bunnycat.twoolio;

import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;


public class Commands implements CommandExecutor{

    static FileConfiguration configFile = Twoolio.getConfigManager().getConfig();
    public boolean onCommand(org.bukkit.command.CommandSender sender, Command command, String label, String[] args) {
        Map<String, Object> expToolConfigMap = configFile.getConfigurationSection("expConfig.tools.expPerLevel").getValues(false);
        Player p = null;
        if ((sender instanceof Player)) {
            p = (Player)sender;
        }
        if (command.getName().equalsIgnoreCase("twoolioupdate")) {
            int maxlevelinconfig = 0;
            Entry<String, Object> maxLevelEntry = null;

            for (var levelupcost : expToolConfigMap.entrySet()) {
                if (Integer.parseInt(levelupcost.getKey()) > maxlevelinconfig) {
                    maxLevelEntry = levelupcost;
                }
            }

            Inventory playerinventory = p.getInventory();
            for (var item : playerinventory.getContents())
            {
                if (item == null) continue;
                if(Utils.isTool(item)) {
                    ItemMeta toolmeta = item.getItemMeta();
                    Integer toollevel = Utils.getIntfromPDC(item.getItemMeta(), "Level");
                    if(toollevel >= Integer.parseInt(maxLevelEntry.getKey())) {
                        Utils.changeIntTags(toolmeta, "Levelup", Optype.SET, Integer.parseInt(maxLevelEntry.getValue().toString()));
                    } else {
                        Utils.changeIntTags(toolmeta, "Levelup", Optype.SET, Integer.parseInt(expToolConfigMap.get(toollevel.toString()).toString()));
                    }


                    Utils.addToolLore(toolmeta);
                    item.setItemMeta(toolmeta);
                }
            }
        }

        if (command.getName().equalsIgnoreCase("settoolupgrades")) {
            try {
                int upgradesargument = Integer.parseInt(args[0]);
                int maxupgradesclamp = 100;
                int minupgradesclamp = 0;
                int upgradestoadd = upgradesargument > maxupgradesclamp ? maxupgradesclamp : upgradesargument < minupgradesclamp ? minupgradesclamp : upgradesargument;
                PlayerInventory playerinventory = p.getInventory();
                ItemStack item = playerinventory.getItemInMainHand();
                ItemMeta toolmeta = item.getItemMeta();
                if(Utils.isTool(item)) {
                    Utils.changeIntTags(toolmeta, "Upgrades", Optype.SET, upgradestoadd);
                    Utils.addToolLore(toolmeta);
                    item.setItemMeta(toolmeta);
                }
                return true;
            } catch (NumberFormatException e) {
                org.bukkit.Bukkit.getLogger().info("Player attempted to set a non-number of upgrades");
            }

        }
        if (command.getName().equalsIgnoreCase("settoolxp")) {
            try {
                int expargument = Integer.parseInt(args[0]);
                int maxexpclamp = 50000;
                int minexpclamp = 0;
                int exptoset = expargument > maxexpclamp ? maxexpclamp : expargument < minexpclamp ? minexpclamp : expargument;
                PlayerInventory playerinventory = p.getInventory();
                ItemStack item = playerinventory.getItemInMainHand();
                ItemMeta toolmeta = item.getItemMeta();
                if(Utils.isTool(item)) {
                    Utils.changeIntTags(toolmeta, "EXP", Optype.SET, exptoset);
                    Utils.addToolLore(toolmeta);
                    item.setItemMeta(toolmeta);
                }
                return true;
            } catch (NumberFormatException e) {
                org.bukkit.Bukkit.getLogger().info("Player attempted to set a non-number of tool XP");
            }
        }
        if (command.getName().equalsIgnoreCase("settoollvl")) {
            try {
                int lvlargument = Integer.parseInt(args[0]);
                int maxlvlclamp = 1000;
                int minlvlclamp = 0;
                int lvltoset = lvlargument > maxlvlclamp ? maxlvlclamp : lvlargument < minlvlclamp ? minlvlclamp : lvlargument;
                PlayerInventory playerinventory = p.getInventory();
                ItemStack item = playerinventory.getItemInMainHand();
                ItemMeta toolmeta = item.getItemMeta();
                if(Utils.isTool(item)) {
                    Utils.changeIntTags(toolmeta, "Level", Optype.SET, lvltoset);
                    Utils.addToolLore(toolmeta);
                    item.setItemMeta(toolmeta);
                }
                return true;
            } catch (NumberFormatException e) {
                org.bukkit.Bukkit.getLogger().info("Player attempted to set a non-number of tool level");
            }
        }

        return true;
    }
}
