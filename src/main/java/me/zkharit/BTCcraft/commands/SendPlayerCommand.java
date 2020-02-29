package me.zkharit.BTCcraft.commands;

import me.zkharit.BTCcraft.BTCcraft;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SendPlayerCommand implements CommandExecutor {
    private BTCcraft btccraft;

    public SendPlayerCommand(BTCcraft b){
        btccraft = b;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(strings.length != 2){
            return false;
        }
        Bukkit.getServer().broadcastMessage(s);
        Bukkit.getServer().broadcastMessage(strings[1]);

        return true;
    }
}
