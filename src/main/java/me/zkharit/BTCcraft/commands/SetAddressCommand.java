package me.zkharit.BTCcraft.commands;

import me.zkharit.BTCcraft.BTCcraft;
import me.zkharit.BTCcraft.BTCcraftWallet;
import org.bitcoinj.core.LegacyAddress;
import org.bitcoinj.params.TestNet3Params;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetAddressCommand implements CommandExecutor {
    private BTCcraft btccraft;

    public SetAddressCommand(BTCcraft b){
        btccraft = b;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(strings.length != 1 && strings.length != 0){
            return false;
        }

        if(commandSender instanceof Player) {
            Player player = (Player) commandSender;
            BTCcraftWallet playerWallet  = btccraft.getBTCcrafWalletFromCache(player);

            if(!playerWallet.getUsable()){
                player.sendMessage(ChatColor.YELLOW + "Please wait until your wallet is finished being restored, this can take up to 5 minutes");
                return true;
            }

            if (strings.length == 1) {

                //regex for valid P2PKH/P2SH/Bech32 addresses, for later implementation
            /*if(!strings[0].matches("^([13][a-km-zA-HJ-NP-Z1-9]{25,34}|bc1[ac-hj-np-zAC-HJ-NP-Z02-9]{11,71})$")){
                player.sendMessage(ChatColor.YELLOW + "BTCCRAFT INFO: " + ChatColor.RESET + "Invalid BTC Address");
                return true;
            }*/

                //Commented out so we can accept test-net addresses for now
            /*if(strings[0].charAt(0) != '1' && strings[0].charAt(0) != '3' && !strings[0].substring(0, 3).equals("bc1")){
                player.sendMessage(ChatColor.YELLOW + "BTCCRAFT INFO: " + ChatColor.RESET + "Invalid BTC Address");
                return true;
            }*/

                player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "WARNING - No address checking is done here make sure the address you set is correct");

                playerWallet.setSetaddress(LegacyAddress.fromString(TestNet3Params.get(), strings[0]));

            }else{

                playerWallet.setSetaddress(LegacyAddress.fromString(TestNet3Params.get(), playerWallet.getDepositaddress().toString()));

            }
            player.sendMessage(ChatColor.AQUA + "Successfully set address as: " + ChatColor.YELLOW + ChatColor.BOLD + playerWallet.getSetaddress());
            btccraft.updateJSON(btccraft.getUUIDFromCache(player).toString(), "set", playerWallet.getSetaddress().toString());
        }

        return true;
    }
}
