package me.zkharit.BTCcraft;

import org.bitcoinj.core.*;
import org.bitcoinj.net.discovery.DnsDiscovery;
import org.bitcoinj.script.Script;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.store.MemoryBlockStore;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.KeyChainGroup;
import org.bitcoinj.wallet.UnreadableWalletException;
import org.bitcoinj.wallet.Wallet;

import org.bitcoinj.wallet.listeners.WalletCoinsReceivedEventListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class BTCcraftWallet extends Wallet {
    private Address depositaddress;
    private Address setaddress;
    private long fee = 10;
    private String mnemonic;
    private Wallet wallet;
    private Player player;
    private long creationTime;
    File walletDirectory = new File("plugins/BTCcraft/wallets/");
    private PeerGroup peerGroup;
    private NetworkParameters parameters;


    public BTCcraftWallet(NetworkParameters params, KeyChainGroup keyChainGroup, BTCcraft btccraft) {
        super(params, keyChainGroup);
        //admin wallet constructor

        parameters = params;

        final File adminWalletFile = new File(walletDirectory, "admin.wallet");
        try {
            if (!adminWalletFile.exists()) {
                walletDirectory.mkdir();
                adminWalletFile.createNewFile();
            }
        }catch(IOException e){
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "BTCCRAFT ERROR: " + ChatColor.RESET + "Error creating admin wallet file");
            e.printStackTrace();
        }

        //create deterministic wallet so we can restore at a later date
        wallet = Wallet.createDeterministic(parameters, Script.ScriptType.P2PKH);
        BlockStore blockStore = new MemoryBlockStore(parameters);

        BlockChain chain = null;

        try {
            chain = new BlockChain(parameters, wallet, blockStore);
        }catch(BlockStoreException e){
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "BTCCRAFT ERROR: " + ChatColor.RESET + "Error creating blockchain for admin wallet");
            e.printStackTrace();
        }

        peerGroup = new PeerGroup(parameters, chain);

        peerGroup.addWallet(wallet);
        chain.addWallet(wallet);

        BukkitRunnable r = new BukkitRunnable() {
            @Override
            public void run() {
                peerGroup.setFastCatchupTimeSecs(wallet.getEarliestKeyCreationTime());
                peerGroup.addPeerDiscovery(new DnsDiscovery(parameters));
                peerGroup.startAsync();
                peerGroup.downloadBlockChain();

                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "BTCCRAFT INFO: " + "Finished Restoring Admin wallet, you can now use BTCcraft commands");
            }
        };

        r.runTaskAsynchronously(btccraft);

        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "BTCCRAFT INFO: " + ChatColor.RESET + "Finished creating wallet and downloading blockchain");

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

                try {
                    wallet.saveToFile(adminWalletFile);
                } catch (IOException e) {
                    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "BTCCRAFT ERROR: " + ChatColor.RESET + "Error saving to admin wallet file");
                    e.printStackTrace();
                }
            }
        });

        try {
            wallet.saveToFile(adminWalletFile);
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.BLUE + "Saved Wallet");
        }catch(IOException e){
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "BTCCRAFT ERROR: " + ChatColor.RESET + "Error writing to admin wallet file");
            e.printStackTrace();
        }
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

    public BTCcraftWallet(NetworkParameters params, KeyChainGroup keyChainGroup, Player p, BTCcraft btccraft) {
        super(params, keyChainGroup);
        //normal player wallet constructor

        parameters = params;
        player = p;

        final File playerWalletFile  = new File(walletDirectory, btccraft.getUUIDFromCache(player) +".wallet");

        try {
            if (!playerWalletFile.exists()) {
                playerWalletFile.createNewFile();
            }
        }catch(IOException e){
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "BTCCRAFT ERROR: " + ChatColor.RESET + "Error creating player wallet file");
            e.printStackTrace();
        }

        //create deterministic wallet so we can restore at a later date
        wallet = Wallet.createDeterministic(parameters, Script.ScriptType.P2PKH);

        BlockStore blockStore = new MemoryBlockStore(parameters);

        BlockChain chain = null;

        try {
            chain = new BlockChain(parameters, wallet, blockStore);
        }catch(BlockStoreException e){
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "BTCCRAFT ERROR: " + ChatColor.RESET + "Error creating blockchain for player wallet");
            e.printStackTrace();
        }

        peerGroup = new PeerGroup(parameters, chain);

        peerGroup.addWallet(wallet);
        chain.addWallet(wallet);

        BukkitRunnable r = new BukkitRunnable() {
            @Override
            public void run() {
                peerGroup.setFastCatchupTimeSecs(wallet.getEarliestKeyCreationTime());
                peerGroup.addPeerDiscovery(new DnsDiscovery(parameters));
                peerGroup.startAsync();
                peerGroup.downloadBlockChain();

                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "BTCCRAFT INFO: " + "Finished Restoring Admin wallet, you can now use BTCcraft commands");
            }
        };

        r.runTaskAsynchronously(btccraft);

        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "BTCCRAFT INFO: " + ChatColor.RESET + "Finished creating wallet and downloading blockchain");

        DeterministicSeed seed = wallet.getKeyChainSeed();
        creationTime = seed.getCreationTimeSeconds();
        mnemonic = Utils.SPACE_JOINER.join(seed.getMnemonicCode());
        depositaddress = wallet.currentReceiveAddress();
        setaddress = depositaddress;

        wallet.addCoinsReceivedEventListener(new WalletCoinsReceivedEventListener() {
            @Override
            public void onCoinsReceived(Wallet wallet, Transaction tx, Coin coin, Coin coin1) {
                player.sendMessage(ChatColor.YELLOW + "Transaction Alert! : " + ChatColor.RESET + "Your wallet received a transaction, waiting for confirmation, checking once a minute");

                while(tx.getConfidence().getDepthInBlocks() < 1){
                    player.sendMessage(ChatColor.YELLOW + "TXid: " + ChatColor.AQUA + tx.getTxId() + ChatColor.YELLOW + " still awaiting confirmation");

                    try {
                        TimeUnit.MINUTES.sleep(1);
                    } catch (InterruptedException e) {
                        Bukkit.getServer().getConsoleSender().sendMessage("Something exception");
                        e.printStackTrace();
                    }
                }

                try {
                    wallet.saveToFile(playerWalletFile);
                } catch (IOException e) {
                    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "BTCCRAFT ERROR: " + ChatColor.RESET + "Error saving to player wallet file");
                    e.printStackTrace();
                }

                player.sendMessage(ChatColor.YELLOW + "TXid: " + ChatColor.AQUA + tx.getTxId() + ChatColor.YELLOW + " has 1 confirmation");
                player.sendMessage(ChatColor.YELLOW + "Received: " + ChatColor.AQUA + tx.getValueSentToMe(wallet).toFriendlyString() + ChatColor.YELLOW + " from txid: " + ChatColor.AQUA + tx.getTxId());
            }
        });

        try {
            wallet.saveToFile(playerWalletFile);
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.BLUE + "Saved Wallet");
        }catch(IOException e){
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "BTCCRAFT ERROR: " + ChatColor.RESET + "Error writing to admin wallet file");
            e.printStackTrace();
        }

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

    public BTCcraftWallet(NetworkParameters params, KeyChainGroup keyChainGroup, Player p, Address da, Address sa, String mnemonicSeed, long ctime, long f, BTCcraft btccraft){
        super(params, keyChainGroup);
        //player wallet restore

        parameters = params;
        depositaddress = da;
        setaddress = sa;
        mnemonic = mnemonicSeed;
        creationTime = ctime;
        fee = f;
        player = p;

        final File playerWalletFile = new File(walletDirectory, btccraft.getUUIDFromCache(player) + ".wallet");

        try {
            DeterministicSeed d = new DeterministicSeed(mnemonic, null, "", creationTime);
            wallet = Wallet.fromSeed(parameters, d, Script.ScriptType.P2PKH);

        }catch(UnreadableWalletException e){
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "BTCCRAFT ERROR: " + ChatColor.RESET + "Could not restore " + ChatColor.YELLOW + player.getName() + "'s " + ChatColor.RESET + "wallet");
            player.sendMessage(ChatColor.RED + "BTCCRAFT ERROR: " + ChatColor.RESET + "Could not restore " + ChatColor.YELLOW + player.getName() + "'s " + ChatColor.RESET + "wallet, contact an admin");
            e.printStackTrace();
        }

        wallet.clearTransactions(0);

        BlockStore blockStore = new MemoryBlockStore(parameters);
        BlockChain chain = null;

        try {
            chain = new BlockChain(parameters, wallet, blockStore);
        }catch(BlockStoreException e){
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "BTCCRAFT ERROR: " + ChatColor.RESET + "Error creating blockchain for player wallet");
            e.printStackTrace();
        }

        peerGroup = new PeerGroup(parameters, chain);

        peerGroup.addWallet(wallet);
        chain.addWallet(wallet);

        BukkitRunnable r = new BukkitRunnable() {
            @Override
            public void run() {
                peerGroup.setFastCatchupTimeSecs(wallet.getEarliestKeyCreationTime());
                peerGroup.addPeerDiscovery(new DnsDiscovery(parameters));
                peerGroup.startAsync();
                peerGroup.downloadBlockChain();

                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "BTCCRAFT INFO: " + "Finished restoring player wallet, you can now use BTCcraft commands");
            }
        };

        r.runTaskAsynchronously(btccraft);

        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "BTCCRAFT INFO: " + ChatColor.RESET + "Finished restoring wallet and downloading blockchain");

        wallet.addCoinsReceivedEventListener(new WalletCoinsReceivedEventListener() {
            @Override
            public void onCoinsReceived(Wallet wallet, Transaction tx, Coin coin, Coin coin1) {
                player.sendMessage(ChatColor.YELLOW + "Transaction Alert! : " + ChatColor.RESET + "Your wallet received a transaction, waiting for confirmation, checking once a minute");

                while(tx.getConfidence().getDepthInBlocks() < 1){
                    player.sendMessage(ChatColor.YELLOW + "TXid: " + ChatColor.AQUA + tx.getTxId() + ChatColor.YELLOW + " still awaiting confirmation");

                    try {
                        TimeUnit.MINUTES.sleep(1);
                    } catch (InterruptedException e) {
                        Bukkit.getServer().getConsoleSender().sendMessage("Something exception");
                        e.printStackTrace();
                    }
                }

                try {
                    wallet.saveToFile(playerWalletFile);
                } catch (IOException e) {
                    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "BTCCRAFT ERROR: " + ChatColor.RESET + "Error saving to player wallet file");
                    e.printStackTrace();
                }

                player.sendMessage(ChatColor.YELLOW + "TXid: " + ChatColor.AQUA + tx.getTxId() + ChatColor.YELLOW + " has 1 confirmation");
                player.sendMessage(ChatColor.YELLOW + "Received: " + ChatColor.AQUA + tx.getValueSentToMe(wallet).toFriendlyString() + ChatColor.YELLOW + " from txid: " + ChatColor.AQUA + tx.getTxId());
            }
        });
    }

    public BTCcraftWallet(NetworkParameters params, KeyChainGroup keyChainGroup, Address da, String mnemonicSeed, long ctime, long f, BTCcraft btccraft){
        super(params, keyChainGroup);
        //admin wallet restore

        parameters = params;
        depositaddress = da;
        setaddress = da;
        mnemonic = mnemonicSeed;
        creationTime = ctime;
        fee = f;

        final File adminWalletFile = new File(walletDirectory, "admin.wallet");
        /*try {
            wallet = Wallet.loadFromFile(adminWalletFile);
        }catch (UnreadableWalletException e){
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "BTCCRAFT ERROR: " + ChatColor.RESET + "Error restoring Admin wallet from file");
            e.printStackTrace();
        }*/


        try {
            DeterministicSeed seed = new DeterministicSeed(mnemonic, null, "", creationTime);
            wallet = Wallet.fromSeed(parameters, seed, Script.ScriptType.P2PKH);
        } catch (UnreadableWalletException e) {
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "BTCCRAFT ERROR: " + ChatColor.RESET + "Error restoring Admin wallet from seed");
            e.printStackTrace();
        }

        wallet.clearTransactions(0);

        BlockStore blockStore = new MemoryBlockStore(parameters);
        BlockChain chain = null;

        try {
            chain = new BlockChain(parameters, wallet, blockStore);
        }catch(BlockStoreException e){
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "BTCCRAFT ERROR: " + ChatColor.RESET + "Error creating blockchain for admin wallet");
            e.printStackTrace();
        }

        peerGroup = new PeerGroup(parameters, chain);

        peerGroup.addWallet(wallet);
        chain.addWallet(wallet);

        BukkitRunnable r = new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "Beginning admin wallet restore");

                peerGroup.setFastCatchupTimeSecs(wallet.getEarliestKeyCreationTime());
                peerGroup.addPeerDiscovery(new DnsDiscovery(parameters));
                peerGroup.startAsync();
                peerGroup.downloadBlockChain();

                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "BTCCRAFT INFO: " + "Finished restoring admin wallet, you can now use BTCcraft commands");
            }
        };

        r.runTaskAsynchronously(btccraft);

        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "BTCCRAFT INFO: " + ChatColor.RESET + "Finished restoring wallet and downloading blockchain");

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

                try {
                    wallet.saveToFile(adminWalletFile);
                } catch (IOException e) {
                    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "BTCCRAFT ERROR: " + ChatColor.RESET + "Error saving to admin wallet file");
                    e.printStackTrace();
                }
            }
        });

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

    public void setSetaddress(Address sa) {
        this.setaddress = sa;
    }

    public void setFee(long fee) {
        this.fee = fee;
    }

    public PeerGroup getPeerGroup() {
        return peerGroup;
    }

    public NetworkParameters getParameters() {
        return parameters;
    }

    public void savePlayerWallet(BTCcraft btccraft){
        final File playerWalletFile = new File(walletDirectory, btccraft.getUUIDFromCache(player) + ".wallet");

        try {
            wallet.saveToFile(playerWalletFile);
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.BLUE + "Saved Wallet");
        }catch(IOException e){
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "BTCCRAFT ERROR: " + ChatColor.RESET + "Error writing to player wallet file");
            e.printStackTrace();
        }
    }
}
