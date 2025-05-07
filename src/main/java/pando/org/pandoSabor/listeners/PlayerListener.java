package pando.org.pandoSabor.listeners;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import pando.org.pandoSabor.PandoSabor;
import pando.org.pandoSabor.game.TablistDisplayAdapter;
import pando.org.pandoSabor.playerData.SaborPlayer;

import java.util.Random;
import java.util.UUID;
import java.util.function.Supplier;

public class PlayerListener implements Listener {

    private final PandoSabor plugin;

    public PlayerListener(PandoSabor plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerLogin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        plugin.getAdvancementManager().createPlayerTab(player);

        plugin.getSaborManager().startPlayer(uuid);

        startTab(player);
    }

    private void startTab(@NotNull Player player) {
        Bukkit.getScheduler().runTaskLater(plugin,r -> {

            SaborPlayer revision = plugin.getSaborManager().getPlayer(player.getUniqueId());
            Supplier<SaborPlayer> saborPlayerSupplier = () -> revision;

            if(saborPlayerSupplier.get() == null){
                saborPlayerSupplier = () -> plugin.getSaborPlayerStorage().load(player.getUniqueId());
            }

            TablistDisplayAdapter.startLiveTablist(player,saborPlayerSupplier,plugin);
        },40L);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        Player player = event.getPlayer();
        Player killer = player.getKiller();

        if(killer == null) return;

        processInfamy(killer,player);

    }

    private void processInfamy(Player killer, Player victim) {
        int victimInfamy = plugin.getSaborPlayerStorage().load(victim.getUniqueId()).getInfamy();

        plugin.getInfamyManager().addInfamy(killer.getUniqueId(), 5);
        plugin.getInfamyDisplayManager().checkPlayer(killer);

        if (victimInfamy >= 10) {
            int diamonds = (int) (1 + 0.25 * victimInfamy);
            plugin.getLogger().warning("Diamonds = " + diamonds);
            Random random = new Random();
            while (diamonds > 0) {
                Location dropLoc = victim.getLocation().clone().add(
                        (random.nextDouble() - 0.5) * 0.5,
                        0.5,
                        (random.nextDouble() - 0.5) * 0.5
                );
                victim.getWorld().dropItem(dropLoc, new ItemStack(Material.DIAMOND_BLOCK));
                diamonds--;
            }

        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        plugin.getSaborManager().closePlayer(event.getPlayer().getUniqueId());
    }


    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event){
        SaborPlayer saborPlayer =  plugin.getSaborManager().getPlayer(event.getPlayer().getUniqueId());
        saborPlayer.addDeath();
        plugin.getSaborPlayerStorage().save(saborPlayer);
        plugin.getInfamyManager().reduceInfamy(saborPlayer.getUuid(),saborPlayer.getInfamy());
    }

    public static void teleportPlayerRandomly(Player player) {
        Random random = new Random();

        Location center = new Location(player.getWorld(), -183, 88, 399);

        for (int attempt = 0; attempt < 50; attempt++) { // Intenta 50 veces
            int offsetX = random.nextInt(2001) - 1000; // Rango -1000 a +1000
            int offsetZ = random.nextInt(2001) - 1000;

            int x = center.getBlockX() + offsetX;
            int z = center.getBlockZ() + offsetZ;

            World world = player.getWorld();
            int y = world.getHighestBlockYAt(x, z);
            Block block = world.getBlockAt(x, y - 1, z); // Bloque donde se parará

            if (block.getType().isSolid()) {
                Location tpLocation = new Location(world, x + 0.5, y, z + 0.5); // +0.5 para centrar al jugador
                player.teleport(tpLocation);
                player.sendMessage(ChatColor.GREEN + "¡Has sido teletransportado!");
                return;
            }
        }

        player.sendMessage(ChatColor.RED + "No se encontró una ubicación segura tras 50 intentos.");
    }
}
