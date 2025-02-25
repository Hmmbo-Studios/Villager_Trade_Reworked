package com.example.customvillagertrades;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class TradeConfiguration {
    private final JavaPlugin plugin;
    private final Map<String, YamlConfiguration> tradeConfigs;
    private final Random random = new Random();

    public TradeConfiguration(JavaPlugin plugin) {
        this.plugin = plugin;
        this.tradeConfigs = new HashMap<>();
        loadTradeConfigurations();
    }

    public void loadTradeConfigurations() {
        File tradesFolder = new File(plugin.getDataFolder(), "trades");
        if (!tradesFolder.exists()) {
            tradesFolder.mkdirs();

            String[] tradeFiles = {
                    "armorer.yml", "butcher.yml", "cartographer.yml", "cleric.yml",
                    "farmer.yml", "fisherman.yml", "fletcher.yml", "leatherworker.yml",
                    "librarian.yml", "shepherd.yml", "toolsmith.yml", "weaponsmith.yml", "mason.yml"
            };

            for (String fileName : tradeFiles) {
                plugin.saveResource("trades/" + fileName, false);
            }
        }

        for (File file : Objects.requireNonNull(tradesFolder.listFiles())) {
            if (file.getName().endsWith(".yml")) {
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                tradeConfigs.put(file.getName().replace(".yml", ""), config);
            }
        }
    }

    public List<MerchantRecipe> getTradesForProfession(String profession, int level) {
        YamlConfiguration config = tradeConfigs.get(profession.toLowerCase());
        if (config == null) return new ArrayList<>();

        List<MerchantRecipe> trades = new ArrayList<>();
        if (!config.contains("trades." + level)) return trades;

        List<Map<?, ?>> tradeOptions = config.getMapList("trades." + level);
        List<MerchantRecipe> possibleTrades = new ArrayList<>();

        for (Map<?, ?> tradeMap : tradeOptions) {
            MerchantRecipe recipe = createRecipe(tradeMap);
            if (recipe != null) {
                possibleTrades.add(recipe);
            }
        }

        
        return selectRandomTrades(possibleTrades, 2);
    }

    private List<MerchantRecipe> selectRandomTrades(List<MerchantRecipe> availableTrades, int amount) {
        List<MerchantRecipe> selectedTrades = new ArrayList<>();
        Set<String> seenTrades = new HashSet<>();

        Collections.shuffle(availableTrades);

        for (MerchantRecipe recipe : availableTrades) {
            StringBuilder key = new StringBuilder();

            
            for (ItemStack ingredient : recipe.getIngredients()) {
                key.append(ingredient.getType().toString());
                if (ingredient.hasItemMeta() && ingredient.getItemMeta().hasDisplayName()) {
                    key.append("|").append(ingredient.getItemMeta().getDisplayName());
                }
            }

            
            key.append("=>").append(recipe.getResult().getType().toString());
            if (recipe.getResult().hasItemMeta() && recipe.getResult().getItemMeta().hasDisplayName()) {
                key.append("|").append(recipe.getResult().getItemMeta().getDisplayName());
            }

            
            if (!seenTrades.contains(key.toString())) {
                seenTrades.add(key.toString());
                selectedTrades.add(recipe);
            }

            if (selectedTrades.size() >= amount) break;
        }

        return selectedTrades;
    }


    private MerchantRecipe createRecipe(Map<?, ?> tradeMap) {
        try {
            ItemStack result = parseItem((Map<?, ?>) tradeMap.get("result"));

            MerchantRecipe recipe = new MerchantRecipe(
                    result,
                    0,
                    (int) tradeMap.get("max-use"),
                    (boolean) tradeMap.get("playerxp"),
                    (int) tradeMap.get("villagerxp"),
                    Float.parseFloat(tradeMap.get("price-multiplier") + "")
            );

            Map<?, ?> ingredientsMap = (Map<?, ?>) tradeMap.get("ingredients");
            for (Object key : ingredientsMap.keySet()) {
                Map<?, ?> ingredientData = (Map<?, ?>) ingredientsMap.get(key);
                ItemStack ingredientItem = parseItem(ingredientData); 
                recipe.addIngredient(ingredientItem);
            }

            return recipe;
        } catch (Exception e) {
            plugin.getLogger().warning("Error creating trade recipe: " + e.getMessage());
            return null;
        }
    }

    private ItemStack parseItem(Map<?, ?> itemData) {
        Material material = Material.valueOf(itemData.get("type").toString());
        int amount = itemData.containsKey("amount") ? (int) itemData.get("amount") : 1;
        ItemStack item = new ItemStack(material, amount);

        
        String displayName = itemData.containsKey("display-name")
                ? itemData.get("display-name").toString().replace("&", "§")
                : null;

        
        List<String> lore = new ArrayList<>();
        if (itemData.containsKey("lore")) {
            for (String line : (List<String>) itemData.get("lore")) {
                lore.add(line.replace("&", "§"));
            }
        }

        if (material == Material.ENCHANTED_BOOK) {
            applyEnchantments(item, itemData);  // Directly modifies the ItemStack
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                if (displayName != null) meta.setDisplayName(displayName);
                if (!lore.isEmpty()) meta.setLore(lore);
                item.setItemMeta(meta);
            }
        } else {
            applyEnchantments(item, itemData);  // Directly modifies the ItemStack
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                if (displayName != null) meta.setDisplayName(displayName);
                if (!lore.isEmpty()) meta.setLore(lore);
                item.setItemMeta(meta);
            }
        }
        return item;
    }

    private void applyEnchantments(ItemStack item, Map<?, ?> itemData) {
        if (itemData.containsKey("enchants")) {
            Map<?, ?> enchantments = (Map<?, ?>) itemData.get("enchants");

            for (Map.Entry<?, ?> entry : enchantments.entrySet()) {
                Enchantment enchantment = Enchantment.getByKey(
                        NamespacedKey.minecraft(entry.getKey().toString().toLowerCase())
                );
                int lvl = (int) entry.getValue();
                if (enchantment != null) {
                    // Check if the item is an enchanted book
                    if (item.getType() == Material.ENCHANTED_BOOK) {
                        ItemMeta meta = item.getItemMeta();
                        if (meta instanceof EnchantmentStorageMeta) {
                            ((EnchantmentStorageMeta) meta).addStoredEnchant(enchantment, lvl, true);
                            item.setItemMeta(meta);
                        }
                    } else {
                        // Apply enchantment directly to the item for better anvil support
                        item.addUnsafeEnchantment(enchantment, lvl);
                    }
                }
            }
        }
    }


    public void saveConfig(String profession) {
        File file = new File(plugin.getDataFolder(), "trades/" + profession.toLowerCase() + ".yml");
        try {
            tradeConfigs.get(profession.toLowerCase()).save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Set<String> getProfessions() {
        return tradeConfigs.keySet();
    }

    public Set<String> getTiers(String profession) {
        YamlConfiguration config = tradeConfigs.get(profession.toLowerCase());
        if (config == null || !config.contains("trades")) return new HashSet<>();

        return config.getConfigurationSection("trades").getKeys(false);
    }

    public List<Map<?, ?>> getTrades(String profession, String tier) {
        YamlConfiguration config = tradeConfigs.get(profession.toLowerCase());
        if (config == null || !config.contains("trades." + tier)) return new ArrayList<>();

        return config.getMapList("trades." + tier);
    }

    /**
     * ✅ **Function to Add a Trade**
     * @param profession - Villager profession (e.g., "armorer").
     * @param tier - Trade tier (e.g., "1", "2", etc.).
     * @param tradeData - Trade details in Map format.
     */
    public void addTrade(String profession, String tier, Map<String, Object> tradeData) {
        YamlConfiguration config = tradeConfigs.get(profession.toLowerCase());
        if (config == null) return;

        List<Map<?, ?>> trades = getTrades(profession, tier);
        trades.add(tradeData);

        config.set("trades." + tier, trades);
        saveConfig(profession);
    }

    /**
     * ✅ **Function to Remove a Trade**
     * @param profession - Villager profession.
     * @param tier - Trade tier.
     * @param tradeIndex - Index of the trade to remove.
     */
    public void removeTrade(String profession, String tier, int tradeIndex) {
        YamlConfiguration config = tradeConfigs.get(profession.toLowerCase());
        if (config == null) return;

        List<Map<?, ?>> trades = getTrades(profession, tier);
        if (tradeIndex < 0 || tradeIndex >= trades.size()) return;

        trades.remove(tradeIndex);
        config.set("trades." + tier, trades);
        saveConfig(profession);
    }

    public void updateTrade(String profession, String tier, int tradeIndex, String field, Object newValue) {
        YamlConfiguration config = tradeConfigs.get(profession.toLowerCase());
        if (config == null) return;

        List<Map<?, ?>> trades = getTrades(profession, tier);
        if (tradeIndex < 0 || tradeIndex >= trades.size()) return;

        Map<String, Object> trade = (Map<String, Object>) trades.get(tradeIndex);
        trade.put(field, newValue);

        config.set("trades." + tier, trades);
        saveConfig(profession);
    }

}
