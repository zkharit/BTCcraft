package me.zkharit.BTCcraft.commands;

import me.zkharit.BTCcraft.BTCcraft;
import me.zkharit.BTCcraft.BTCcraftWallet;
import org.bitcoinj.core.Address;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GetMnemonicCommand implements CommandExecutor {
    private BTCcraft btccraft;

    public GetMnemonicCommand(BTCcraft b){
        btccraft = b;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length != 0) {
            return false;
        }

        if(commandSender instanceof Player){
            Player p = (Player) commandSender;
            p.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + p.getName() + ChatColor.AQUA + " your mnemonic to restore your BTC wallet is: " + ChatColor.YELLOW + btccraft.getBTCcrafWalletFromCache(p).getMnemonic());
        }

        return true;
    }
}
