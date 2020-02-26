package me.zkharit.BTCcraft;

import org.bitcoinj.core.Coin;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.wallet.*;

import org.bukkit.entity.Player;

public class BTCcraftWallet extends Wallet{
    private String depositaddress;
    private String setaddress;
    private long fee = 10;
    private Coin balance;

    public BTCcraftWallet(NetworkParameters params, KeyChainGroup keyChainGroup, Player player) {
        super(params, keyChainGroup);

        String uuid = player.getUniqueId().toString();
    }

    public String getDepositAddress() {
        return depositaddress;
    }

    public String getSetaddress() {
        return setaddress;
    }

    public long getFee() {
        return fee;
    }

    public Coin getBalance() {
        return balance;
    }

    public void setDefaultaddress(String defaultaddress) {
        this.depositaddress = defaultaddress;
    }

    public void setSetaddress(String setaddress) {
        this.setaddress = setaddress;
    }

    public void setFee(long fee) {
        this.fee = fee;
    }

    public void setBalance(Coin balance) {
        this.balance = balance;
    }

    /*public BTCcraftWallet(Player player) {
        super();
        UUID uuid = player.getUniqueId();
    }*/
}
