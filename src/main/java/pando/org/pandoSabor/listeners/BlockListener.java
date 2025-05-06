package pando.org.pandoSabor.listeners;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pando.org.pandoSabor.PandoSabor;
import pando.org.pandoSabor.game.TablistDisplayAdapter;
import pando.org.pandoSabor.playerData.SaborPlayer;
import pando.org.pandoSabor.playerData.economy.WealthBlock;
import pando.org.pandoSabor.playerData.economy.WealthBlockStorage;

import java.util.Random;
import java.util.UUID;
import java.util.function.Supplier;

public class BlockListener implements Listener {

    private final PandoSabor plugin;

    private final WealthBlockStorage wealthBlockStorage;

    public BlockListener(PandoSabor plugin) {
        this.plugin = plugin;
        this.wealthBlockStorage = plugin.getWealthBlockStorage();
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        Player player = event.getPlayer();

        if(event.getBlock().getType().equals(Material.DIAMOND_BLOCK)){
            WealthBlock wealthBlock = new WealthBlock(player.getUniqueId(),player.getWorld().getName(),event.getBlock().getLocation(),"DIAMOND_BLOCK");
            wealthBlockStorage.saveBlock(wealthBlock);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        Player player = event.getPlayer();

        if(event.getBlock().getType().equals(Material.DIAMOND_BLOCK)){
            WealthBlock wealthBlock = wealthBlockStorage.getBlock(player.getWorld().getName(),event.getBlock().getLocation());

            if(wealthBlock != null){
                wealthBlockStorage.removeBlock(player.getWorld().getName(),event.getBlock().getLocation());
                if(!player.getUniqueId().equals(wealthBlock.getOwnerUuid())){
                    Player stolenFrom = Bukkit.getPlayer(wealthBlock.getOwnerUuid());

                   if(stolenFrom != null && stolenFrom.isOnline()){
                       stolenFrom.sendMessage(ChatColor.WHITE + player.getName() + ChatColor.RED + " te ha robado");
                       plugin.getInfamyManager().addInfamy(player.getUniqueId(),3);
                   }
                }
            }
        }
    }
}
