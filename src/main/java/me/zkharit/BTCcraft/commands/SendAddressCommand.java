package me.zkharit.BTCcraft.commands;


import me.zkharit.BTCcraft.BTCcraft;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.InsufficientMoneyException;
import org.bitcoinj.core.LegacyAddress;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.wallet.SendRequest;
import org.bitcoinj.wallet.Wallet;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class SendAddressCommand implements CommandExecutor {
    private WalletAppKit kit;
    private BTCcraft btcraft;

    public SendAddressCommand(WalletAppKit k, BTCcraft b){
        kit = k;
        btcraft = b;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        //if incorrect arg length then return false so the user sees the proper usage
        if(strings.length != 2){
            return false;
        }

        //regex for valid P2PKH/P2SH/Bech32 addresses, for later implementation
        /*if(!strings[0].matches("^([13][a-km-zA-HJ-NP-Z1-9]{25,34}|bc1[ac-hj-np-zAC-HJ-NP-Z02-9]{11,71})$")){
            commandSender.sendMessage(ChatColor.YELLOW + "BTCCRAFT INFO: " + ChatColor.RESET + "Invalid BTC Address");
            return true;
        }*/

        //Commented out so we can accept test-net addresses for now
        /*if(strings[0].charAt(0) != '1' && strings[0].charAt(0) != '3' && !strings[0].substring(0, 3).equals("bc1")){
            commandSender.sendMessage(ChatColor.YELLOW + "BTCCRAFT INFO: " + ChatColor.RESET + "Invalid BTC Address");
            return true;
        }*/

        //Get tx amount from the second argument
        Coin value = Coin.parseCoin(strings[1]);
        //if too small an amount we will get a dust error, so make sure we send enough
        if(value.isLessThan(Coin.parseCoin("0.00001"))){
            commandSender.sendMessage(ChatColor.YELLOW + "BTCCRAFT INFO: " + ChatColor.RESET + "Attempting to send too little amount");
            return true;
        }

        //if a player sent the command
        if(commandSender instanceof Player){
            //get player that sent the command
            Player player = (Player) commandSender;

            //get address from first argument
            LegacyAddress send = LegacyAddress.fromBase58(kit.params(), strings[0]);

            //initialize the send result
            Wallet.SendResult result;

            //create a req for the value and the send to person, and se the fee to be used
            SendRequest req = SendRequest.to(send, value);
            //comment out fee right now for nullpointer issues (havent implemented walletcache and storing wallets)
            //req.feePerKb = Coin.valueOf(btcraft.getBTCcrafWalletFromCache(player).getFee());

            //attempt to send, or let the user know they dont have enough money
            try {
                result = kit.wallet().sendCoins(kit.peerGroup(), req);
            } catch (InsufficientMoneyException e) {
                player.sendMessage(ChatColor.YELLOW + "BTCCRAFT INFO: " + ChatColor.AQUA + "Not Enough Money");
                //e.printStackTrace(); insufficient fund requests spam console
                return true;
            }

            //If broadcasting tx is successful then send the tx id and give them a link to the tx on blockchain.com
            player.sendMessage(ChatColor.YELLOW + "BTCCRAFT INFO: " + ChatColor.RESET + "Transaction id: " + ChatColor.AQUA + result.tx.getTxId());
            player.sendMessage(ChatColor.YELLOW + "View your transaction here: " + ChatColor.AQUA + "https://www.blockchain.com/btc/tx/" + result.tx.getTxId());
            return true;
        }

        //if command sender is the console then make it an adminsend
        //might be removed in future to avoid unintended problems
        if(commandSender instanceof ConsoleCommandSender){
            new AdminSendAddressCommand(kit).onCommand(commandSender, command, s, strings);
        }
        return true;
    }
}
