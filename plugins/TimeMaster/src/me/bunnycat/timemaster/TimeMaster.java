package me.bunnycat.timemaster;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.entity.HumanEntity;

import java.util.logging.Logger;

public class TimeMaster extends JavaPlugin implements Listener {
    Logger myPluginLogger = Bukkit.getLogger();

    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }
   
    @EventHandler
    public void onRightClickWatch(PlayerInteractEvent event) {
    	HumanEntity ent = event.getPlayer();
    	if (ent instanceof Player) {
        	Player player = (Player) ent;
	    	if (event.getItem() != null) {
				if (event.getItem().getType() == Material.CLOCK) {
					if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
						//myPluginLogger.info("Right clicked with: " + event.getItem().getType().toString());
						if (player.getLocation().getWorld().getEnvironment() == Environment.NORMAL) {
							event.getPlayer().openInventory(setup_time_inventory());
			        	} else {
							event.setCancelled(true);
							return;
				    	}
					}
				}
	    	}
    	}
    }
    
    @EventHandler 
    public void onInventoryClickWatch(InventoryClickEvent event) {
    	if (event.getCurrentItem() != null) {
    		HumanEntity ent = event.getWhoClicked();
    		if (ent instanceof Player) {
    			Player player = (Player) ent;
    			if(event.getSlot() >= 0) {
    				if (event.isRightClick() && event.getCurrentItem().getType() == Material.CLOCK) {
    					event.setCancelled(true);
    					if (player.getLocation().getWorld().getEnvironment() == Environment.NORMAL) {
    						new BukkitRunnable() {
    							@Override
    							public void run() {
    								player.openInventory(setup_time_inventory());
    							}
    						}.runTaskLater(this, 1);
    					} else {
	    					return;
	    	    		}
        			}
        		}	    	
    		}
    	}
    }
    
    @EventHandler
    public void onTimeMenuClick(InventoryClickEvent event) {
    	//ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();;
    	//String command = "";
		HumanEntity ent = event.getWhoClicked();
    	if (ent instanceof Player) {
        	Player player = (Player) ent;
        	Inventory open_inventory = player.getOpenInventory().getTopInventory();
        	if(event.getSlot() >= 0) {
        		if (event.getView().getTitle() == "Time Menu" && event.getClickedInventory() == open_inventory) {
        			switch( event.getSlot() ) {
        			case 1: player.getLocation().getWorld().setTime(0);
        				break;
        			case 3: player.getLocation().getWorld().setTime(6000);
        				break;
        			case 5: player.getLocation().getWorld().setTime(12000);
        				break;
        			case 7: player.getLocation().getWorld().setTime(18000);
        				break;
        			case 9: {
        				player.getLocation().getWorld().setStorm(false);
        				player.getLocation().getWorld().setThundering(false);
        				break;
        			}
        			case 13: {
        				player.getLocation().getWorld().setStorm(true);
        				player.getLocation().getWorld().setThundering(false);
        				break;
        			}
        			case 17: {
        				player.getLocation().getWorld().setStorm(true);
        				player.getLocation().getWorld().setThundering(true);
        				break;
        			}
        			default: event.setCancelled(true);
        				return;
        			}
        			//Bukkit.dispatchCommand(console, command);
        			event.setCancelled(true);
					new BukkitRunnable() {
						
						   @Override
						   public void run() {
							   player.closeInventory();
						   }
					}.runTaskLater(this, 1);
        		}
        	}
		}
	}
    
    @EventHandler
    public void onTimeMenuDrag(InventoryDragEvent event) {
    	HumanEntity ent = event.getWhoClicked();
    	if (ent instanceof Player) {
        	Player player = (Player) ent;
        	Inventory open_inventory = player.getOpenInventory().getTopInventory();
        	if (event.getView().getTitle() == "Time Menu" && event.getInventory() == open_inventory) {
        		event.setCancelled(true);
        	}
    	}
    }
    
    public Inventory setup_time_inventory() {
		Inventory time_menu_inventory = Bukkit.createInventory(null, 18, "Time Menu");
		//time_menu_inventory.setItem(0, colorname_pane(15, 	" "));
		//time_menu_inventory.setItem(2, colorname_pane(15, 	" "));
		//time_menu_inventory.setItem(4, colorname_pane(15, 	" "));
		//time_menu_inventory.setItem(6, colorname_pane(15, 	" "));
		//time_menu_inventory.setItem(8, colorname_pane(15, 	" "));
		time_menu_inventory.setItem(1, colorname_pane(Material.YELLOW_STAINED_GLASS_PANE, 	"Sunrise"));
		time_menu_inventory.setItem(3, colorname_pane(Material.LIGHT_BLUE_STAINED_GLASS_PANE, 	"Noon"));
		time_menu_inventory.setItem(5, colorname_pane(Material.BLUE_STAINED_GLASS_PANE, 	"Sunset"));
		time_menu_inventory.setItem(7, colorname_pane(Material.BLACK_STAINED_GLASS_PANE, 	"Midnight"));
		time_menu_inventory.setItem(9, colorname_pane(Material.CYAN_STAINED_GLASS_PANE, 	"Clear"));
		time_menu_inventory.setItem(13, colorname_pane(Material.PURPLE_STAINED_GLASS_PANE, 	"Rain"));
		time_menu_inventory.setItem(17, colorname_pane(Material.RED_STAINED_GLASS_PANE, 	"Thunderstorm"));
    	return time_menu_inventory;
    }
    
    public ItemStack colorname_pane (Material mat, String name){
    	ItemStack pane = new ItemStack(mat);
    	ItemMeta pane_meat = pane.getItemMeta();
    	pane_meat.setDisplayName(name);
    	pane.setItemMeta(pane_meat);
    	//pane.setDurability((short)color);
    	return pane;
    }
}