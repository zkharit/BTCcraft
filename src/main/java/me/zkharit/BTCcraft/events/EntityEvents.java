package me.zkharit.BTCcraft.events;

import me.zkharit.BTCcraft.BTCcraft;
import me.zkharit.BTCcraft.BTCcraftWallet;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class EntityEvents implements Listener {
    BTCcraft btccraft;

    public EntityEvents(BTCcraft b){
        btccraft = b;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        if(!player.hasPlayedBefore()){
            generatePlayerWallet(btccraft, player);
        }
    }

    private void generatePlayerWallet(BTCcraft b, Player player){
        if(b.getGenPlayerWallets()) {
            BTCcraftWallet wallet = b.generatePlayerWallet(player);
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "INFO:"+ ChatColor.RESET+ "generated " + player.getName() + " wallet address: " + wallet.getDespoitAddress());
        }else{
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "INFO:"+ ChatColor.RESET+ "skipping player address creation disabled in config");
        }
    }


}
