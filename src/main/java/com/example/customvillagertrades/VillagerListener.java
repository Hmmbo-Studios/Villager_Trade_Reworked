package com.example.customvillagertrades;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityTransformEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

import java.util.*;

public class VillagerListener implements Listener {
    private final CustomVillagerTrades plugin;
    private final TradeConfiguration tradeConfig;
    private final Random random;

    public VillagerListener(CustomVillagerTrades plugin, TradeConfiguration tradeConfig) {
        this.plugin = plugin;
        this.tradeConfig = tradeConfig;
        this.random = new Random();
    }

    @EventHandler
    public void onVillagerAcquireTrade(VillagerAcquireTradeEvent event) {
        Villager villager = (Villager) event.getEntity();

        if (plugin.getConfig().getBoolean("Enabled", true)) {
            event.setCancelled(true); 
            setCustomTrades(villager);
        }
    }

    @EventHandler
    public void onVillagerSpawn(CreatureSpawnEvent event) {
        if (event.getEntityType() == EntityType.VILLAGER) {
            Villager villager = (Villager) event.getEntity();
           
        }
    }

    @EventHandler
    public void onZombieVillagerConvert(EntityTransformEvent event) {
        if (event.getTransformReason() == EntityTransformEvent.TransformReason.CURED) {
            if (event.getEntityType() == EntityType.ZOMBIE_VILLAGER &&
                    event.getTransformedEntity() instanceof Villager) {
                Villager villager = (Villager) event.getTransformedEntity();
                setCustomTrades(villager);
            }
        }
    }

    private void setCustomTrades(Villager villager) {
        String professionName = villager.getProfession().name().toLowerCase();

        
        if (villager.getProfession() == Villager.Profession.NITWIT ||
                villager.getProfession() == Villager.Profession.NONE) {
            return;
        }

        List<MerchantRecipe> availableTrades = tradeConfig.getTradesForProfession(
                professionName, villager.getVillagerLevel());

        if (!availableTrades.isEmpty()) {
            List<MerchantRecipe> selectedTrades = selectRandomTrades(availableTrades, 2);
            villager.setRecipes(selectedTrades);
        }
    }

    private List<MerchantRecipe> selectRandomTrades(List<MerchantRecipe> availableTrades, int amount) {
        List<MerchantRecipe> selectedTrades = new ArrayList<>();
        Set<String> seenTrades = new HashSet<>();

        Collections.shuffle(availableTrades); 

        for (MerchantRecipe recipe : availableTrades) {
            StringBuilder tradeKey = new StringBuilder();

            
            for (ItemStack ingredient : recipe.getIngredients()) {
                tradeKey.append(ingredient.getType().toString());
                if (ingredient.hasItemMeta() && ingredient.getItemMeta().hasDisplayName()) {
                    tradeKey.append("|").append(ingredient.getItemMeta().getDisplayName());
                }
                tradeKey.append(";");
            }

            
            tradeKey.append("=>").append(recipe.getResult().getType().toString());
            if (recipe.getResult().hasItemMeta() && recipe.getResult().getItemMeta().hasDisplayName()) {
                tradeKey.append("|").append(recipe.getResult().getItemMeta().getDisplayName());
            }

            
            if (!seenTrades.contains(tradeKey.toString())) {
                seenTrades.add(tradeKey.toString());
                selectedTrades.add(recipe);
            }

            if (selectedTrades.size() >= amount) break; 
        }

        return selectedTrades;
    }

}
