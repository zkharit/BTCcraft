package me.zkharit.BTCcraft;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bukkit.entity.Player;

public class BTCcraftTransaction extends Transaction {
    String sendaddress;
    String receiveaddress;
    Double amount;
    Double fee;
    String transactionid;

    public BTCcraftTransaction(NetworkParameters params, Player player) {
        super(params);
    }

}
