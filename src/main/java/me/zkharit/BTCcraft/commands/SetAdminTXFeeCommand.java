package me.zkharit.BTCcraft.commands;

import me.zkharit.BTCcraft.BTCcraft;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SetAdminTXFeeCommand implements CommandExecutor {
    private BTCcraft btccraft;

    public SetAdminTXFeeCommand(BTCcraft b){
        btccraft = b;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        return false;
    }
}
