package com.example.customvillagertrades;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.ItemStack;

public class MenuListener implements Listener {
    private final CustomVillagerTrades plugin;

    public MenuListener(CustomVillagerTrades plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        if (event.getView().getTitle().equals("Villager Trade Configuration")) {
            event.setCancelled(true);
            
            if (event.getSlotType() != SlotType.CONTAINER) return;
            if (event.getCurrentItem() == null) return;
            
            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();
            
            if (clickedItem.hasItemMeta() && clickedItem.getItemMeta().hasDisplayName()) {
                String displayName = clickedItem.getItemMeta().getDisplayName();
                String professionName = displayName.substring(2); // Remove color code
                
                // Handle profession selection
                try {
                    org.bukkit.entity.Villager.Profession profession = 
                        org.bukkit.entity.Villager.Profession.valueOf(professionName);
                    
                    // Open trade configuration for this profession
                    openTradeConfiguration(player, profession);
                } catch (IllegalArgumentException e) {
                    player.sendMessage("§cInvalid profession selected!");
                }
            }
        }
    }
    
    private void openTradeConfiguration(Player player, org.bukkit.entity.Villager.Profession profession) {
        // TODO: Implement trade configuration menu for specific profession
        player.sendMessage("§aOpening trade configuration for " + profession.name());
    }
}