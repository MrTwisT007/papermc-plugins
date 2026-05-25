package me.bunnycat.pocketshulkerbox;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.entity.HumanEntity;

import java.util.Hashtable;
import java.util.UUID;
import java.util.logging.Logger;

public class PocketShulkerBox extends JavaPlugin implements Listener {
    Logger myPluginLogger = Bukkit.getLogger();
    
	public Hashtable<UUID, ItemStack> 			item_hashtable 		= new Hashtable<UUID, ItemStack>();
	
    public void save_shulkerbox_item(Player player, ItemStack item) {
    	item_hashtable.put(player.getUniqueId(), item);
    }

    public ItemStack get_saved_shulkerbox_item(Player player) {
    	return item_hashtable.get(player.getUniqueId());
    }
    
    public void onEnable() {
        //myPluginLogger.info("Pocket Shulker Box Started");
        getServer().getPluginManager().registerEvents(this, this);
    }
   
    //give new players a shulker box to start
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
    	Player p = e.getPlayer();
    	if (!p.hasPlayedBefore()) {
    		ItemStack shulkerboxstack = new ItemStack(Material.BLACK_SHULKER_BOX);
    		BlockStateMeta shulkerboxmeta = (BlockStateMeta) shulkerboxstack.getItemMeta();
    		ShulkerBox shulkerbox = (ShulkerBox) shulkerboxmeta.getBlockState();
    		Inventory shulkerboxinv = shulkerbox.getInventory();
    		shulkerboxinv.addItem(new ItemStack(Material.COMPASS));
    		shulkerboxinv.addItem(new ItemStack(Material.CLOCK));
    		shulkerboxinv.addItem(new ItemStack(Material.ENDER_CHEST));
    		shulkerbox.getInventory().setContents(shulkerboxinv.getContents());
    		shulkerboxmeta.setBlockState(shulkerbox);
    		shulkerboxstack.setItemMeta(shulkerboxmeta);
    		p.getInventory().addItem(shulkerboxstack);
    	}
    }
    
    public boolean already_updated_shulkerbox_inventory;
    
    @EventHandler
    public void onRightClickInventoryItem(InventoryClickEvent event) {
    	
    	HumanEntity ent = event.getWhoClicked();
    	if (ent instanceof Player) {
        	Player player = (Player) ent;
        	if(event.getSlot() >= 0) {
        		if (event.getClickedInventory().getType() == InventoryType.PLAYER) {
        			if(event.isRightClick()) {
        				ItemStack clicked_item = event.getCurrentItem();
        				if(clicked_item != null) {
	        				ItemMeta clicked_item_meta = clicked_item.getItemMeta();
	        				if(clicked_item_meta instanceof BlockStateMeta) {
	        					BlockStateMeta clicked_item_bs_meta = (BlockStateMeta) clicked_item_meta;
	        					if (clicked_item_bs_meta.getBlockState() instanceof ShulkerBox) {
	        						// If a shulker box is already open, first save the contents to it before dealing with another one
	        						if(player.getOpenInventory().getTitle() == "Shulker Box") {
	    								Inventory closing_shulkerbox_inventory = player.getOpenInventory().getTopInventory();
	    								ShulkerBox shulkerbox = (ShulkerBox) ((BlockStateMeta) get_saved_shulkerbox_item(player).getItemMeta()).getBlockState();
	    								shulkerbox.getInventory().setContents(closing_shulkerbox_inventory.getContents());
	    								BlockStateMeta closed_shulker_box_meta = (BlockStateMeta)get_saved_shulkerbox_item(player).getItemMeta();
	    								closed_shulker_box_meta.setBlockState(shulkerbox);
	        							get_saved_shulkerbox_item(player).setItemMeta(closed_shulker_box_meta);
	        							already_updated_shulkerbox_inventory = true;
	        						}
	        						save_shulkerbox_item(player, clicked_item);
	        						ShulkerBox shulkerbox = (ShulkerBox) ((BlockStateMeta) get_saved_shulkerbox_item(player).getItemMeta()).getBlockState();
	        						Inventory shulkerbox_inventory = shulkerbox.getInventory();
	        						Inventory placeholder_inventory = Bukkit.createInventory(null,  27, "Shulker Box");
	        						placeholder_inventory.setContents(shulkerbox_inventory.getContents());
	        						new BukkitRunnable() {
	        							
	        						   @Override
	        						   public void run() {
	        							   player.openInventory(placeholder_inventory);
	        						   }
	        						}.runTaskLater(this, 1);
	        					}
	        				}
        				}
        			}
        		}
        	}
    	}
    }
    
    @EventHandler
    public void onShulkerBoxPlaceholderInventoryCloseEvent(InventoryCloseEvent event) {
    	// Don't want this to run if inventory closed by shulker box switching - box should already be updated
    	HumanEntity ent = event.getPlayer();
    	if (ent instanceof Player) {
        	Player player = (Player) ent;
        	InventoryView closed_inventory = event.getView();
        	Inventory closed_inventory_self = event.getInventory();
    		if(closed_inventory.getTitle() == "Shulker Box") {
    			if (already_updated_shulkerbox_inventory) {
    				already_updated_shulkerbox_inventory = false;
    				return;
    			}
    			ShulkerBox shulkerbox = (ShulkerBox) ((BlockStateMeta) get_saved_shulkerbox_item(player).getItemMeta()).getBlockState();
    			shulkerbox.getInventory().setContents(closed_inventory_self.getContents());
    			BlockStateMeta closed_shulker_box_meta = (BlockStateMeta)get_saved_shulkerbox_item(player).getItemMeta();
    			closed_shulker_box_meta.setBlockState(shulkerbox);
    			get_saved_shulkerbox_item(player).setItemMeta(closed_shulker_box_meta);
    		}
    	}
    }
    
    @EventHandler
    public void onShulkerBoxItemClickEvent(InventoryClickEvent event) {
    	if (event.getCurrentItem() != null) {
    		HumanEntity ent = event.getWhoClicked();
    		if (ent instanceof Player) {
    			Player player = (Player) ent;
    			if(event.getSlot() >= 0) {
    				if(event.isRightClick()) {
    					ItemStack clicked_item = event.getCurrentItem();
    					if(clicked_item != null) {
    						ItemMeta clicked_item_meta = clicked_item.getItemMeta();
    						if(clicked_item_meta instanceof BlockStateMeta) {
    							BlockStateMeta clicked_item_blockstate = (BlockStateMeta) clicked_item_meta;
    							if (clicked_item_blockstate.getBlockState() instanceof ShulkerBox) {
    								event.setCancelled(true);
    							}
    						}
    					}
    				} else if ((event.isLeftClick() || event.getAction() == InventoryAction.SWAP_WITH_CURSOR ) && player.getOpenInventory().getTitle() == "Shulker Box") {
    					// Don't allow swapping any item held in cursor with any shulker box in inventory if a shulker box is already open
    					ItemStack clicked_item = event.getCurrentItem();
    					if (clicked_item != null) {
	    					ItemMeta clicked_item_meta = clicked_item.getItemMeta();
	    					if(clicked_item_meta instanceof BlockStateMeta) {
	    						BlockStateMeta clicked_item_blockstate = (BlockStateMeta) clicked_item_meta;
	    						if (clicked_item_blockstate.getBlockState() instanceof ShulkerBox) {
	    							// The following allows interacting with other shulker boxes in the inventory if uncommented, but will allow putting a shulker inside a shulker.
	    							// if ( clicked_item.equals(get_saved_shulkerbox_item(player)))
	    							event.setCancelled(true);
	    						}
	    					}
    					}
    				}
    			}
    		}
    	}
    }
}