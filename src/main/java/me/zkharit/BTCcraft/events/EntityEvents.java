package me.zkharit.BTCcraft.events;

import me.zkharit.BTCcraft.BTCcraft;

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
        if(!player.hasPlayedBefore()){
            generatePlayerWallet(btccraft, player);
        }
        btccraft.addToPlayerCache(player, player.getUniqueId());
    }

    private void generatePlayerWallet(BTCcraft b, Player player){
        if(b.isGenPlayerWallets()) {
            //BTCcraftWallet wallet = b.generatePlayerWallet(player);
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "BTCCRAFT INFO: "+ ChatColor.AQUA + "generated " + player.getName() + ChatColor.RESET +" wallet address: " /*+ wallet.getDepositAddress()*/);
        }else{
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "BTCCRAFT INFO: "+ ChatColor.RESET + "skipping player address creation disabled in config");
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event){
        Player player = event.getPlayer();
        btccraft.removeFromPlayerCache(player);
        btccraft.removeFromWalletCache(player);
    }


}
