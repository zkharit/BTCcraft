package me.zkharit.BTCcraft.commands;

import me.zkharit.BTCcraft.BTCcraft;
import me.zkharit.BTCcraft.BTCcraftWallet;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Iterator;

public class GenerateAddressCommand implements CommandExecutor {
    private BTCcraft btccraft;

    public GenerateAddressCommand(BTCcraft b){
        btccraft = b;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(strings.length != 1){
            return false;
        }

        Iterator it = btccraft.playerCache.entrySet().iterator();

        while (it.hasNext()) {
            HashMap.Entry entry = (HashMap.Entry) it.next();
            Player temp = (Player) entry.getKey();

            if (temp.getName().toLowerCase().equals(strings[0].toLowerCase())) {
                if(btccraft.getBTCcrafWalletFromCache(temp) == null){
                    BTCcraftWallet b = btccraft.generatePlayerWallet(temp);
                    commandSender.sendMessage(ChatColor.AQUA + "Generated " + ChatColor.YELLOW + "" + ChatColor.BOLD + strings[0] + ChatColor.AQUA + " an address: " + ChatColor.YELLOW + b.getDepositaddress());
                }else{
                    commandSender.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + strings[0] + ChatColor.AQUA + " already has an address, it is: " + ChatColor.YELLOW + btccraft.getBTCcrafWalletFromCache(temp).getDepositaddress());
                }
                return true;
            }
        }

        commandSender.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + strings[0] + ChatColor.AQUA + " cannot be found, they must be online");

        return true;
    }
}
