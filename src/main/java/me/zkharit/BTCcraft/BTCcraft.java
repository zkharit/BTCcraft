package me.zkharit.BTCcraft;

import me.zkharit.BTCcraft.commands.*;
import me.zkharit.BTCcraft.events.EntityEvents;
import me.zkharit.BTCcraft.events.ServerEvents;

import org.bitcoinj.core.*;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.wallet.KeyChainGroup;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.wallet.listeners.WalletCoinsReceivedEventListener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.util.HashMap;
import java.util.UUID;

public class BTCcraft extends JavaPlugin{
    public HashMap<Player, UUID> playerCache = new HashMap<Player, UUID>();
    public HashMap<Player, BTCcraftWallet> walletCache = new HashMap<Player, BTCcraftWallet>();

    private boolean genPlayerWallets;
    private boolean useDatabase;

    private Address adminAddress;

    private Connection connection;
    private String username;
    private String password;
    private String host;
    private int port;
    private String dbname;

    private File configFile;
    private File walletsFile;
    private FileWriter playerwalletsWriter;

    private File walletsDirectory = new File(getDataFolder().toString() + "/wallets");

    private NetworkParameters params = new TestNet3Params();
    private String filePrefix = "wallet";
    private WalletAppKit kit;

    private BTCcraft btCcraft = this;

    private boolean allowJoin = true;

    @Override
    @SuppressWarnings("unchecked")
    public void onEnable(){
        configFile = new File(getDataFolder(), "config.yml");
        if(!configFile.exists()){

            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "BTCCRAFT ERROR: " + ChatColor.RESET + "No config file found, generating one, please reload the plugin");
            BukkitRunnable r = new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.getServer().broadcastMessage(ChatColor.RED + "BTCCRAFT ERROR: " + ChatColor.RESET + "No config file found, generating one, please /reload or reload the individual plugin");
                }
            };

            r.runTaskAsynchronously(this);

            try{
                if(getDataFolder().mkdir());
                else{
                    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "BTCCRAFT ERROR: " + ChatColor.RESET + "Error creating BTCcraft directory");
                    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "BTCCRAFT ERROR: " + ChatColor.RESET + "Either create the plugins/BTCcraft folder or experience unintended issues");
                    this.getPluginLoader().disablePlugin(this);
                }
                if(configFile.createNewFile());
                else{
                    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "BTCCRAFT ERROR: " + ChatColor.RESET + "Error creating config.yml");
                    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "BTCCRAFT ERROR: " + ChatColor.RESET + "Either create the file config.yml in your plugins/BTCcraft folder or experience unintended issues");
                    //print line number for debugging purposes for server runner
                    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "BTCCRAFT ERROR: " + ChatColor.RESET + "Line Number: " + Thread.currentThread().getStackTrace()[1].getLineNumber());
                    this.getPluginLoader().disablePlugin(this);
                }
            }catch(IOException e){
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "BTCCRAFT ERROR: " + ChatColor.RESET + "Error creating config.yml");
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "BTCCRAFT ERROR: " + ChatColor.RESET + "Either create the file config.yml in your plugins/BTCcraft folder or experience unintended issues");
                //print line number for debugging purposes for server runner
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "BTCCRAFT ERROR: " + ChatColor.RESET + "Line Number: " + Thread.currentThread().getStackTrace()[1].getLineNumber());
                e.printStackTrace();
                this.getPluginLoader().disablePlugin(this);
            }
            this.getPluginLoader().disablePlugin(this);
            return;
        }
        //create config and set default's
        FileConfiguration config = this.getConfig();

        //Determine if player wallets should be generated on first join event
        config.addDefault("Generate Player Wallets", true);
        //Decide between using a MySQL database or a .json file to store user wallet records (Database is recommended)
        config.addDefault("Use Database", false);
        //If you are running a MySQL Database:
        //Database Username
        config.addDefault("username", "");
        //Database Password
        config.addDefault("password", "");
        //Database host
        config.addDefault("host", "");
        //Database port
        config.addDefault("port", 3306);
        //Database name
        config.addDefault("dbname", "");
        config.options().copyDefaults(true);
        saveConfig();

        //Chose to generate wallets for players or not
        genPlayerWallets = config.getBoolean("Generate Player Wallets");

        //use db or not
        useDatabase = config.getBoolean("Use Database");

        startBTCservice();

        if(useDatabase){
            username = config.getString("username");
            password = config.getString("password");
            host = config.getString("host");
            port = config.getInt("port");
            dbname = config.getString("dbname");

            try{
                openConnection();
            }catch (ClassNotFoundException e){
                //if db fails then revert to using playerwallets.json for wallet storage
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "BTCCRAFT ERROR: " + ChatColor.RESET + "MySQL database specified might not have the jdbc driver installed, defaulting to .json file usage");
                useDatabase = false;
                e.printStackTrace();
            }catch (SQLException e){
                //if db fails then revert to using playerwallets.json for wallet storage
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "BTCCRAFT ERROR: " + ChatColor.RESET + "SQLException");
                useDatabase = false;
                e.printStackTrace();
            }
            useDatabase = false;
            //Auto-set as false for now, database storage is not implemented
        }

        //if opening db failed, or plugin set to use .json file
        if(!useDatabase){
            walletsFile = new File(getDataFolder(), "playerwallets.json");
            //create the file if it doesn't already exist
            if(!walletsFile.exists()){
                try{
                    if(walletsFile.createNewFile());
                    else{
                        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "BTCCRAFT ERROR: " + ChatColor.RESET + "Error creating playerwallets.json");
                        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "BTCCRAFT ERROR: " + ChatColor.RESET + "Either create the file playerwallets.json in your plugins/BTCcraft folder or experience unintended issues");
                        //print line number for debugging purposes for server runner
                        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "BTCCRAFT ERROR: " + ChatColor.RESET + "Line Number: " + Thread.currentThread().getStackTrace()[1].getLineNumber());
                    }
                }catch(IOException e){
                    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "BTCCRAFT ERROR: " + ChatColor.RESET + "Error creating playerwallets.json");
                    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "BTCCRAFT ERROR: " + ChatColor.RESET + "Either create the file playerwallets.json in your plugins/BTCcraft folder or experience unintended issues");
                    //print line number for debugging purposes for server runner
                    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "BTCCRAFT ERROR: " + ChatColor.RESET + "Line Number: " + Thread.currentThread().getStackTrace()[1].getLineNumber());
                    e.printStackTrace();
                }

                BTCcraftWallet adminWallet = generateAdminWallet();

                adminAddress = adminWallet.getDepositaddress();
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "BTCCRAFT INFO: " + ChatColor.RESET + "Wallet: " + adminAddress);

                //add admin wallet to cache, use null for admin
                addToWalletCache(null, adminWallet);

                JSONArray walletsArray = new JSONArray();
                JSONObject adminJSON = new JSONObject();

                adminJSON.put("UUID", "admin");
                adminJSON.put("deposit", adminAddress.toString());
                adminJSON.put("fee", Long.toString(adminWallet.getFee()));
                adminJSON.put("mnemonic", adminWallet.getMnemonic());
                adminJSON.put("creationtime", Long.toString(adminWallet.getCreationTime()));

                walletsArray.add(adminJSON);

                try{
                    playerwalletsWriter = new FileWriter(walletsFile.getPath());
                    playerwalletsWriter.write(walletsArray.toString());
                    playerwalletsWriter.close();
                }catch(IOException e){
                    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "BTCCRAFT ERROR: " + ChatColor.RESET + "Failed writing to playerwallets.json");
                    e.printStackTrace();
                }

                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "BTCCRAFT INFO: " + ChatColor.RESET + "BTCcraft enabled, safe to rejoin server");
            }else{
                getAdminWalletFromJSON();
            }
        }

        //set command executors
        this.getCommand("wallet").setExecutor(new WalletCommand(this));
        this.getCommand("sendaddress").setExecutor(new SendAddressCommand(kit, this));
        this.getCommand("sendplayer").setExecutor(new SendPlayerCommand());
        this.getCommand("settxfee").setExecutor(new SetTXFeeCommand());
        this.getCommand("adminsendplayer").setExecutor(new AdminSendPlayerCommand());
        this.getCommand("adminsendaddress").setExecutor(new AdminSendAddressCommand(kit));
        this.getCommand("setadmintxfee").setExecutor(new SetAdminTXFeeCommand());
        this.getCommand("setaddress").setExecutor(new SetAddressCommand(this));
        this.getCommand("withdraw").setExecutor(new WithdrawCommand(this));
        this.getCommand("generateaddress").setExecutor(new GenerateAddressCommand(this));
        this.getCommand("getplayeraddress").setExecutor(new GetPlayerAddressCommand(this));
        this.getCommand("getmnemonic").setExecutor(new GetMnemonicCommand(this));

        //send plugin instance into entity events, for using config information
        getServer().getPluginManager().registerEvents(new EntityEvents(this), this);
        getServer().getPluginManager().registerEvents(new ServerEvents(this), this);

    }

    @Override
    public void onDisable(){
        //write cache to .json file or db
        //kit.stopAsync();
    }

    private void startBTCservice(){
        kit = new WalletAppKit(params, walletsDirectory, filePrefix) {
            @Override
            protected void onSetupCompleted() {
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "BTCCRAFT INFO: " + ChatColor.RESET + "WalletAppKit finished initializing");
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "BTCCRAFT INFO: " + ChatColor.RESET + "Finishing Setup, " + ChatColor.RED + ChatColor.UNDERLINE + ChatColor.BOLD + "please wait until safe to join message appears...");
            }
        };

        for(Player p : getServer().getOnlinePlayers()){
            p.kickPlayer(ChatColor.YELLOW + "BTCCRAFT INFO: " + ChatColor.RESET + "BTCCRAFT initializing, " + ChatColor.RED + ChatColor.UNDERLINE + ChatColor.BOLD + "please wait to join (check console)");
        }

        allowJoin = false;

        kit.startAsync();
        kit.awaitRunning();

        allowJoin = true;
    }

    public BTCcraftWallet generateAdminWallet(){
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "BTCCRAFT INFO: " + ChatColor.RESET + "Generating Admin wallet address...");
        BTCcraftWallet adminWallet = new BTCcraftWallet(params,KeyChainGroup.createBasic(params));

        kit.chain().addWallet(adminWallet.getWallet());
        kit.peerGroup().addWallet(adminWallet.getWallet());

        return adminWallet;
    }

    public BTCcraftWallet generatePlayerWallet(Player player){
        //generate the new wallet
        BTCcraftWallet playerWallet = new BTCcraftWallet(params, KeyChainGroup.createBasic(params), player);

        //add to the WalletAppKit
        kit.chain().addWallet(playerWallet.getWallet());
        kit.peerGroup().addWallet(playerWallet.getWallet());

        //Add to the wallet cache, adding to player cache is handled in EntityEvent onPlayerJoin
        addToWalletCache(player, playerWallet);

        //Add to the wallets file
        appendToWallets(getUUIDFromCache(player), playerWallet);

        return playerWallet;
        /*Address a;
        kit = new WalletAppKit(params, new File(getDataFolder().toString() + "/wallets"), filePrefix){
            @Override
            protected void onSetupCompleted() {
                // This is called in a background thread after startAndWait is called, as setting up various objects
                // can do disk and network IO that may cause UI jank/stuttering in wallet apps if it were to be done
                // on the main thread.
                if (wallet().getKeyChainGroupSize() < 1)
                    wallet().importKey(new ECKey());
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "BTCCRAFT INFO: " + ChatColor.RESET + "WalletAppKit finished initializing");
            }
        };

        kit.startAsync();
        kit.awaitRunning();

        a = kit.wallet().currentReceiveAddress();

        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "BTCCRAFT INFO: " + ChatColor.RESET + "Wallet: " + a.toString());

        */
    }

    public void openConnection() throws SQLException, ClassNotFoundException {
        if(connection != null && !connection.isClosed()) {
            return;
        }

        synchronized (this) {
            if(connection != null && !connection.isClosed()) {
                return;
            }
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.dbname, this.username, this.password);
        }
    }

    public boolean isGenPlayerWallets() {
        return genPlayerWallets;
    }

    public Address getAdminAddress() {
        return adminAddress;
    }

    @SuppressWarnings("unchecked")
    public boolean appendToWallets(UUID uuid, BTCcraftWallet w){
        try{
            JSONParser parser = new JSONParser();
            JSONArray wallets = (JSONArray) parser.parse(new FileReader(walletsFile.getPath()));

            JSONObject testObject = new JSONObject();

            testObject.put("UUID", uuid.toString());
            testObject.put("deposit", w.getDepositaddress().toString());
            testObject.put("set", w.getSetaddress().toString());
            testObject.put("fee", Long.toString(w.getFee()));
            testObject.put("mnemonic", w.getMnemonic());
            testObject.put("creationtime", Long.toString(w.getCreationTime()));

            wallets.add(testObject);

            playerwalletsWriter = new FileWriter(walletsFile.getPath());
            playerwalletsWriter.append(wallets.toString());
            playerwalletsWriter.close();
        }catch(IOException e){
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "BTCCRAFT ERROR: Error appending to playerwallets.json");
            e.printStackTrace();
        } catch (ParseException e) {
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "BTCCRAFT ERROR: Error parsing from playerwallets.json");
            e.printStackTrace();
        }
        return false;
    }

    public UUID getUUIDFromCache(Player player){
        return playerCache.get(player);
    }

    public void addToPlayerCache(Player player, UUID uuid){
        playerCache.put(player, uuid);
    }

    public void removeFromPlayerCache(Player player){
        playerCache.remove(player);
    }

    public BTCcraftWallet getBTCcrafWalletFromCache(Player player){
        return walletCache.get(player);
    }

    public void addToWalletCache(Player player, BTCcraftWallet btCcraftWallet){
        walletCache.put(player, btCcraftWallet);
    }

    public void removeFromWalletCache(Player player){
        walletCache.remove(player);
        //kit.chain().removeWallet(getBTCcrafWalletFromCache(player).getWallet());
    }

    public void updateJSON(String uuid, String attribute, String value){
        try {
            JSONParser parser = new JSONParser();
            JSONArray wallets = (JSONArray) parser.parse(new FileReader(walletsFile.getPath()));

            for(int i = 0; i < wallets.size(); i++){
                JSONObject j = (JSONObject) wallets.get(i);

                if(j.containsValue(uuid)){
                    j.put(attribute, value);

                    wallets.set(i, j);

                    playerwalletsWriter = new FileWriter(walletsFile.getPath());
                    playerwalletsWriter.write(wallets.toString());
                    playerwalletsWriter.close();
                    break;
                }
            }
        }catch(IOException e){
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "BTCCRAFT ERROR: Error updating playerwallets.json");
            e.printStackTrace();
        }catch(ParseException e){
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "BTCCRAFT ERROR: Error parsing from playerwallets.json");
            e.printStackTrace();
        }
    }

    public boolean isAllowJoin() {
        return allowJoin;
    }

    public BTCcraftWallet getPlayerWalletFromJSON(Player player){
        UUID uuid = getUUIDFromCache(player);

        try{
            JSONParser parser = new JSONParser();
            JSONArray wallets = (JSONArray)parser.parse(new FileReader(walletsFile.getPath()));

            for(int i = 0; i < wallets.size(); i++){
                JSONObject j = (JSONObject)wallets.get(i);

                if(j.containsValue(uuid.toString())){

                    String mnemonic = (String) j.get("mnemonic");
                    long creationTime = Long.parseLong((String) j.get("creationtime"));
                    Address depositAddress = LegacyAddress.fromString(params, (String) j.get("deposit"));
                    Address setAddress = LegacyAddress.fromString(params, (String) j.get("set"));
                    long fee = Long.parseLong((String) j.get("fee"));

                    BTCcraftWallet b = new BTCcraftWallet(params, KeyChainGroup.createBasic(params), player, depositAddress, setAddress, mnemonic, creationTime, fee);

                    addToWalletCache(player, b);

                    kit.chain().addWallet(b.getWallet());
                    kit.peerGroup().addWallet(b.getWallet());
                    return b;
                }
            }

        }catch(IOException e){
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "BTCCRAFT ERROR: Error using playerwallets.json");
            e.printStackTrace();
        }catch(ParseException e){
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "BTCCRAFT ERROR: Error parsing from playerwallets.json");
            e.printStackTrace();
        }

        return null;
    }

    public void getAdminWalletFromJSON(){
        try{
            JSONParser parser = new JSONParser();
            JSONArray wallets = (JSONArray)parser.parse(new FileReader(walletsFile.getPath()));

            for(int i = 0; i < wallets.size(); i++){
                JSONObject j = (JSONObject)wallets.get(i);

                if(j.containsValue("admin")){

                    String mnemonic = (String) j.get("mnemonic");
                    long creationTime = Long.parseLong((String) j.get("creationtime"));
                    Address depositAddress = LegacyAddress.fromString(params, (String) j.get("deposit"));
                    long fee = Long.parseLong((String) j.get("fee"));

                    BTCcraftWallet b = new BTCcraftWallet(params, KeyChainGroup.createBasic(params), depositAddress, mnemonic, creationTime, fee);

                    addToWalletCache(null, b);

                    kit.chain().addWallet(b.getWallet());
                    kit.peerGroup().addWallet(b.getWallet());
                    return;
                }
            }
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "BTCCRAFT ERROR: " + ChatColor.RESET + " couldn't restore admin wallet from playerwallets.json file, please inspect");

        }catch(IOException e){
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "BTCCRAFT ERROR: Error using playerwallets.json");
            e.printStackTrace();
        }catch(ParseException e){
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "BTCCRAFT ERROR: Error parsing from playerwallets.json");
            e.printStackTrace();
        }
    }
}