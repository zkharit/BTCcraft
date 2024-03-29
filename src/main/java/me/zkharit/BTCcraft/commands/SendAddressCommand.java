package me.zkharit.BTCcraft.commands;


import me.zkharit.BTCcraft.BTCcraft;
import me.zkharit.BTCcraft.BTCcraftWallet;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.InsufficientMoneyException;
import org.bitcoinj.core.LegacyAddress;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.wallet.SendRequest;
import org.bitcoinj.wallet.Wallet;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SendAddressCommand implements CommandExecutor {
    private BTCcraft btccraft;

    public SendAddressCommand(BTCcraft b){
        btccraft = b;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        //if incorrect arg length then return false so the user sees the proper usage
        if(strings.length != 2){
            return false;
        }

        if(commandSender instanceof Player) {

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
            if (value.isLessThan(Coin.parseCoin("0.00001"))) {
                commandSender.sendMessage(ChatColor.YELLOW + "BTCCRAFT INFO: " + ChatColor.RESET + "Attempting to send too little amount");
                return true;
            }

            //get player that sent the command
            Player player = (Player) commandSender;
            BTCcraftWallet playerWallet = btccraft.getBTCcrafWalletFromCache(player);

            if(!playerWallet.getUsable()){
                player.sendMessage(ChatColor.YELLOW + "Please wait until your wallet is finished being restored, this can take up to 5 minutes");
                return true;
            }

            //get address from first argument
            LegacyAddress send = LegacyAddress.fromBase58(playerWallet.getParams(), strings[0]);

            //initialize the send result
            Wallet.SendResult result;

            //create a req for the value and the send to person, and se the fee to be used
            SendRequest req = SendRequest.to(send, value);
            req.feePerKb = Transaction.REFERENCE_DEFAULT_MIN_TX_FEE;
            //req.feePerKb = Coin.valueOf(10L);
            //comment out fee right now for nullpointer issues (havent implemented walletcache and storing wallets)
            //req.feePerKb = Coin.valueOf(btcraft.getBTCcrafWalletFromCache(player).getFee());

            //attempt to send, or let the user know they dont have enough money

            try {
                result = playerWallet.getWallet().sendCoins(playerWallet.getPeerGroup(), req);
            } catch (InsufficientMoneyException e) {
                player.sendMessage(ChatColor.YELLOW + "BTCCRAFT INFO: " + ChatColor.AQUA + "Not Enough Money");
                //e.printStackTrace(); insufficient fund requests spam console
                return true;
            }

            //If broadcasting tx is successful then send the tx id and give them a link to the tx on blockchain.com
            player.sendMessage(ChatColor.YELLOW + "BTCCRAFT INFO: " + ChatColor.RESET + "Transaction id: " + ChatColor.AQUA + result.tx.getTxId());
            player.sendMessage(ChatColor.YELLOW + "View your transaction here: " + ChatColor.AQUA + ChatColor.UNDERLINE + "https://www.blockchain.com/btc/tx/" + result.tx.getTxId());
            return true;

        }

        return true;
    }
}
