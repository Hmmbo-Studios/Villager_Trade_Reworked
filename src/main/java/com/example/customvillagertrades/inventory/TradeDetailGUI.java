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

import java.util.List;
import java.util.Map;

public class TradeDetailGUI implements Listener {
    private final TradeConfiguration tradeConfig;

    public TradeDetailGUI(TradeConfiguration tradeConfig) {
        this.tradeConfig = tradeConfig;
    }

    public void openTradeDetailGUI(Player player, String profession, String tier, int tradeIndex) {
        Inventory inv = Bukkit.createInventory(null, 27, ChatColor.RED + "Edit Trade " + (tradeIndex + 1));

        // Buttons for editing trade details
        inv.setItem(11, createMenuItem(Material.EMERALD, ChatColor.YELLOW + "Edit Price"));
        inv.setItem(13, createMenuItem(Material.ANVIL, ChatColor.GREEN + "Edit Result"));
        inv.setItem(15, createMenuItem(Material.BARRIER, ChatColor.RED + "Delete Trade"));

        player.openInventory(inv);
    }

    private ItemStack createMenuItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }
        return item;
    }

    @EventHandler
    public void onTradeDetailEdit(InventoryClickEvent event) {
        if (event.getView().getTitle().startsWith(ChatColor.RED + "Edit Trade")) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem == null || !clickedItem.hasItemMeta()) return;

            String title = event.getView().getTitle();
            int tradeIndex = Integer.parseInt(title.replace(ChatColor.RED + "Edit Trade ", "")) - 1;

            // Extract profession and tier from player's previous actions
            String profession = "unknown";
            String tier = "1"; // Default, you should track previous selections per player

            if (clickedItem.getType() == Material.EMERALD) {
                player.closeInventory();
                player.sendMessage(ChatColor.YELLOW + "Type the new price in chat...");
                ChatInputHandler.startEditing(player, profession, tier, tradeIndex, "price", tradeConfig);
            } else if (clickedItem.getType() == Material.ANVIL) {
                player.closeInventory();
                player.sendMessage(ChatColor.GREEN + "Type the new result item in chat...");
                ChatInputHandler.startEditing(player, profession, tier, tradeIndex, "result", tradeConfig);
            } else if (clickedItem.getType() == Material.BARRIER) {
                tradeConfig.removeTrade(profession, tier, tradeIndex);
                player.sendMessage(ChatColor.RED + "Trade removed!");
                player.closeInventory();
            }
        }
    }
}
