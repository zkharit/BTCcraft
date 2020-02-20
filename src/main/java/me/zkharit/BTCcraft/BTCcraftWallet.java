package me.zkharit.BTCcraft;

import org.bitcoinj.core.Coin;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.wallet.*;

public class BTCcraftWallet extends Wallet{
    private String depositaddress;
    private String setaddress;
    private Double fee = 10.0;
    private Coin balance;

    public BTCcraftWallet(NetworkParameters params, KeyChainGroup keyChainGroup) {
        super(params, keyChainGroup);
    }

    public String getDespoitAddress() {
        return depositaddress;
    }

    public String getSetaddress() {
        return setaddress;
    }

    public Double getFee() {
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

    public void setFee(Double fee) {
        this.fee = fee;
    }

    public void setBalance(Coin balance) {
        this.balance = balance;
    }

    /*public BTCcraftWallet(Player player) {
        UUID uuid = player.getUniqueId();

    }*/
}
