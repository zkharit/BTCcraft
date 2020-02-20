package me.zkharit.BTCcraft;

import me.zkharit.BTCcraft.events.EntityEvents;
import me.zkharit.BTCcraft.commands.WalletCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class BTCcraft extends JavaPlugin{
    private boolean genPlayerWallets;

    @Override
    public void onEnable(){
        //create config and set default's
        FileConfiguration config = this.getConfig();

        config.addDefault("Admin BTC Address", "");
        config.addDefault("Generate Player Wallets", true);

        //Chose to generate wallets for players or not
        genPlayerWallets = config.getBoolean("Admin BTC Address");

        //Get Admin BTC Address if entered
        if(!config.getString("Admin BTC Address").equals("")){
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.AQUA+config.getString("Admin BTC Address"));
            config.options().copyDefaults(true);
            saveConfig();
        }else{

        }

        //set command executors
        this.getCommand("wallet").setExecutor(new WalletCommand());
        /*
        this.getCommand("sendaddress").setExecutor(new WalletCommand());
        this.getCommand("sendplayer").setExecutor(new WalletCommand());
        this.getCommand("settxfee").setExecutor(new WalletCommand());
        this.getCommand("adminsend").setExecutor(new WalletCommand());
        this.getCommand("setadmintxfee").setExecutor(new WalletCommand());
        this.getCommand("adminsetaddress").setExecutor(new WalletCommand());
        this.getCommand("setaddress").setExecutor(new WalletCommand());
        this.getCommand("withdraw").setExecutor(new WalletCommand());
         */


        //send plugin instance into entity events, for using config information
        getServer().getPluginManager().registerEvents(new EntityEvents(this), this);
        getServer().getPluginManager();

    }

    @Override
    public void onDisable(){

    }

    public BTCcraftWallet generatePlayerWallet(Player player){
        return null;
    }

    public boolean getGenPlayerWallets(){
        return genPlayerWallets;
    }
}
