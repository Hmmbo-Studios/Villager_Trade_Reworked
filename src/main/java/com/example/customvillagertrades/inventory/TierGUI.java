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

public class TierGUI implements Listener {
    private final TradeConfiguration tradeConfig;

    public TierGUI(TradeConfiguration tradeConfig) {
        this.tradeConfig = tradeConfig;
    }

    public void openTierGUI(Player player, String profession) {
        Inventory inv = Bukkit.createInventory(null, 27, ChatColor.AQUA + "Select Tier: " + profession);

        int index = 0;
        for (String tier : tradeConfig.getTiers(profession)) { // ✅ Now this works
            ItemStack item = new ItemStack(Material.BOOK);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD + "Tier " + tier);
            item.setItemMeta(meta);
            inv.setItem(index++, item);
        }

        player.openInventory(inv);
    }

    @EventHandler
    public void onTierSelect(InventoryClickEvent event) {
        if (event.getView().getTitle().startsWith(ChatColor.AQUA + "Select Tier: ")) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem != null && clickedItem.hasItemMeta()) {
                String profession = event.getView().getTitle().replace(ChatColor.AQUA + "Select Tier: ", "");
                String tier = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName()).replace("Tier ", "");

                TradeEditorGUI t = new TradeEditorGUI(tradeConfig);
                t.openTradeGUI(player, profession, tier);
            }
        }
    }
}
