package me.zkharit.BTCcraft;

import me.zkharit.BTCcraft.events.EntityEvents;
import me.zkharit.BTCcraft.commands.WalletCommand;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class BTCcraft extends JavaPlugin{

    private boolean genPlayerWallets;
    private boolean useDatabase;

    private String adminAddress;

    private String username;
    private String password;
    private String host;
    private int port;
    private String dbname;

    private Connection connection;

    private File walletsFile;
    private FileWriter playerwalletsWriter;

    @Override
    @SuppressWarnings("unchecked")
    public void onEnable(){
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

        if(useDatabase){
            username = config.getString("username");
            password = config.getString("password");
            host = config.getString("host");
            port = config.getInt("port");
            dbname = config.getString("dbname");

            try{
                openConnection();
            } catch (ClassNotFoundException e){
                //if db fails then revert to using playerwallets.json for wallet storage
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "ERROR: " + ChatColor.RESET + "MySQL database specified might not have the jdbc driver installed, defaulting to .json file usage");
                useDatabase = false;
                e.printStackTrace();
            } catch (SQLException e){
                //if db fails then revert to using playerwallets.json for wallet storage
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "ERROR: " + ChatColor.RESET + "SQLException");
                useDatabase = false;
                e.printStackTrace();
            }
        }

        //if opening db failed, or plugin set to use .json file
        if(!useDatabase){
            walletsFile = new File(getDataFolder(), "playerwallets.json");
            //create the file if it doesn't already exist
            if (!walletsFile.exists()) {
                try {
                    if(walletsFile.createNewFile());
                    else{
                        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "ERROR: " + ChatColor.RESET + "Error creating playerwallets.json");
                        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "ERROR: " + ChatColor.RESET + "Either create the file playerwallets.json in your plugins/BTCcraft folder or experience unintended issues");
                        //print line number for debugging purposes for server runner
                        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "ERROR: " + ChatColor.RESET + "Line Number: " + Thread.currentThread().getStackTrace()[1].getLineNumber());
                    }
                }catch(IOException e){
                    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "ERROR: " + ChatColor.RESET + "Error creating playerwallets.json");
                    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "ERROR: " + ChatColor.RESET + "Either create the file playerwallets.json in your plugins/BTCcraft folder or experience unintended issues");
                    //print line number for debugging purposes for server runner
                    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "ERROR: " + ChatColor.RESET + "Line Number: " + Thread.currentThread().getStackTrace()[1].getLineNumber());
                    e.printStackTrace();
                }

                JSONArray walletsArray = new JSONArray();
                JSONObject adminJSON = new JSONObject();

                BTCcraftWallet adminWallet = generateAdminWallet();
                adminAddress = adminWallet.getDepositAddress();

                adminJSON.put("UUID", "admin");
                adminJSON.put("address", /*adminWallet.getDepositAddress()*/"12345");

                walletsArray.add(adminJSON);

                try{
                    playerwalletsWriter = new FileWriter(walletsFile.getPath());
                    playerwalletsWriter.write(walletsArray.toString());
                    playerwalletsWriter.close();
                }catch(IOException e){
                    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "ERROR: " + ChatColor.RESET + "Failed writing to playerwallets.json");
                    e.printStackTrace();
                }
            }
        }

        //set command executors
        this.getCommand("wallet").setExecutor(new WalletCommand());
        /*
        this.getCommand("sendaddress").setExecutor(new SendAddressCommand());
        this.getCommand("sendplayer").setExecutor(new SendPlayerCommand());
        this.getCommand("settxfee").setExecutor(new SetTXFeeCommand());
        this.getCommand("adminsendplayer").setExecutor(new AdminSendPlayerCommand());
        this.getCommand("adminsendaddress").setExecutor(new AdminSendAddressCommand());
        this.getCommand("setadmintxfee").setExecutor(new SetAdminTXFeeCommand());
        this.getCommand("setaddress").setExecutor(new SetAddressCommand());
        this.getCommand("withdraw").setExecutor(new WithdrawCommand());
        this.getCommand("generateaddress").setExecutor(new GenerateAddressCommand());
        this.getCommand("getplayeraddress").setExecutor(new GetPlayerAddressCommand());
         */


        //send plugin instance into entity events, for using config information
        getServer().getPluginManager().registerEvents(new EntityEvents(this), this);
        getServer().getPluginManager();

    }

    @Override
    public void onDisable(){

    }

    public BTCcraftWallet generatePlayerWallet(Player player){

        if(isUseDatabase()){

        }else{

        }

        return null;
    }

    public BTCcraftWallet generateAdminWallet(){
        return null;
    }


    public void openConnection() throws SQLException, ClassNotFoundException {
        if (connection != null && !connection.isClosed()) {
            return;
        }

        synchronized (this) {
            if (connection != null && !connection.isClosed()) {
                return;
            }
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.dbname, this.username, this.password);
        }
    }

    public boolean isGenPlayerWallets() {
        return genPlayerWallets;
    }

    public boolean isUseDatabase() {
        return useDatabase;
    }

    public String getAdminAddress() {
        return adminAddress;
    }

    public Connection getConnection() {
        return connection;
    }

    public FileWriter getPlayerwalletsWriter() {
        return playerwalletsWriter;
    }

    @SuppressWarnings("unchecked")
    public boolean appendToWallets(String UUID){
        try{
            JSONParser parser = new JSONParser();
            JSONArray wallets = (JSONArray) parser.parse(new FileReader(walletsFile.getPath()));

            JSONObject testObject = new JSONObject();

            testObject.put("UUID","67890");
            testObject.put("deposit","67890");
            testObject.put("set","");

            wallets.add(testObject);

            playerwalletsWriter = new FileWriter(walletsFile.getPath());
            playerwalletsWriter.append(wallets.toString());
            playerwalletsWriter.close();
        }catch(IOException e){
            e.printStackTrace();
        } catch (ParseException e) {
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "ERROR: Parse Error");
            e.printStackTrace();
        }
        return false;
    }
}
