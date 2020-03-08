package me.zkharit.BTCcraft.commands;

import me.zkharit.BTCcraft.BTCcraft;
import me.zkharit.BTCcraft.BTCcraftWallet;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.InsufficientMoneyException;
import org.bitcoinj.core.LegacyAddress;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.wallet.SendRequest;
import org.bitcoinj.wallet.Wallet;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class AdminSendAddressCommand implements CommandExecutor {
    private BTCcraft btccraft;

    public AdminSendAddressCommand(BTCcraft b){
        btccraft = b;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if(strings.length != 2){
            return false;
        }

        Coin value = Coin.parseCoin(strings[1]);
        //if too small an amount we will get a dust error, so make sure we send enough
        if (value.isLessThan(Coin.parseCoin("0.00001"))) {
            commandSender.sendMessage(ChatColor.YELLOW + "BTCCRAFT INFO: " + ChatColor.RESET + "Attempting to send too little amount");
            return true;
        }

        BTCcraftWallet adminWallet = btccraft.getBTCcrafWalletFromCache(null);

        if(!adminWallet.getUsable()){
            commandSender.sendMessage(ChatColor.YELLOW + "Please wait until your wallet is finished being restored, this can take up to 5 minutes");
            return true;
        }

        //get address from first argument
        LegacyAddress send = LegacyAddress.fromBase58(adminWallet.getParams(), strings[0]);

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
            result = adminWallet.getWallet().sendCoins(adminWallet.getPeerGroup(), req);
        } catch (InsufficientMoneyException e) {
            commandSender.sendMessage(ChatColor.YELLOW + "BTCCRAFT INFO: " + ChatColor.AQUA + "Not Enough Money");
            //e.printStackTrace(); insufficient fund requests spam console
            return true;
        }

        //If broadcasting tx is successful then send the tx id and give them a link to the tx on blockchain.com
        commandSender.sendMessage(ChatColor.YELLOW + "BTCCRAFT INFO: " + ChatColor.RESET + "Transaction id: " + ChatColor.AQUA + result.tx.getTxId());
        commandSender.sendMessage(ChatColor.YELLOW + "View your transaction here: " + ChatColor.AQUA + ChatColor.UNDERLINE + "https://www.blockchain.com/btc/tx/" + result.tx.getTxId());
        return true;
    }
}
