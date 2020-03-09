package me.zkharit.BTCcraft.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BtccraftHelpCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(strings.length != 0){
            return false;
        }

        commandSender.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "BTCcraft help menu");
        commandSender.sendMessage(ChatColor.AQUA + "/btccrafthelp" + ChatColor.WHITE + ": " + ChatColor.YELLOW + "brings up this help menu");
        commandSender.sendMessage(ChatColor.AQUA + "/generateaddress" + ChatColor.WHITE + ": " + ChatColor.YELLOW + "generates an address for a specific player");
        commandSender.sendMessage(ChatColor.AQUA + "/getmnemonic" + ChatColor.WHITE + ": " + ChatColor.YELLOW + "prints out the mnemonic for your btc address to restore");
        commandSender.sendMessage(ChatColor.AQUA + "/getplayeraddress" + ChatColor.WHITE + ": " + ChatColor.YELLOW + "prints out a specified players address");
        commandSender.sendMessage(ChatColor.AQUA + "/sendaddress" + ChatColor.WHITE + ": " + ChatColor.YELLOW + "send btc to a specific address");
        commandSender.sendMessage(ChatColor.AQUA + "/sendplayer" + ChatColor.WHITE + ": " + ChatColor.YELLOW + "sends btc to a players assigned btc address (not their set address)");
        commandSender.sendMessage(ChatColor.AQUA + "/setaddress" + ChatColor.WHITE + ": " + ChatColor.YELLOW + "set a specific address for you to /withdraw your earned btc to");
        commandSender.sendMessage(ChatColor.AQUA + "/settxfee" + ChatColor.WHITE + ": " + ChatColor.YELLOW + "set a transaction fee in terms of satoshis/kb");
        commandSender.sendMessage(ChatColor.AQUA + "/wallet" + ChatColor.WHITE + ": " + ChatColor.YELLOW + "prints out your wallet address, set address, fee, and balance");
        commandSender.sendMessage(ChatColor.AQUA + "/withdraw" + ChatColor.WHITE + ": " + ChatColor.YELLOW + "withdraw a specified amount of btc to send to your set address");

        return true;
    }
}
