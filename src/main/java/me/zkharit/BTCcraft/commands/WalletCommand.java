package me.zkharit.BTCcraft.commands;

import me.zkharit.BTCcraft.BTCcraft;
import me.zkharit.BTCcraft.BTCcraftWallet;
import org.bitcoinj.core.Address;
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
            if(wallet == null){
                player.sendMessage(ChatColor.YELLOW + "Could not find a wallet associated with your player");
                return true;
            }

            if(!wallet.getUsable()){
                player.sendMessage(ChatColor.YELLOW + "Please wait until your wallet is finished being restored, this can take up to 5 minutes");
                return true;
            }

            Address a = wallet.getDepositaddress();
            Address b = wallet.getSetaddress();

            player.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "Address: " + ChatColor.YELLOW + a);
            if(!a.toString().equals(b.toString())){
                player.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "Set Address: " + ChatColor.YELLOW + b);
            }
            player.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "Fee (sat/kb): " + ChatColor.YELLOW + wallet.getFee());
            player.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "Balance: " + ChatColor.YELLOW + wallet.getBalance().toFriendlyString());
            player.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "View your wallet here: " + ChatColor.YELLOW + ChatColor.UNDERLINE + "https://www.blockchain.com/btc/address/" + a);
        }
        return true;
    }
}
