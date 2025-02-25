package com.example.customvillagertrades.commands;

import com.example.customvillagertrades.TradeConfiguration;
import com.example.customvillagertrades.TradeManager;
import com.example.customvillagertrades.inventory.ProfessionGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Suggest;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LampCommands {
    TradeManager instance;
    TradeConfiguration tc;

    public LampCommands(TradeManager instance, TradeConfiguration tc) {
        this.instance = instance;
        this.tc = tc;
    }

    @Command("bvt reload")
    @CommandPermission("ctp.vt.add")
    public void reload(BukkitCommandActor actor) {
        instance.reload();
        tc.loadTradeConfigurations();
        actor.sendRawMessage("plugin reloaded");
    }
    @Command("bvt help")
    @CommandPermission("ctp.vt.add")
    public void help(BukkitCommandActor actor) {
        actor.sendRawMessage("Please Join Our Discord for help.");
        actor.sendRawMessage("http://discord.hmmbo.com");
    }

//    @Command("vt menu")
//    @CommandPermission("ctp.vt.add")
//    public void menu(Player actor) {
//
//
//            Player player = actor;
//            ProfessionGUI p = new ProfessionGUI(tc);
//            p.openProfessionGUI(player);
//
//    }
//
//
//    @Command("vt add")
//    @Description("Adds a trade to a villager.")
//    @CommandPermission("ctp.vt.add")
//
//    public void addTrade(
//            Player player,
//            @Suggest("villagerUUIDs") UUID villagerUUID,
//            @Suggest("itemNames") String item1,
//            @Suggest("itemNames") String item2,
//            @Suggest("itemNames") String result) {
//        Entity entity = Bukkit.getEntity(villagerUUID);
//        if (!(entity instanceof Villager villager)) {
//            player.sendMessage("No villager found with the provided UUID.");
//            return;
//        }
//
//        ItemStack itemStack1 = parseItem(player, item1);
//        ItemStack itemStack2 = item2.equalsIgnoreCase("null") ? null : parseItem(player, item2);
//        ItemStack resultStack = parseItem(player, result);
//
//        if (itemStack1 == null || resultStack == null) {
//            player.sendMessage("Invalid item format or empty slot.");
//            return;
//        }
//
//        MerchantRecipe recipe = new MerchantRecipe(resultStack, 0, Integer.MAX_VALUE, true);
//        recipe.addIngredient(itemStack1);
//        if (itemStack2 != null) {
//            recipe.addIngredient(itemStack2);
//        }
//
//        List<MerchantRecipe> recipes = new ArrayList<>(villager.getRecipes());
//        recipes.add(recipe);
//        villager.setRecipes(recipes);
//
//        player.sendMessage("Trade added successfully.");
//    }
//
//    private ItemStack parseItem(Player player, String item) {
//        if (item.toLowerCase().startsWith("slot_")) {
//            try {
//                int slot = Integer.parseInt(item.substring(5));
//                if (slot >= 0 && slot <= 8) {
//                    ItemStack itemInSlot = player.getInventory().getItem(slot);
//                    if (itemInSlot != null && itemInSlot.getType() != Material.AIR) {
//                        return itemInSlot.clone();
//                    }
//                }
//            } catch (NumberFormatException e) {
//                return null;
//            }
//        } else {
//            try {
//                Material material = Material.valueOf(item.toUpperCase());
//                return new ItemStack(material, 1);
//            } catch (IllegalArgumentException e) {
//                return null;
//            }
//        }
//        return null;
//    }


}
