package me.zkharit.BTCcraft;

import org.bitcoinj.core.*;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.script.Script;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.KeyChainGroup;
import org.bitcoinj.wallet.Wallet;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class BTCcraftWallet extends Wallet {
    private Address depositaddress;
    private Address setaddress;
    private long fee = 10;
    private String mnemonic;
    private Wallet wallet;
    private Player player;
    private long creationTime;

    public BTCcraftWallet(NetworkParameters params, KeyChainGroup keyChainGroup, WalletAppKit kit) {
        super(params, keyChainGroup);

        //create deterministic wallet so we can restore at a later date
        wallet = Wallet.createDeterministic(params, Script.ScriptType.P2PKH);
        if (wallet.getKeyChainGroupSize() < 1) {
            wallet.importKey(new ECKey());
        }else{
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "BTCCRAFT ERROR: " + ChatColor.RESET + "Error Creating Admin Wallet, please delete plugins/BTCcraft/wallets and restart/reload");
            Bukkit.getServer().broadcastMessage(ChatColor.RED + "BTCCRAFT ERROR: " + ChatColor.RESET + "Error Creating Admin Wallet, please delete plugins/BTCcraft/wallets and restart/reload");
        }

        DeterministicSeed seed = wallet.getKeyChainSeed();
        creationTime = seed.getCreationTimeSeconds();
        mnemonic = Utils.SPACE_JOINER.join(seed.getMnemonicCode());
        depositaddress = wallet.currentReceiveAddress();

        //Dont use kit.wallet()
        //Create a new deterministic wallet each time, then add to the kit with kit.chain().addwallet(wallet)
        //then add to the walletcache
        //then store in the db/.json file
        //remove from cache on leave
        //access seed from the db/.json
        //on rejoin use restore from seed
        //add to wallet cache
        //add to kit same way
        //should work (i think) (hopefullly)
    }

    public Address getDepositaddress() {
        return depositaddress;
    }

    public Address getSetaddress() {
        return setaddress;
    }

    public long getFee() {
        return fee;
    }

    public Coin getBalance() {
        return wallet.getBalance();
        //return balance;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setDepositaddress(Address da) {
        this.depositaddress = da;
    }

    public void setSetaddress(Address sa) {
        this.setaddress = sa;
    }

    public void setFee(long fee) {
        this.fee = fee;
    }

    public Player getPlayer() {
        return player;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }
}
