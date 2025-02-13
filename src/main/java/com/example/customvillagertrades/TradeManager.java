package com.example.customvillagertrades;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class TradeManager {
    private final CustomVillagerTrades plugin;
    private final Map<Villager.Profession, Map<Integer, List<MerchantRecipe>>> tradesByProfession;

    public TradeManager(CustomVillagerTrades plugin) {
        this.plugin = plugin;
        this.tradesByProfession = new HashMap<>();
        loadTrades();
    }
    public void reload(){
        tradesByProfession.clear();
        plugin.reloadConfig();

    }

    private void loadTrades() {
        ConfigurationSection villagerTypes = plugin.getConfig().getConfigurationSection("VillagerTypes");
        if (villagerTypes == null) return;

        for (String professionStr : villagerTypes.getKeys(false)) {
            try {
                Villager.Profession profession = Villager.Profession.valueOf(professionStr.toUpperCase());
                if (profession == Villager.Profession.NONE || profession == Villager.Profession.NITWIT) {
                    continue;
                }

                ConfigurationSection professionSection = villagerTypes.getConfigurationSection(professionStr);
                Map<Integer, List<MerchantRecipe>> levelTrades = new HashMap<>();

                for (String levelStr : professionSection.getKeys(false)) {
                    int level = Integer.parseInt(levelStr);
                    List<MerchantRecipe> trades = loadTradesForLevel(professionSection.getConfigurationSection(levelStr));
                    levelTrades.put(level, trades);
                }

                tradesByProfession.put(profession, levelTrades);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid profession type: " + professionStr);
            }
        }
    }

    private List<MerchantRecipe> loadTradesForLevel(ConfigurationSection levelSection) {
        List<MerchantRecipe> trades = new ArrayList<>();
        ConfigurationSection tradesSection = levelSection.getConfigurationSection("trades");
        
        if (tradesSection == null) return trades;

        for (String key : tradesSection.getKeys(false)) {
            ConfigurationSection tradeSection = tradesSection.getConfigurationSection(key);
            
            try {
                
                String[] resultParts = tradeSection.getString("result").split(":");
                Material resultMaterial = Material.valueOf(resultParts[0].toUpperCase());
                int resultAmount = Integer.parseInt(resultParts[1]);
                ItemStack result = new ItemStack(resultMaterial, resultAmount);

                
                if (resultMaterial == Material.ENCHANTED_BOOK) {
                    ConfigurationSection enchants = tradeSection.getConfigurationSection("enchantments");
                    if (enchants != null) {
                        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) result.getItemMeta();
                        for (String enchantName : enchants.getKeys(false)) {
                            Enchantment enchant = Enchantment.getByName(enchantName.toUpperCase());
                            if (enchant != null) {
                                meta.addStoredEnchant(enchant, enchants.getInt(enchantName), true);
                            }
                        }
                        result.setItemMeta(meta);
                    }
                }

                
                ConfigurationSection metadata = tradeSection.getConfigurationSection("metadata");
                if (metadata != null) {
                    ItemMeta meta = result.getItemMeta();
                    if (metadata.contains("name")) {
                        meta.setDisplayName(metadata.getString("name"));
                    }
                    if (metadata.contains("lore")) {
                        meta.setLore(metadata.getStringList("lore"));
                    }
                    result.setItemMeta(meta);
                }

                
                MerchantRecipe recipe = new MerchantRecipe(
                    result,
                    0,
                    tradeSection.getInt("max-use", 12),
                    tradeSection.getBoolean("playerxp", true),
                    tradeSection.getInt("villagerxp", 2),
                    Float.parseFloat(tradeSection.getString("price-multiplier", "0.05"))
                );

                
                List<String> ingredients = tradeSection.getStringList("ingredients");
                for (String ingredient : ingredients) {
                    String[] parts = ingredient.split(":");
                    Material material = Material.valueOf(parts[0].toUpperCase());
                    int amount = Integer.parseInt(parts[1]);
                    recipe.addIngredient(new ItemStack(material, amount));
                }

                trades.add(recipe);
            } catch (Exception e) {
                plugin.getLogger().warning("Error loading trade: " + e.getMessage());
            }
        }

        return trades;
    }

    public List<MerchantRecipe> getRandomTradesForVillager(Villager villager) {
        Map<Integer, List<MerchantRecipe>> levelTrades = tradesByProfession.get(villager.getProfession());
        if (levelTrades == null) return new ArrayList<>();

        List<MerchantRecipe> trades = levelTrades.get(villager.getVillagerLevel());
        if (trades == null || trades.isEmpty()) return new ArrayList<>();

        List<MerchantRecipe> randomTrades = new ArrayList<>();
        Random random = new Random();
        
        List<MerchantRecipe> availableTrades = new ArrayList<>(trades);
        int tradesToSelect = Math.min(2, availableTrades.size());
        
        for (int i = 0; i < tradesToSelect; i++) {
            int index = random.nextInt(availableTrades.size());
            randomTrades.add(availableTrades.get(index));
            availableTrades.remove(index);
        }

        return randomTrades;
    }
}