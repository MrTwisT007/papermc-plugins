package me.bunnycat.twoolio;

import java.io.Serial;
import java.util.ArrayList;

import org.bukkit.Material;

public class AllowedTools {
    private final ArrayList<Material> allowedtools;

    public AllowedTools() {
        allowedtools = new ArrayList<Material>() {
            @Serial
            private static final long serialVersionUID = 1L;

            {
                this.add(Material.WOODEN_SWORD);
                this.add(Material.WOODEN_PICKAXE);
                this.add(Material.WOODEN_AXE);
                this.add(Material.WOODEN_SHOVEL);
                this.add(Material.WOODEN_HOE);
                this.add(Material.STONE_SWORD);
                this.add(Material.STONE_PICKAXE);
                this.add(Material.STONE_AXE);
                this.add(Material.STONE_SHOVEL);
                this.add(Material.STONE_HOE);
                this.add(Material.IRON_SWORD);
                this.add(Material.IRON_PICKAXE);
                this.add(Material.IRON_AXE);
                this.add(Material.IRON_SHOVEL);
                this.add(Material.IRON_HOE);
                this.add(Material.GOLDEN_SWORD);
                this.add(Material.GOLDEN_PICKAXE);
                this.add(Material.GOLDEN_AXE);
                this.add(Material.GOLDEN_SHOVEL);
                this.add(Material.GOLDEN_HOE);
                this.add(Material.DIAMOND_SWORD);
                this.add(Material.DIAMOND_PICKAXE);
                this.add(Material.DIAMOND_AXE);
                this.add(Material.DIAMOND_SHOVEL);
                this.add(Material.DIAMOND_HOE);
                this.add(Material.NETHERITE_SWORD);
                this.add(Material.NETHERITE_PICKAXE);
                this.add(Material.NETHERITE_AXE);
                this.add(Material.NETHERITE_SHOVEL);
                this.add(Material.NETHERITE_HOE);
            }
        };
    }

    public ArrayList<Material> getTools(){
        return allowedtools;
    }
}
