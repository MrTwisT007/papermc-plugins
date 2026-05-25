package me.bunnycat.anvilpenaltyforgiver;
import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
//import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
//import org.bukkit.event.block.Action;
//import org.bukkit.event.player.PlayerInteractEvent;
//import org.bukkit.event.inventory.InventoryClickEvent;
//import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.Inventory;
//import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.logging.Logger;

//public class PenaltyModifier extends JavaPlugin {
	// Fired when plugin is first enabled
//    @Override
//    public void onEnable() {
//    }
    // Fired when plugin is disabled
//    @Override
//    public void onDisable() {

//    }
//}

 
public class PenaltyModifier extends JavaPlugin implements Listener {
    Logger myPluginLogger = Bukkit.getLogger();
    FileConfiguration config = this.getConfig();
 
    public void onEnable() {
        myPluginLogger.info("Anvil Penalty Modifier Started");
        getServer().getPluginManager().registerEvents(this, this);
        
        config.addDefault("Forgiveness threshold", 10);
        config.options().copyDefaults(true);
        saveConfig();
    }
 
/*    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
    	myPluginLogger.info("Clicked inventory");
    	HumanEntity ent = event.getWhoClicked();

    	if(ent instanceof Player){
    		Player player = (Player)ent;

            if(event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY || 
            		event.getAction() == InventoryAction.SWAP_WITH_CURSOR ) {
            	Inventory inv = player.getOpenInventory().getTopInventory();
            //} else {
            //	Inventory inv = player.getOpenInventory().getTopInventory();
            //}
    		
    		if(inv instanceof AnvilInventory){
    			myPluginLogger.info("Within Anvil Inventory");
    			AnvilInventory anvil = (AnvilInventory)inv;
    			ItemStack[] items = anvil.getContents();
    			int item_count = 0;
    			for(ItemStack item : items) {
    				if(item != null){
    					ItemMeta meta = item.getItemMeta();
    					if (meta instanceof Repairable ) {
    						Repairable r = (Repairable) meta;
    						int count = r.getRepairCost();
    						myPluginLogger.info("Item " + item_count + " repair cost is " + count);
    						if(count > 10) {
    							player.sendMessage(ChatColor.RED + "Your repair penalties are forgiven!");
    							r.setRepairCost(0);
    						}
    						item.setItemMeta(meta);
    					}
    				} else {
    					myPluginLogger.info("NULL Item " + item_count);
    				}
    				item_count++;
    			}
    		}
            }
    	}
    }*/
    
    @EventHandler
    public void onAnvilItemTransfer(PrepareAnvilEvent event) {
    	//myPluginLogger.info("Anvil Prepared");
    	
    	HumanEntity ent = event.getView().getPlayer();
    	Inventory inv = event.getInventory();
    	
    	if(ent instanceof Player){
    		Player player = (Player)ent;
    		
    		if(inv instanceof AnvilInventory){
    			
    			AnvilInventory anvil = (AnvilInventory)inv;
    			ItemStack[] items = anvil.getContents();
    			//int item_count = 0;
    			
    			for(ItemStack item : items) {
    				if(item != null){
    					ItemMeta meta = item.getItemMeta();
    					if (meta instanceof Repairable ) {
    						Repairable r = (Repairable) meta;
    						int count = r.getRepairCost();
    						//myPluginLogger.info("Item " + item_count + " repair cost is " + count);
    						int threshold = config.getInt("Forgiveness threshold");
    						if(count > threshold) {
    							player.sendMessage(ChatColor.RED + "Your repair penalties are forgiven!");
    							r.setRepairCost(0);
    						}
    						item.setItemMeta(meta);
    					}
    				} else {
    					//myPluginLogger.info("NULL Item " + item_count);
    				}
    				//item_count++;
    			}
    		}
		}
    }
}