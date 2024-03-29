package me.zkharit.BTCcraft.events;

import me.zkharit.BTCcraft.BTCcraft;

import me.zkharit.BTCcraft.BTCcraftWallet;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EntityEvents implements Listener {
    BTCcraft btccraft;

    public EntityEvents(BTCcraft b){
        btccraft = b;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();

        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "BTCCRAFT INFO: "+ ChatColor.AQUA + player.getName() + ChatColor.RESET + " has joined the server");
        btccraft.addToPlayerCache(player, player.getUniqueId());

        if(!player.hasPlayedBefore()){
            generatePlayerWallet(btccraft, player);
        }else{
            getPlayerWalletFromJSON(btccraft, player);
        }

    }

    private void getPlayerWalletFromJSON(BTCcraft b, Player player) {
        BTCcraftWallet wallet = b.getPlayerWalletFromJSON(player);

        if(wallet == null){
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "BTCCRAFT INFO: " + ChatColor.AQUA  + player.getName() + ChatColor.RESET + " does not have a wallet");
        }else{
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "BTCCRAFT INFO: " + ChatColor.AQUA + "Added " + player.getName() + "'s" + ChatColor.RESET + " wallet to the wallet cache");
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event){
        Player player = event.getPlayer();
        BTCcraftWallet b = btccraft.getBTCcrafWalletFromCache(player);
        if(b != null){
            b.savePlayerWallet(btccraft);
            btccraft.removeFromWalletCache(player);
        }

        btccraft.removeFromPlayerCache(player);
    }

    private void generatePlayerWallet(BTCcraft b, Player player){
        if(b.isGenPlayerWallets()) {
            BTCcraftWallet wallet = b.generatePlayerWallet(player);
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "BTCCRAFT INFO: "+ ChatColor.AQUA + "Generated " + player.getName() + ChatColor.RESET + " wallet address: " + wallet.getDepositaddress());
        }else{
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "BTCCRAFT INFO: "+ ChatColor.RESET + "Skipping player address creation disabled in config");
        }
    }
}
