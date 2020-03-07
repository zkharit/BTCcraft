package me.zkharit.BTCcraft.commands;

import me.zkharit.BTCcraft.BTCcraft;
import me.zkharit.BTCcraft.BTCcraftWallet;
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

        if(commandSender instanceof Player) {
            Player p = (Player) commandSender;

            BTCcraftWallet pWallet = btccraft.getBTCcrafWalletFromCache(p);
            if (pWallet.getUsable()) {
                p.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + p.getName() + ChatColor.AQUA + " your mnemonic to restore your BTC wallet is: " + ChatColor.YELLOW + pWallet.getMnemonic());

            }else{
                p.sendMessage(ChatColor.YELLOW + "Please wait until your wallet is finished being restored, this can take up to 5 minutes");
            }
        }

        return true;
    }
}
