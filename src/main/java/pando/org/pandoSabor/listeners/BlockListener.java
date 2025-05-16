package pando.org.pandoSabor.listeners;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import pando.org.pandoSabor.PandoSabor;
import pando.org.pandoSabor.discord.DiscordNotifier;
import pando.org.pandoSabor.playerData.economy.WealthBlock;
import pando.org.pandoSabor.database.WealthBlockStorage;

import java.util.List;

public class BlockListener implements Listener {

    private final PandoSabor plugin;

    private final WealthBlockStorage wealthBlockStorage;

    public BlockListener(PandoSabor plugin) {
        this.plugin = plugin;
        this.wealthBlockStorage = plugin.getWealthBlockStorage();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();

        if (!event.getBlock().getType().equals(Material.DIAMOND_BLOCK)) return;

        List<WealthBlock> wealthBlocks = wealthBlockStorage.getAllBlocksByPlayer(player.getUniqueId());
        if (wealthBlocks == null) return;

        if (wealthBlocks.isEmpty()) {
            WealthBlock wealthBlock = new WealthBlock(
                    player.getUniqueId(),
                    player.getWorld().getName(),
                    event.getBlock().getLocation(),
                    "DIAMOND_BLOCK"
            );
            wealthBlockStorage.saveBlock(wealthBlock);
            return;
        }

        WealthBlock lastBlock = wealthBlocks.getLast();
        if (lastBlock == null) return;

        Location lastLocation = lastBlock.getLocation();

        if(lastLocation != null && lastLocation.getWorld().equals(event.getBlock().getWorld())){
            if (lastLocation.distance(event.getBlock().getLocation()) <= 5) {
                WealthBlock wealthBlock = new WealthBlock(
                        player.getUniqueId(),
                        player.getWorld().getName(),
                        event.getBlock().getLocation(),
                        "DIAMOND_BLOCK"
                );
                wealthBlockStorage.saveBlock(wealthBlock);
            }else{
                sendDistanceWarn(player);
                event.setCancelled(true);
            }
        }else{
            sendDistanceWarn(player);
            event.setCancelled(true);
        }


    }

    private void sendDistanceWarn(Player player) {
        player.sendMessage(ChatColor.GOLD + "[" + ChatColor.YELLOW + "Economía del Sabor" + ChatColor.GOLD + "] "
                + ChatColor.GRAY + "El bloque debe estar máximo a " + ChatColor.RED + "5 "
                + ChatColor.GRAY + "bloques de distancia del anterior"
        );
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        Player player = event.getPlayer();

        if(event.getBlock().getType().equals(Material.DIAMOND_BLOCK)){
            WealthBlock wealthBlock = wealthBlockStorage.getBlock(player.getWorld().getName(),event.getBlock().getLocation());

            if(wealthBlock != null){
                wealthBlockStorage.removeBlock(player.getWorld().getName(),event.getBlock().getLocation());
                if(!player.getUniqueId().equals(wealthBlock.getOwnerUuid())){


                    DiscordNotifier.notifyRobbery(wealthBlock.getOwnerUuid(), player.getName());


                    Player stolenFrom = Bukkit.getPlayer(wealthBlock.getOwnerUuid());



                   if(stolenFrom != null && stolenFrom.isOnline()){
                       stolenFrom.sendMessage(ChatColor.WHITE + player.getName() + ChatColor.RED + " te ha robado");
                       plugin.getInfamyManager().addInfamy(player.getUniqueId(),3);
                       plugin.getInfamyDisplayManager().checkPlayer(player);
                   }
                }
            }
        }
    }
}
