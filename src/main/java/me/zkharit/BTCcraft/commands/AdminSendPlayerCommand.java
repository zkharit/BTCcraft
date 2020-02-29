package me.zkharit.BTCcraft.commands;

import me.zkharit.BTCcraft.BTCcraft;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class AdminSendPlayerCommand implements CommandExecutor {
    private BTCcraft btccraft;

    public AdminSendPlayerCommand(BTCcraft b){
        btccraft = b;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        return false;
    }
}
