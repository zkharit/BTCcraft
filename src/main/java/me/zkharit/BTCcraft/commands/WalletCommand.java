package me.zkharit.BTCcraft.commands;

import me.zkharit.BTCcraft.BTCcraft;
import me.zkharit.BTCcraft.BTCcraftWallet;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WalletCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player){
            Player player = (Player) commandSender;

            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "INFO: "+ ChatColor.AQUA + player.getName() + ChatColor.RESET + " has used /wallet");


        }

        return false;
    }

    private BTCcraftWallet getWallet(Player player){
        return null;
    }
}
