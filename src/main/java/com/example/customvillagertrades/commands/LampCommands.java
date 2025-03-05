package com.example.customvillagertrades.commands;

import com.example.customvillagertrades.TradeManager;
import com.example.customvillagertrades.TradeConfiguration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class LampCommands implements CommandExecutor {

    private final TradeManager instance;
    private final TradeConfiguration tc;

    // Constructor to initialize TradeManager and TradeConfiguration
    public LampCommands(TradeManager instance, TradeConfiguration tc) {
        this.instance = instance;
        this.tc = tc;
    }

    // Handle the "bvt reload" command
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (sender.hasPermission("ctp.vt.add")) {
                instance.reload();
                tc.loadTradeConfigurations();
                sender.sendMessage("Plugin reloaded");
                return true;
            } else {
                sender.sendMessage("You don't have permission to reload.");
                return true;
            }
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
            sender.sendMessage("Please Join Our Discord for help.");
            sender.sendMessage("http://discord.hmmbo.com");
            return true;
        }

        return false;  // Command not recognized, return false to indicate we didn't handle it
    }
}
