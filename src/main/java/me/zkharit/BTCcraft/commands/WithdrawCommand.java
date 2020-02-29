package me.zkharit.BTCcraft.commands;

import me.zkharit.BTCcraft.BTCcraft;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class WithdrawCommand implements CommandExecutor {
    private BTCcraft btccraft;

    public WithdrawCommand(BTCcraft b){
        btccraft = b;
    }
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        commandSender.sendMessage(btccraft.getBTCcrafWalletFromCache(null).getBalance().toFriendlyString());
        return true;
    }
}
