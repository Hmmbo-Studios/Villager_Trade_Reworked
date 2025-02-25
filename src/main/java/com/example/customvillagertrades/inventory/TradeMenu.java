package com.example.customvillagertrades.inventory;

import com.example.customvillagertrades.CustomVillagerTrades;
import com.example.customvillagertrades.TradeConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class TradeMenu {
    private final CustomVillagerTrades plugin;
    private final Inventory inventory;
    private final TradeConfiguration tradeConfig;

    public TradeMenu(CustomVillagerTrades plugin) {
        this.plugin = plugin;
        this.tradeConfig = new TradeConfiguration(plugin);
        this.inventory = Bukkit.createInventory(null, 54, "Villager Trade Configuration");
        initializeItems();
    }

    private void initializeItems() {
        
        for (Profession profession : Profession.values()) {
            if (profession != Profession.NONE) { 
                ItemStack item = createProfessionItem(profession);
                inventory.addItem(item);
            }
        }
    }

    private ItemStack createProfessionItem(Profession profession) {
        Material material;
        switch (profession) {
            case FARMER:
                material = Material.HAY_BLOCK;
                break;
            case LIBRARIAN:
                material = Material.BOOKSHELF;
                break;

            case ARMORER:
                material = Material.IRON_CHESTPLATE;
                break;
            case BUTCHER:
                material = Material.BEEF;
                break;
            case CLERIC:
                material = Material.BREWING_STAND;
                break;
            case FISHERMAN:
                material = Material.FISHING_ROD;
                break;
            case FLETCHER:
                material = Material.BOW;
                break;
            case LEATHERWORKER:
                material = Material.LEATHER;
                break;
            case MASON:
                material = Material.STONECUTTER;
                break;
            case SHEPHERD:
                material = Material.WHITE_WOOL;
                break;
            case TOOLSMITH:
                material = Material.IRON_PICKAXE;
                break;
            case WEAPONSMITH:
                material = Material.IRON_SWORD;
                break;
            case CARTOGRAPHER:
                material = Material.CARTOGRAPHY_TABLE;
                break;
            case NITWIT:
                material = Material.BARRIER;
                break;
            default:
                material = Material.VILLAGER_SPAWN_EGG;
        }

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§6" + profession.name());
            meta.setLore(Arrays.asList(
                "§7Click to view trades for this profession",
                "§7Right-click to edit trades"
            ));
            item.setItemMeta(meta);
        }
        return item;
    }

    public void openMenu(Player player) {
        player.openInventory(inventory);
    }
}