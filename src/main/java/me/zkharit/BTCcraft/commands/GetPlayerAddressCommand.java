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
import java.util.Map;

public class GetPlayerAddressCommand implements CommandExecutor {
    private BTCcraft btccraft;

    public GetPlayerAddressCommand(BTCcraft b){
        btccraft = b;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(strings.length != 1){
            return false;
        }

        Player player;

        if(commandSender instanceof Player) {
            player = (Player) commandSender;

            Iterator it = btccraft.walletCache.entrySet().iterator();

            while (it.hasNext()) {
                HashMap.Entry entry = (HashMap.Entry) it.next();
                Player temp = (Player) entry.getKey();

                if(temp == null){
                    entry = (HashMap.Entry)it.next();
                    temp = (Player)entry.getKey();
                }

                if (temp.getName().toLowerCase().equals(strings[0].toLowerCase())) {
                    BTCcraftWallet b = (BTCcraftWallet) entry.getValue();
                    player.sendMessage(ChatColor.AQUA + strings[0] + "'s address: " + ChatColor.YELLOW + b.getDepositaddress());
                    return true;
                }
            }

            player.sendMessage(ChatColor.AQUA + strings[0] + "'s " + ChatColor.YELLOW + "address cannot be found, they must be online");
        }
        return true;
    }
}
