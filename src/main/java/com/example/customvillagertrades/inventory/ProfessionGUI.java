package com.example.customvillagertrades.inventory;

import com.example.customvillagertrades.TradeConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ProfessionGUI implements Listener {

    private final TradeConfiguration tradeConfig;

    public ProfessionGUI(TradeConfiguration tradeConfig) {
        this.tradeConfig = tradeConfig;
    }

    public void openProfessionGUI(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, ChatColor.GREEN + "Select Profession");

        int index = 0;
        for (String profession : tradeConfig.getProfessions()) {
            ItemStack item = new ItemStack(Material.VILLAGER_SPAWN_EGG);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.YELLOW + profession);
            item.setItemMeta(meta);
            inv.setItem(index++, item);
        }

        player.openInventory(inv);
    }

    @EventHandler
    public void onProfessionSelect(InventoryClickEvent event) {
        if (event.getView().getTitle().equalsIgnoreCase(ChatColor.GREEN + "Select Profession")) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem != null && clickedItem.hasItemMeta()) {
                String profession = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());
                TierGUI t = new TierGUI(tradeConfig);
                t.openTierGUI(player, profession);
            }
        }
    }
}
