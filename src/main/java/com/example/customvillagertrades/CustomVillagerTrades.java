package com.example.customvillagertrades;

import com.example.customvillagertrades.commands.LampCommands;
import com.example.customvillagertrades.inventory.*;
import org.bukkit.plugin.java.JavaPlugin;
import revxrsal.commands.bukkit.BukkitLamp;

public class CustomVillagerTrades extends JavaPlugin {
    private static CustomVillagerTrades instance;
    private TradeManager tradeManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        
        tradeManager = new TradeManager(this);


        TradeConfiguration tradeConfig = new TradeConfiguration(this);

        var lamp = BukkitLamp.builder(this).build();
        lamp.register(new LampCommands(tradeManager,tradeConfig));



        getServer().getPluginManager().registerEvents(new VillagerListener(this, tradeConfig), this);
        getServer().getPluginManager().registerEvents(new MenuListener(this), this);
        getServer().getPluginManager().registerEvents(new ProfessionGUI(tradeConfig), this);
        getServer().getPluginManager().registerEvents(new TierGUI(tradeConfig), this);
        getServer().getPluginManager().registerEvents(new TradeEditorGUI(tradeConfig), this);
        getServer().getPluginManager().registerEvents(new TradeDetailGUI(tradeConfig), this);
        getServer().getPluginManager().registerEvents(new ChatInputHandler(), this); // Handles chat inputs
        

    }

    public static CustomVillagerTrades getInstance() {
        return instance;
    }

    public TradeManager getTradeManager() {
        return tradeManager;
    }
}