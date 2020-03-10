package me.zkharit.BTCcraft.commands;

import me.zkharit.BTCcraft.BTCcraft;
import me.zkharit.BTCcraft.BTCcraftWallet;
import org.bitcoinj.core.Address;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class AdminWalletCommand implements CommandExecutor {
    private BTCcraft btccraft;

    public AdminWalletCommand(BTCcraft b){
        btccraft = b;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(strings.length != 0){
            return false;
        }

        BTCcraftWallet adminWallet = btccraft.getBTCcrafWalletFromCache(null);

        if(adminWallet == null){
            commandSender.sendMessage(ChatColor.YELLOW + "There is no admin wallet generated");
            //should never happen but here just in case
            return true;
        }
        Address depositAddress = adminWallet.getDepositaddress();

        commandSender.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "Address: " + ChatColor.YELLOW + depositAddress);
        commandSender.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "Fee (sat/kb): " + ChatColor.YELLOW + adminWallet.getFee());
        commandSender.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "Balance: " + ChatColor.YELLOW + adminWallet.getBalance().toFriendlyString());
        commandSender.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "View your wallet here: " + ChatColor.YELLOW + ChatColor.UNDERLINE + "https://www.blockchain.com/btc/address/" + depositAddress);

        return true;
    }
}
