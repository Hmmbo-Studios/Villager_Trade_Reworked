package com.example.customvillagertrades.inventory;

import com.example.customvillagertrades.TradeConfiguration;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.Map;

public class ChatInputHandler implements Listener {
    private static final Map<Player, EditSession> activeEdits = new HashMap<>();

    public static void startEditing(Player player, String profession, String tier, int tradeIndex, String field, TradeConfiguration tradeConfig) {
        player.sendMessage(ChatColor.YELLOW + "Enter the new value for " + field + ":");
        activeEdits.put(player, new EditSession(profession, tier, tradeIndex, field, tradeConfig));
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!activeEdits.containsKey(player)) return;

        event.setCancelled(true);
        String message = event.getMessage();

        EditSession session = activeEdits.get(player);
        session.tradeConfig.updateTrade(session.profession, session.tier, session.tradeIndex, session.field, message);
        player.sendMessage(ChatColor.GREEN + "Trade " + session.field + " updated successfully!");

        activeEdits.remove(player);
    }

    private static class EditSession {
        String profession, tier, field;
        int tradeIndex;
        TradeConfiguration tradeConfig;

        public EditSession(String profession, String tier, int tradeIndex, String field, TradeConfiguration tradeConfig) {
            this.profession = profession;
            this.tier = tier;
            this.tradeIndex = tradeIndex;
            this.field = field;
            this.tradeConfig = tradeConfig;
        }
    }
}
