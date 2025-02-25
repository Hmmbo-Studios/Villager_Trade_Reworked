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

public class TradeEditorGUI implements Listener {
    private final TradeConfiguration tradeConfig;

    public TradeEditorGUI(TradeConfiguration tradeConfig) {
        this.tradeConfig = tradeConfig;
    }

    public void openTradeGUI(Player player, String profession, String tier) {
        Inventory inv = Bukkit.createInventory(null, 54, ChatColor.BLUE + "Edit Trades: " + profession + " (Tier " + tier + ")");

        List<Map<?, ?>> trades = tradeConfig.getTrades(profession, tier); // ✅ Now this works
        int index = 0;
        for (Map<?, ?> trade : trades) {
            ItemStack tradeItem = new ItemStack(Material.EMERALD);
            ItemMeta meta = tradeItem.getItemMeta();
            meta.setDisplayName(ChatColor.GREEN + "Trade " + (index + 1));
            tradeItem.setItemMeta(meta);
            inv.setItem(index++, tradeItem);
        }

        player.openInventory(inv);
    }

    @EventHandler
    public void onTradeEdit(InventoryClickEvent event) {
        if (event.getView().getTitle().startsWith(ChatColor.BLUE + "Edit Trades")) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem != null && clickedItem.hasItemMeta()) {
                String title = event.getView().getTitle();
                String profession = title.substring(title.indexOf(": ") + 2, title.indexOf(" (Tier "));
                String tier = title.substring(title.indexOf("(Tier ") + 6, title.length() - 1);

                TradeDetailGUI t = new TradeDetailGUI(tradeConfig);
                t.openTradeDetailGUI(player, profession, tier, event.getSlot()); // ✅ Fixed

            }
        }
    }
}
