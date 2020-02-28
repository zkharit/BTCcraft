package me.zkharit.BTCcraft;

import org.bitcoinj.core.*;
import org.bitcoinj.script.Script;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.KeyChainGroup;
import org.bitcoinj.wallet.UnreadableWalletException;
import org.bitcoinj.wallet.Wallet;

import org.bitcoinj.wallet.listeners.WalletCoinsReceivedEventListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.TimeUnit;

public class BTCcraftWallet extends Wallet {
    private Address depositaddress;
    private Address setaddress;
    private long fee = 10;
    private String mnemonic;
    private Wallet wallet;
    private Player player;
    private long creationTime;
    /*kit.wallet().addCoinsReceivedEventListener(new WalletCoinsReceivedEventListener() {
        @Override
        public void onCoinsReceived(Wallet wallet, Transaction tx, Coin coin, Coin coin1) {
            BukkitRunnable r = new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.getServer().broadcastMessage(ChatColor.RED + "BTCCRAFT ERROR: Received 0 Confirmations");

                }
            };
            r.runTaskAsynchronously(btCcraft);
            while(tx.getConfidence().getDepthInBlocks() < 1){
                BukkitRunnable k = new BukkitRunnable() {
                    @Override
                    public void run() {
                        Bukkit.getServer().broadcastMessage(ChatColor.RED + "BTCCRAFT ERROR: Waiting for confirmations");

                    }
                };
                k.runTaskAsynchronously(btCcraft);
                try {
                    TimeUnit.MINUTES.sleep(1);
                } catch (InterruptedException e) {
                    Bukkit.getServer().getConsoleSender().sendMessage("Something exception");
                    e.printStackTrace();
                }
            }
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "BTCCRAFT INFO: Received coins??? block depth of: " + tx.getConfidence().getDepthInBlocks());
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "BTCCRAFT INFO: Coin: " + coin.value);
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "BTCCRAFT INFO: Coin1: " + coin1.value);
        }
    });
        return null;*/

    public BTCcraftWallet(NetworkParameters params, KeyChainGroup keyChainGroup) {
        super(params, keyChainGroup);
        //admin wallet constructor

        //create deterministic wallet so we can restore at a later date
        wallet = Wallet.createDeterministic(params, Script.ScriptType.P2PKH);

        DeterministicSeed seed = wallet.getKeyChainSeed();
        creationTime = seed.getCreationTimeSeconds();
        mnemonic = Utils.SPACE_JOINER.join(seed.getMnemonicCode());
        depositaddress = wallet.currentReceiveAddress();
        setaddress = depositaddress;

        wallet.addCoinsReceivedEventListener(new WalletCoinsReceivedEventListener() {
            @Override
            public void onCoinsReceived(Wallet wallet, Transaction tx, Coin coin, Coin coin1) {
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "BTCCRAFT INFO: " + ChatColor.RESET + "Admin wallet received transaction, waiting for confirmation, trying once a minute");

                while(tx.getConfidence().getDepthInBlocks() < 1){
                    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "BTCCRAFT INFO: " + ChatColor.RESET + "txid: " + ChatColor.AQUA + tx.getTxId() + ChatColor.RESET + " still awaiting confirmation");

                    try {
                        TimeUnit.MINUTES.sleep(1);
                    } catch (InterruptedException e) {
                        Bukkit.getServer().getConsoleSender().sendMessage("Something exception");
                        e.printStackTrace();
                    }
                }
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "BTCCRAFT INFO: " + ChatColor.RESET + "txid: " + ChatColor.AQUA + tx.getTxId() + ChatColor.RESET + " has 1 confirmation");
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "BTCCRAFT INFO: " + ChatColor.RESET + "Received: " + tx.getValueSentToMe(wallet).toFriendlyString() + ChatColor.RESET + " from txid: " + ChatColor.AQUA + tx.getTxId());
            }
        });

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

    public BTCcraftWallet(NetworkParameters params, KeyChainGroup keyChainGroup, Player p) {
        super(params, keyChainGroup);
        //normal player wallet constructor

        //create deterministic wallet so we can restore at a later date
        wallet = Wallet.createDeterministic(params, Script.ScriptType.P2PKH);

        DeterministicSeed seed = wallet.getKeyChainSeed();
        creationTime = seed.getCreationTimeSeconds();
        mnemonic = Utils.SPACE_JOINER.join(seed.getMnemonicCode());
        depositaddress = wallet.currentReceiveAddress();
        setaddress = depositaddress;

        player = p;

        wallet.addCoinsReceivedEventListener(new WalletCoinsReceivedEventListener() {
            @Override
            public void onCoinsReceived(Wallet wallet, Transaction tx, Coin coin, Coin coin1) {
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "BTCCRAFT INFO: " + ChatColor.RESET + "Admin wallet received transaction, waiting for confirmation, trying once a minute");

                while(tx.getConfidence().getDepthInBlocks() < 1){
                    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "BTCCRAFT INFO: " + ChatColor.RESET + "txid: " + ChatColor.AQUA + tx.getTxId() + ChatColor.RESET + " still awaiting confirmation");

                    try {
                        TimeUnit.MINUTES.sleep(1);
                    } catch (InterruptedException e) {
                        Bukkit.getServer().getConsoleSender().sendMessage("Something exception");
                        e.printStackTrace();
                    }
                }
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "BTCCRAFT INFO: " + ChatColor.RESET + "txid: " + ChatColor.AQUA + tx.getTxId() + ChatColor.RESET + " has 1 confirmation");
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "BTCCRAFT INFO: " + ChatColor.RESET + "Received: " + tx.getValueSentToMe(wallet).toFriendlyString() + ChatColor.RESET + " from txid: " + ChatColor.AQUA + tx.getTxId());
            }
        });

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

    public BTCcraftWallet(NetworkParameters params, KeyChainGroup keyChainGroup, Player p, Address da, Address sa, String mnemonicSeed, long ctime, long f){
        super(params, keyChainGroup);

        depositaddress = da;
        setaddress = sa;
        mnemonic = mnemonicSeed;
        creationTime = ctime;
        fee = f;
        player = p;

        try {
            DeterministicSeed d = new DeterministicSeed(mnemonic, null, "", creationTime);
            wallet = Wallet.fromSeed(params, d, Script.ScriptType.P2PKH);

        }catch(UnreadableWalletException e){
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "BTCCRAFT ERROR: " + ChatColor.RESET + "Could not restore " + ChatColor.YELLOW + player.getName() + "'s " + ChatColor.RESET + "wallet");
            e.printStackTrace();
        }

        wallet.addCoinsReceivedEventListener(new WalletCoinsReceivedEventListener() {
            @Override
            public void onCoinsReceived(Wallet wallet, Transaction tx, Coin coin, Coin coin1) {
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "BTCCRAFT INFO: " + ChatColor.RESET + "Admin wallet received transaction, waiting for confirmation, trying once a minute");

                while(tx.getConfidence().getDepthInBlocks() < 1){
                    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "BTCCRAFT INFO: " + ChatColor.RESET + "txid: " + ChatColor.AQUA + tx.getTxId() + ChatColor.RESET + " still awaiting confirmation");

                    try {
                        TimeUnit.MINUTES.sleep(1);
                    } catch (InterruptedException e) {
                        Bukkit.getServer().getConsoleSender().sendMessage("Something exception");
                        e.printStackTrace();
                    }
                }
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "BTCCRAFT INFO: " + ChatColor.RESET + "txid: " + ChatColor.AQUA + tx.getTxId() + ChatColor.RESET + " has 1 confirmation");
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "BTCCRAFT INFO: " + ChatColor.RESET + "Received: " + tx.getValueSentToMe(wallet).toFriendlyString() + ChatColor.RESET + " from txid: " + ChatColor.AQUA + tx.getTxId());
            }
        });
    }

    public BTCcraftWallet(NetworkParameters params, KeyChainGroup keyChainGroup, Address da, String mnemonicSeed, long ctime, long f){
        super(params, keyChainGroup);

        depositaddress = da;
        setaddress = da;
        mnemonic = mnemonicSeed;
        creationTime = ctime;
        fee = f;

        DeterministicSeed d;
        try {
            d = new DeterministicSeed(mnemonic, null, "", creationTime);
            wallet = Wallet.fromSeed(params, d, Script.ScriptType.P2PKH);
        } catch (UnreadableWalletException e) {
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "BTCCRAFT ERROR: " + ChatColor.RESET + "Could not restore " + ChatColor.RED + "ADMIN wallet " + ChatColor.RESET + "please investigate");
            e.printStackTrace();
        }

        wallet.addCoinsReceivedEventListener(new WalletCoinsReceivedEventListener() {
            @Override
            public void onCoinsReceived(Wallet wallet, Transaction tx, Coin coin, Coin coin1) {
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "BTCCRAFT INFO: " + ChatColor.RESET + "Admin wallet received transaction, waiting for confirmation, trying once a minute");

                while(tx.getConfidence().getDepthInBlocks() < 1){
                    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "BTCCRAFT INFO: " + ChatColor.RESET + "txid: " + ChatColor.AQUA + tx.getTxId() + ChatColor.RESET + " still awaiting confirmation");

                    try {
                        TimeUnit.MINUTES.sleep(1);
                    } catch (InterruptedException e) {
                        Bukkit.getServer().getConsoleSender().sendMessage("Something exception");
                        e.printStackTrace();
                    }
                }
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "BTCCRAFT INFO: " + ChatColor.RESET + "txid: " + ChatColor.AQUA + tx.getTxId() + ChatColor.RESET + " has 1 confirmation");
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "BTCCRAFT INFO: " + ChatColor.RESET + "Received: " + tx.getValueSentToMe(wallet).toFriendlyString() + ChatColor.RESET + " from txid: " + ChatColor.AQUA + tx.getTxId());
            }
        });

        //first try current release (after adding event listener) added peergroup.addwallet to all statements, maybe thatll work?, if not then might have to do full Wallet setup for each wallet created
        //NEED TO CHANGE OTHER CONSTRUCTORS EVENT LISTENERS THEY ARE COPY PASTED RN!!!!!!!!!!!!!!!!!!!
        //add wallet listener
        //look into wallet.saveToFile and restoring that, that prob has all transactions of a wallet stored so we can get them on restarts/reloads/rejoins
        //might need to step away from WalletAppKit and have a whole instance for each and every wallet?, have to look into RestoreFromSeed.java
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

    public Player getPlayer() {
        return player;
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

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }
}
