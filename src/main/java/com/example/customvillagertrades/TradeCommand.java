package com.example.customvillagertrades;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TradeCommand implements CommandExecutor {
    private final CustomVillagerTrades plugin;

    public TradeCommand(CustomVillagerTrades plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players!");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("customvillagertrades.admin")) {
            player.sendMessage("You don't have permission to use this command!");
            return true;
        }

        // Open trade configuration menu
        new TradeMenu(plugin).openMenu(player);
        return true;
    }
}