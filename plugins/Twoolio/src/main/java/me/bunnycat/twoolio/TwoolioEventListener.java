package me.bunnycat.twoolio;

import com.destroystokyo.paper.MaterialTags;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class TwoolioEventListener implements Listener {

    Utils utils = new Utils();
    static FileConfiguration configFile = Twoolio.getConfigManager().getConfig();

    @EventHandler
    public void onFindToolsInInventories(InventoryOpenEvent e) {
        InventoryView openedview = e.getView();
        Inventory topinventory = openedview.getTopInventory();
        Inventory playerinventory = openedview.getBottomInventory();
        if (Utils.checkValidInventory(topinventory)) {
            for (var item : topinventory.getContents())
            {
                if (item == null) continue;
                if(Utils.isTool(item)) Utils.registerNewTool(item);
            }

        }
        for (var item : playerinventory.getContents())
        {
            if (item == null) continue;
            //Bukkit.getLogger().info("Checkingbottom!");
            if(Utils.isTool(item)) Utils.registerNewTool(item);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        utils.makeScoreboard(e.getPlayer());
        ItemStack iteminhand = e.getPlayer().getInventory().getItemInMainHand();
        if (Utils.isTool(iteminhand)) utils.updateScoreboard(e.getPlayer(), iteminhand);
    }

    @EventHandler
    public void onCraftAnyTool(CraftItemEvent e) {
        ItemStack item = e.getCurrentItem();
        if(Utils.isTool(item)) Utils.registerNewTool(item);
    }

    @EventHandler
    public void onPickupAnyTool(EntityPickupItemEvent e) {
        if (e.getEntityType() != EntityType.PLAYER) return;
        ItemStack item = e.getItem().getItemStack();
        if(Utils.isTool(item)) Utils.registerNewTool(item);
    }

    @EventHandler
    public void onClickAnyTool(InventoryClickEvent e) {
        ItemStack item;
        if (e.isShiftClick()){
            item = e.getCurrentItem();
        } else {
            item = e.getCursor();
        }
        if(Utils.isTool(item)) Utils.registerNewTool(item);
    }

    @EventHandler
    public void onBreakAnyBlock(BlockBreakEvent e) {
        //DEBUG: stop breaking blocks for now
        if(configFile.getBoolean("debug.unbreakableBlocks")) {
            e.setCancelled(true);
        }
        //Check if sword leveling is allowed on block breaks
        if (Utils.isSword(e.getPlayer().getInventory().getItemInMainHand())) {
            if(!configFile.getBoolean("expConfig.sword.allowEXPGainOnBlockBreak")) {
                return;
            }
        }
        ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
        Block brokenblock = e.getBlock();
        double blockbreakspeed = Math.ceil(1/brokenblock.getBreakSpeed(e.getPlayer()));
        double expblockbreakgainscalar = configFile.getDouble("expConfig.tools.expBlockBreakGainScalar");
        int blockbreakexp = (int) (blockbreakspeed * expblockbreakgainscalar);
        int expcap = configFile.getInt("expConfig.tools.expCapPerGain");
        int exptogive = Math.min(blockbreakexp, expcap);

        if(configFile.getBoolean("debug.printEXPGainValue")){
            org.bukkit.Bukkit.getLogger().info("EXP gained: " + exptogive + " Pre Scalar/Cap: " + blockbreakspeed);
        }

        if(Utils.isTool(item)) {
            Utils.operateOnTool(e.getPlayer(), item, "EXP", Optype.INCREMENT, exptogive);
            utils.updateScoreboard(e.getPlayer(), item);
        }
    }

    @EventHandler
    public void onSwitchHotbar(PlayerItemHeldEvent e) {
        ItemStack item = e.getPlayer().getInventory().getItem(e.getNewSlot());
        if (Utils.isTool(item)) {
            utils.updateScoreboard(e.getPlayer(), item);
        } else {
            utils.clearScoreboard(e.getPlayer());
        }
    }

    @EventHandler
    public void onSwitchHands(PlayerSwapHandItemsEvent e) {
        ItemStack item = e.getMainHandItem();
        if (Utils.isTool(item)) {
            utils.updateScoreboard(e.getPlayer(), item);
        } else {
            utils.clearScoreboard(e.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerAttackWithSword(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player) {
            Player p = (Player) e.getDamager();
            ItemStack item = p.getInventory().getItemInMainHand();
            double damage = e.getDamage();
            double expdamagedealgainscalar = configFile.getDouble("expConfig.sword.expDamageDealGainScalar");
            int damageexp = (int)(damage * expdamagedealgainscalar);
            int expcap = configFile.getInt("expConfig.sword.expCapPerGain");
            int exptogive = Math.min(damageexp, expcap);

            if(configFile.getBoolean("debug.printEXPGainValue")){
                org.bukkit.Bukkit.getLogger().info("EXP gained: " + exptogive + " Pre Scalar/Cap: " + damage);
            }

            if (Utils.isSword(item)) {
                Utils.operateOnTool(p, p.getInventory().getItemInMainHand(), "EXP", Optype.INCREMENT, exptogive);
                utils.updateScoreboard(p, item);
            }
        }
    }

    @EventHandler
    public void onTillwithHoe(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player p = e.getPlayer();
            Block clickedBlock = e.getClickedBlock();
            BlockFace blockFace = e.getBlockFace();
            Material blockType = clickedBlock.getType();
            Location blockLocation = clickedBlock.getLocation();
            Location blockAbove = blockLocation.add(0, 1, 0);
            if (MaterialTags.HOES.getValues().contains(p.getInventory().getItemInMainHand().getType())) {
                if (blockType == Material.DIRT || blockType == Material.DIRT_PATH || blockType == Material.GRASS_BLOCK)
                    if (blockFace == BlockFace.UP) {
                        if (blockAbove.getBlock().getType() != Material.WATER) {
                            Utils.operateOnTool(p, p.getInventory().getItemInMainHand(), "EXP", Optype.INCREMENT, 2);
                            utils.updateScoreboard(p, p.getInventory().getItemInMainHand());
                        }
                    } else {
                        e.setCancelled(true);
                    }
            }
        }
    }

    @EventHandler
    public void onRightClickTool(InventoryClickEvent e) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        Player p = (Player)e.getView().getPlayer();
        if (e.isRightClick()) {
            ItemStack item = e.getCurrentItem();
            Integer itemslot = e.getSlot();
            if (Utils.isTool(item)) {
                e.setCancelled(true);
                Inventory inv = e.getClickedInventory();
                // Probably best to restrict this to player inventory only so we don't lose context if the upgrade menu invocation happens in a top inventory
                if (inv.getType() == InventoryType.PLAYER) {
                    utils.doUpgradeMenu(p, item, itemslot);
                }
            }
        }
    }

    @EventHandler
    public void onClickUpgradeItem(InventoryClickEvent e) {
        Player p = (Player)e.getView().getPlayer();
        ItemStack upgritem = e.getCurrentItem();
        InventoryView openedview = e.getView();
        String invtitle = PlainTextComponentSerializer.plainText().serialize(openedview.title());
        if (invtitle.equals("Tool Upgrades")) {
            if (e.getClickedInventory() == p.getOpenInventory().getTopInventory()){
                // If clicked on empty slot - just back out
                if (e.getCurrentItem() == null) {
                    e.setCancelled(true);
                    return;
                }

                utils.doUpgradeTool(p, upgritem);

                e.setCancelled(true);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        p.closeInventory();
                    }
                }.runTaskLater(Twoolio.plugin, 1);
            } else if (e.getClickedInventory() == p.getOpenInventory().getBottomInventory()) {
                e.setCancelled(true);
            }
        }
    }

    //Prevent the player from moving items from the upgrade inventory
    @EventHandler
    public void onUpgradeMenuDrag(InventoryDragEvent e) {
        if (e.getWhoClicked() instanceof Player p) {
            if (PlainTextComponentSerializer.plainText().serialize(e.getView().title()) == "Tool Upgrades" && e.getInventory() == p.getOpenInventory().getTopInventory()) {
                e.setCancelled(true);
            }
        }
    }
}
