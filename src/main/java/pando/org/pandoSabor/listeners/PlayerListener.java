package pando.org.pandoSabor.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pando.org.pandoSabor.PandoSabor;
import pando.org.pandoSabor.game.TablistDisplayAdapter;
import pando.org.pandoSabor.game.TablistUtils;
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

        Supplier<SaborPlayer> saborPlayerSupplier = () -> plugin.getSaborPlayerStorage().load(player.getUniqueId());


        TablistDisplayAdapter.startLiveTablist(player,saborPlayerSupplier,plugin);

        if(event.getPlayer().hasPlayedBefore()){
            plugin.getSaborManager().startPlayer(uuid);
        }else{
            teleportPlayerRandomly(player);
            plugin.getSaborManager().addSaborPlayer(new SaborPlayer(uuid));
        }

        plugin.getAdvancementManager().createPlayerTab(player);
    }

    @EventHandler
    public void onPlayerLogin(PlayerQuitEvent event){
        plugin.getSaborManager().closePlayer(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player sender = event.getPlayer();
        SaborPlayer saborSender = plugin.getSaborManager().getPlayer(sender.getUniqueId());

        // Filtra los destinatarios
        event.getRecipients().removeIf(receiver -> {
            if (receiver.equals(sender)) return false; // Siempre puede verse a sí mismo
            return !saborSender.getUnlockedPlayers().contains(receiver.getUniqueId());
        });
    }

    @EventHandler
    public void onPrivateMessage(PlayerCommandPreprocessEvent event) {
        String msg = event.getMessage();
        Player sender = event.getPlayer();

        // Comandos privados que querés filtrar
        String[] aliases = {"/tell", "/msg", "/w", "/whisper", "/pm"};
        for (String alias : aliases) {
            if (msg.toLowerCase().startsWith(alias + " ")) {
                String[] args = msg.split(" ");
                if (args.length < 3) return; // Comando mal formado

                Player target = Bukkit.getPlayerExact(args[1]);
                if (target == null || !target.isOnline()) return;

                SaborPlayer saborSender = plugin.getSaborManager().getPlayer(sender.getUniqueId());

                if (!saborSender.getUnlockedPlayers().contains(target.getUniqueId())) {
                    sender.sendMessage(ChatColor.RED + "No conoces a ese jugador aun");
                    event.setCancelled(true);
                }

                break;
            }
        }
    }


    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event){
        SaborPlayer saborPlayer =  plugin.getSaborManager().getPlayer(event.getPlayer().getUniqueId());
        saborPlayer.addDeath();
        plugin.getSaborPlayerStorage().save(saborPlayer);
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
