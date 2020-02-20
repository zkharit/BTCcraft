package me.zkharit.BTCcraft;

import org.bitcoinj.core.NetworkParameters;

public class Transaction extends org.bitcoinj.core.Transaction {
    String sendaddress;
    String receiveaddress;
    Double amount;
    Double fee;
    String transactionid;

    public Transaction(NetworkParameters params) {
        super(params);
    }

}
