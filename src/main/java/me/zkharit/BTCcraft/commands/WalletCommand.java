package me.zkharit.BTCcraft.commands;

import me.zkharit.BTCcraft.BTCcraft;
import me.zkharit.BTCcraft.BTCcraftWallet;
import org.bitcoinj.core.Address;
import org.bitcoinj.kits.WalletAppKit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WalletCommand implements CommandExecutor {
    private BTCcraft btccraft;

    public WalletCommand(BTCcraft b){
        btccraft = b;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(strings.length != 0){
            return false;
        }

        if(commandSender instanceof Player){
            Player player = (Player) commandSender;

            BTCcraftWallet wallet = btccraft.getBTCcrafWalletFromCache(player);
            Address a = wallet.getDepositaddress();

            //Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "INFO: "+ ChatColor.AQUA + player.getName() + ChatColor.RESET + " has used /wallet");
            player.sendMessage(ChatColor.YELLOW + "BTCCRAFT INFO: " + ChatColor.AQUA + "Address: " + ChatColor.YELLOW + a);
            player.sendMessage(ChatColor.YELLOW + "BTCCRAFT INFO: " + ChatColor.AQUA + "Balance: " + ChatColor.YELLOW + wallet.getBalance());
            player.sendMessage(ChatColor.YELLOW + "View your wallet here: " + ChatColor.AQUA + ChatColor.UNDERLINE + "https://www.blockchain.com/btc/address/" + a);
        }
        return true;
    }
}
