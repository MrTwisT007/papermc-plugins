package me.bunnycat.twoolio;

import com.destroystokyo.paper.MaterialSetTag;
import com.destroystokyo.paper.MaterialTags;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.title.Title;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.lang.reflect.Field;
import java.time.Duration;
import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.Map.Entry;

public class Utils{

    static AllowedTools allowedtoolslist = new AllowedTools();
    private final Map<String, Scoreboard> boardMap = new HashMap<>();
    private final Map<String, Objective> objMap = new HashMap<>();
    private final Map<String, Map.Entry<ItemStack,Integer>> upgrToolMap = new HashMap<>();

    static FileConfiguration configFile = Twoolio.getConfigManager().getConfig();

    public static NamespacedKey keyFromString(String string) {
        return new NamespacedKey(Twoolio.plugin, string);
    }

    public static Integer getIntfromPDC(ItemMeta meta, String key) {
        return meta.getPersistentDataContainer().get(keyFromString(key), PersistentDataType.INTEGER);
    }

    public static String getStringfromPDC(ItemMeta meta, String key) {
        return meta.getPersistentDataContainer().get(keyFromString(key), PersistentDataType.STRING);
    }

    public static void showTitleWrapper(Player p, String title, String subtitle, int duration) {
        int fadein=1,fadeout=1;
        p.showTitle(Title.title(Component.text(title),
                Component.text(subtitle),
                Title.Times.times(	Duration.ofSeconds(fadein),
                        Duration.ofSeconds(duration),
                        Duration.ofSeconds(fadeout)
                )
        ));
    }

    public void makeScoreboard(Player p) {
        if (!boardMap.containsKey(p.getName()))
            boardMap.put(p.getName(),Bukkit.getScoreboardManager().getNewScoreboard());
        p.setScoreboard(boardMap.get(p.getName()));
    }

    public void updateScoreboard(Player p, ItemStack item) {
        //String levelString = configFile.getString("loreStrings.level");
        //String pointString = configFile.getString("loreStrings.points");
        String upgradeString = configFile.getString("loreStrings.upgrades");
        Integer itemExp = getIntfromPDC(item.getItemMeta(), "EXP");
        Integer itemLevel = getIntfromPDC(item.getItemMeta(), "Level");
        Integer itemLevelup = getIntfromPDC(item.getItemMeta(), "Levelup");
        Integer itemUpgrades = getIntfromPDC(item.getItemMeta(), "Upgrades");
        Scoreboard board = boardMap.get(p.getName());
        if (!objMap.containsKey(p.getName()))
            objMap.put(p.getName(), board.registerNewObjective("Score", Criteria.DUMMY, item.displayName()));
        Objective obj = objMap.get(p.getName());

        Component itemNameAndXP = Component.text(StringUtils.substringBetween(serializeItemName(item),"[", "]") +
                " Lvl" +
                itemLevel.toString() +
                " " +
                itemExp.toString() +
                "/" +
                itemLevelup.toString());
        obj.displayName(itemNameAndXP);

        //obj.getScore(pointString).setScore(itemExp);
        //obj.getScore(levelString).setScore(itemLevel);
        obj.getScore(upgradeString).setScore(itemUpgrades);
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        p.setScoreboard(board);
    }

    public String serializeItemName(ItemStack item) {
        return PlainTextComponentSerializer.plainText().serialize(item.displayName());
    }

    public void clearScoreboard(Player p) {
        boardMap.put(p.getName(),Bukkit.getScoreboardManager().getNewScoreboard());
        objMap.remove(p.getName());
        p.setScoreboard(boardMap.get(p.getName()));
    }

    public static void showActionText(Player p, String text) {
        p.sendActionBar(Component.text(text));
    }

    public static boolean isTool(ItemStack item) {
        if (item == null) return false;
        return allowedtoolslist.getTools().contains(item.getType());
    }

    public static boolean isSword(ItemStack item) {
        if (item == null) return false;
        return MaterialTags.SWORDS.getValues().contains(item.getType());
    }

    // Make unbreakable tool and setup tags
    public static void registerNewTool(ItemStack tool) {
        ItemMeta toolmeta = tool.getItemMeta();
        // Don't make the tool item in the upgrade menu unbreakable, it's not real anyway
        if (toolmeta.hasDisplayName() && PlainTextComponentSerializer.plainText().serialize(toolmeta.displayName()).equals("Upgrade to next tier")) {
        } else {
            // If tool is not unbreakable - it doesn't have our tags
            //if (!toolmeta.isUnbreakable()) {
            if(!checkRegistrationStatus(toolmeta)) {
                changeBoolTags(toolmeta, "registered", true);
                if(configFile.getBoolean("toolConfig.setUnbreakable")) toolmeta.setUnbreakable(true);
                Integer exptoNextLevel = configFile.getInt("expConfig.tools.expPerLevel.1");
                changeIntTags(toolmeta, "EXP", 	Optype.SET, 0);
                changeIntTags(toolmeta, "Level", 	Optype.SET, 1);
                changeIntTags(toolmeta, "Upgrades", Optype.SET, 0);
                changeIntTags(toolmeta, "Levelup", 	Optype.SET, exptoNextLevel);
                addToolLore(toolmeta);
                tool.setItemMeta(toolmeta);
            }
        }
    }

    // Operate on tag values
    public static void operateOnTool(Player p, ItemStack tool, String keyname, Optype optype, int amount) {
        ItemMeta toolmeta = tool.getItemMeta();
        changeIntTags(toolmeta, keyname, optype, amount);
        checkForLevelup(p, toolmeta);
        addToolLore(toolmeta);
        tool.setItemMeta(toolmeta);
    }

    public static void checkForLevelup(Player p, ItemMeta toolmeta) {
        Integer exp 	= getIntfromPDC(toolmeta, "EXP");
        Integer level 	= getIntfromPDC(toolmeta, "Level");
        Integer expToLvlUp  = configFile.getConfigurationSection("expConfig.tools.expPerLevel").getInt(level.toString());
        Integer expToLvlNxt = configFile.getConfigurationSection("expConfig.tools.expPerLevel").getInt((++level).toString());
        if (expToLvlUp == 0 || expToLvlNxt == 0) {
            expToLvlUp  = getIntfromPDC(toolmeta,"Levelup");
            expToLvlNxt = expToLvlUp;
        }
        if (exp >= expToLvlUp) {
            changeIntTags(toolmeta, "EXP", 	Optype.DECREMENT, expToLvlUp);
            changeIntTags(toolmeta, "Level", 	Optype.INCREMENT, 1);
            changeIntTags(toolmeta, "Upgrades", Optype.INCREMENT, 1);
            changeIntTags(toolmeta, "Levelup", 	Optype.SET, expToLvlNxt);
            showActionText(p, "Tool leveled up! Right click on tool in inventory to upgrade!");
            Location l = p.getLocation();
            l.add(0, 1, 0);
            p.getWorld().playEffect(l,Effect.valueOf(configFile.getString("toolLevelUpEffects.effect")), 1);
            p.playSound(l, Sound.valueOf(configFile.getString("toolLevelUpEffects.sound")), configFile.getInt("toolLevelUpEffects.volume"), configFile.getInt("toolLevelUpEffects.pitch"));
        }
    }


    public static void changeIntTags(ItemMeta itemmeta, String keystring, Optype optype, int amount) {
        NamespacedKey key = keyFromString(keystring);
        PersistentDataContainer tooltags = itemmeta.getPersistentDataContainer();
        if ((!tooltags.has(key, PersistentDataType.INTEGER)))
            tooltags.set(key, PersistentDataType.INTEGER, amount);
        int tagvalue = tooltags.get(key, PersistentDataType.INTEGER);
        switch(optype) {
            case INCREMENT:
                tagvalue = tagvalue + amount;
                break;
            case DECREMENT:
                tagvalue = tagvalue - amount;
                break;
            case RESET:
                tagvalue = 0;
                break;
            case SET:
                tagvalue = amount;
                break;
            case NONE:
                return;
        }
        tooltags.set(key, PersistentDataType.INTEGER, tagvalue);
    }

    public static void changeStringTags(ItemMeta itemmeta, String keystring, String value) {
        NamespacedKey key = keyFromString(keystring);
        PersistentDataContainer tooltags = itemmeta.getPersistentDataContainer();
        tooltags.set(key, PersistentDataType.STRING, value);
    }

    public static void changeBoolTags(ItemMeta itemmeta, String keystring, Boolean value) {
        NamespacedKey key = keyFromString(keystring);
        PersistentDataContainer tooltags = itemmeta.getPersistentDataContainer();
        tooltags.set(key, PersistentDataType.BOOLEAN, value);
    }

    public static boolean checkRegistrationStatus(ItemMeta itemmeta) {
        PersistentDataContainer tooltags = itemmeta.getPersistentDataContainer();
        return (tooltags.has(keyFromString("registered")));
    }

    public static void addToolLore(ItemMeta toolmeta) {
        String levelString = configFile.getString("loreStrings.level");
        String expString = configFile.getString("loreStrings.exp");
        String upgradeString = configFile.getString("loreStrings.upgrades");

        List<Component> toollore = new ArrayList<>();
        String expval   = getIntfromPDC(toolmeta, "EXP").toString();
        String level 	= getIntfromPDC(toolmeta, "Level").toString();
        String upgrades = getIntfromPDC(toolmeta, "Upgrades").toString();
        String levelup 	= getIntfromPDC(toolmeta, "Levelup").toString();

        toollore.add(Component.text(expString + expval + "/" + levelup));
        toollore.add(Component.text(levelString + level));
        toollore.add(Component.text(upgradeString + upgrades));

        toolmeta.lore(toollore);
    }

    public static void addUpgradeLore(ItemMeta upgrmeta, String pdckey, String lorestring) {
        //Get value of tag under pdckey and set lore string to be "lorestring + pdcvalue"
        List<Component> upgrlore = new ArrayList<>();
        Integer pdcvalue = getIntfromPDC(upgrmeta, pdckey);
        upgrlore.add(Component.text(lorestring + pdcvalue.toString()));
        upgrmeta.lore(upgrlore);
    }

    public static boolean checkValidInventory(Inventory inv) {
        return ((inv.getType() == InventoryType.CHEST) ||
                (inv.getType() == InventoryType.BARREL)||
                (inv.getType() == InventoryType.SHULKER_BOX) ||
                (inv.getType() == InventoryType.PLAYER));
    }

    public void doUpgradeMenu(Player p, ItemStack tool, Integer toolslot) throws NoSuchFieldException, IllegalAccessException {
        //Remember this tool
        SimpleEntry<ItemStack, Integer> toolentry = new SimpleEntry<>(tool, toolslot);
        upgrToolMap.put(p.getName(),toolentry);
        Set<String> toolcategoryset = configFile.getConfigurationSection("toolEnchantments").getKeys(false);

        for (String toolcategory : toolcategoryset) {
            // Get tag by reflection from string and check isTagged against the tool.
            Field MaterialTagsField = MaterialTags.class.getField(toolcategory);
            MaterialSetTag value = (MaterialSetTag) MaterialTagsField.get(new MaterialTags());
            if (value.isTagged(tool)) {
                Inventory upgradeinventory = Bukkit.createInventory(null,  9, Component.text("Tool Upgrades"));

                if(configFile.contains("toolUpgradePaths."+tool.getType().toString())) {
                    Map<String, Object> upgrtoolpathandcost = configFile.getConfigurationSection("toolUpgradePaths." + tool.getType().toString()).getValues(false);

                    String upgrtoolname = (String) upgrtoolpathandcost.get("upgrname");
                    int upgrtoolcost = (int) upgrtoolpathandcost.get("cost");

                    if (upgrtoolname != null) {
                        ItemStack upgradetool = new ItemStack(Material.valueOf(upgrtoolname));
                        ItemMeta upgrtoolmeta = upgradetool.getItemMeta();
                        upgrtoolmeta.displayName(Component.text("Upgrade to next tier"));
                        changeIntTags(upgrtoolmeta, "cost", Optype.SET, upgrtoolcost);
                        addUpgradeLore(upgrtoolmeta, "cost", configFile.getString("loreStrings.cost"));
                        upgradetool.setItemMeta(upgrtoolmeta);
                        upgradeinventory.setItem(8, upgradetool);
                    }
                }

                ItemStack upgradebook = new ItemStack(Material.ENCHANTED_BOOK);
                ItemMeta upgrbookmeta = upgradebook.getItemMeta();

                ConfigurationSection toolenchantments = configFile.getConfigurationSection("toolEnchantments."+toolcategory);

                for(String enchantment : toolenchantments.getKeys(false))
                {
                    Map toolenchantmentmap = toolenchantments.getConfigurationSection(enchantment).getValues(false);
                    Enchantment upgrmenuitem = getEnchfromString(enchantment);
                    Integer currtoolenchlvl = tool.getEnchantmentLevel(getEnchfromString(enchantment)); //returns 0 if enchantment doesn't exist on tool, can use this to decide if menu should have this enchantment
                    int maxenchlvl = (int)toolenchantmentmap.get("maxlvl");
                    int enchcost = (int)toolenchantmentmap.get("cost");
                    if (currtoolenchlvl < maxenchlvl) {
                        upgrbookmeta.displayName(upgrmenuitem.displayName(currtoolenchlvl+1));
                        changeStringTags(upgrbookmeta, "enchkey", enchantment);
                        changeIntTags(upgrbookmeta, "enchlvl", Optype.SET, currtoolenchlvl+1);
                        changeIntTags(upgrbookmeta, "cost", Optype.SET, enchcost);
                        addUpgradeLore(upgrbookmeta, "cost", configFile.getString("loreStrings.cost"));
                        upgradebook.setItemMeta(upgrbookmeta);
                        upgradeinventory.addItem(upgradebook);
                    }

                } // delay so we don't get phantom items when we open inventory later
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        p.openInventory(upgradeinventory);
                    }
                }.runTaskLater(Twoolio.plugin, 1);
            }
        }
    }

    public void doUpgradeTool(Player p, ItemStack upgritem) {
        ItemStack tool = upgrToolMap.get(p.getName()).getKey();
        Integer toolslot = upgrToolMap.get(p.getName()).getValue();
        ItemMeta toolmeta = tool.getItemMeta();
        if(getIntfromPDC(toolmeta, "Upgrades") < getIntfromPDC(upgritem.getItemMeta(), "cost")) {
            showTitleWrapper(p, "Keep Leveling!", "Not enough upgrade points!", 1);
        } else {
            if (upgritem != null) {
                changeIntTags(toolmeta, "Upgrades", Optype.DECREMENT, getIntfromPDC(upgritem.getItemMeta(), "cost"));
                switch (upgritem.getType()) {
                    case ENCHANTED_BOOK:
                        toolmeta.addEnchant(getEnchfromString(getStringfromPDC(upgritem.getItemMeta(), "enchkey")), getIntfromPDC(upgritem.getItemMeta(), "enchlvl"), true);
                        addToolLore(toolmeta);
                        tool.setItemMeta(toolmeta);
                        break;
                    default:
                        // if the clicked item is a tier upgrade it's represented by a tool itemstack
                        if (allowedtoolslist.getTools().contains(upgritem.getType())) {
                            ItemStack newtiertool = new ItemStack(upgritem.getType());
                            addToolLore(toolmeta);
                            newtiertool.setItemMeta(toolmeta);
                            p.getInventory().setItem(toolslot, newtiertool);
                        }
                        break;
                }
            }
        }
        //If holding the tool in hand, update the scoreboard to reflect the change in upgradepoints, but don't change the scoreboard if holding any other item
        new BukkitRunnable() {
            @Override
            public void run() {
                if(Utils.isTool(p.getInventory().getItemInMainHand())){
                    updateScoreboard(p,p.getInventory().getItemInMainHand());
                }
            }
        }.runTaskLater(Twoolio.plugin, 1);
        //tool.addUnsafeEnchantment(Enchantment.getByKey(NamespacedKey.minecraft(enchantment.getValue().toString())), 1); // will need to use unsafe to do illegal combinations
    }

    public static Enchantment getEnchfromString(String enchantmentstring) {
        return Enchantment.getByKey(NamespacedKey.minecraft(enchantmentstring));
    }

    public static Enchantment getEnchfromEntry(Entry<String,Object> enchantmententry) {
        return Enchantment.getByKey(NamespacedKey.minecraft(enchantmententry.getKey()));
    }
}
