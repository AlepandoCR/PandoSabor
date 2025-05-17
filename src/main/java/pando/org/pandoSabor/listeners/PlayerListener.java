package pando.org.pandoSabor.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Attr;
import pando.org.pandoSabor.PandoSabor;
import pando.org.pandoSabor.game.TablistDisplayAdapter;
import pando.org.pandoSabor.playerData.SaborPlayer;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Supplier;

public class PlayerListener implements Listener {

    private final PandoSabor plugin;
    private final List<Location> spawnLocations;

    public PlayerListener(PandoSabor plugin) {
        this.plugin = plugin;

        World targetWorld = Bukkit.getWorld("overworld");

        this.spawnLocations = startLocations(targetWorld);
    }

    @NotNull
    private static List<Location> startLocations(World targetWorld) {
        return Arrays.asList(
                new Location(targetWorld, -2017.5, 109.5, -319.5),
                new Location(targetWorld, -2024.5, 101.5, -320.5),
                new Location(targetWorld, -2023.5, 101.5, -331.5),
                new Location(targetWorld, -2024.5, 102.5, -325.5)
        );
    }

    @EventHandler
    public void onPlayerLogin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        plugin.getAdvancementManager().createPlayerTab(player);

        plugin.getSaborManager().startPlayer(uuid);

        startTab(player);

        formatJoinMsg(event, player);

        tavernTp(player);

        setRandomScale(player);
    }

    private void setRandomScale(Player player) {

        if(player.hasPlayedBefore()) return;

        AttributeInstance attribute = player.getAttribute(Attribute.SCALE);

        if (attribute == null) {
            player.registerAttribute(Attribute.SCALE);
            attribute = player.getAttribute(Attribute.SCALE);
        }

        if (attribute != null) {
            double amount = 0.8 + (Math.random() * 0.4);
            attribute.setBaseValue(amount);
        }
    }

    private static void formatJoinMsg(PlayerJoinEvent event, Player player) {
        if (!player.hasPlayedBefore()) {
            event.joinMessage(
                    Component.text("» ")
                            .color(NamedTextColor.LIGHT_PURPLE)
                            .append(Component.text(player.getName())
                                    .color(NamedTextColor.GOLD)
                                    .decorate(TextDecoration.BOLD))
                            .append(Component.text(" ha despertado en la taberna por primera vez."))
                            .color(NamedTextColor.GRAY)
            );
        } else {
            event.joinMessage(
                    Component.text("» ")
                            .color(NamedTextColor.YELLOW)
                            .append(Component.text(player.getName())
                                    .color(NamedTextColor.GREEN))
                            .append(Component.text(" ha regresado al reino."))
                            .color(NamedTextColor.GRAY)
            );
        }

        if (player.isOp()) {
            event.joinMessage(null);
        }
    }

    public void tavernTp(Player player){
        Random random = new Random();
        if (!player.hasPlayedBefore()) {
            Location chosenLocation = spawnLocations.get(random.nextInt(spawnLocations.size()));

            chosenLocation.getChunk().load();

            Bukkit.getScheduler().runTaskLater(
                   plugin,
                    () -> player.teleport(chosenLocation),
                    1L
            );
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        event.getTo();
        if (isEnd(event.getTo().getWorld())) {
            Player player = event.getPlayer();
            player.sendMessage("§c¡El acceso al End está bloqueado!");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPortal(PlayerPortalEvent event) {
        event.getTo();
        if (isEnd(event.getTo().getWorld())) {
            Player player = event.getPlayer();
            player.sendMessage("§cNo puedes usar portales al End.");
            event.setCancelled(true);
        }
    }

    private boolean isEnd(World world) {
        return world != null && world.getEnvironment() == World.Environment.THE_END;
    }

    private void startTab(@NotNull Player player) {
        Bukkit.getScheduler().runTaskLater(plugin,r -> {

            SaborPlayer revision = plugin.getSaborManager().getPlayer(player.getUniqueId());
            Supplier<SaborPlayer> saborPlayerSupplier = () -> revision;

            if(saborPlayerSupplier.get() == null){
                saborPlayerSupplier = () -> plugin.getSaborPlayerStorage().load(player.getUniqueId());
            }

            TablistDisplayAdapter.startLiveTablist(player,saborPlayerSupplier,plugin);
        },60L);
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
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        SaborPlayer saborPlayer = plugin.getSaborManager().getPlayer(player.getUniqueId());

        // Registrar la muerte
        saborPlayer.addDeath();
        plugin.getSaborPlayerStorage().save(saborPlayer);

        // Eliminar infamia al morir
        plugin.getInfamyManager().reduceInfamy(saborPlayer.getUuid(), saborPlayer.getInfamy());

        // Chequear si llegó a 3 muertes
        if (saborPlayer.getDeaths() >= 3) {

            if(player.isOp()) return;

            banAndMsg(player);
        }
    }

    private static void banAndMsg(Player player) {
        String reason = ChatColor.RED + "👑 El Rey Saborgón habla:\n" +
                ChatColor.GOLD + "\"¡Tres veces has fallado en mantener tu pellejo con sabor! " +
                "Y ahora, como dicta mi ley glotona... ¡serás exiliado del Reino del Sabor!\"\n\n" +
                ChatColor.DARK_RED + "Has sido baneado por perder tus 3 vidas.";


        player.ban(reason, Duration.ofDays(100),"Sistema del Rey");

        String broadcast = ChatColor.DARK_GRAY + "[" + ChatColor.RED + "⚠" + ChatColor.DARK_GRAY + "] " +
                ChatColor.GRAY + "El Rey Saborgón ha declarado la " + ChatColor.RED + "expulsión definitiva "
                + ChatColor.GRAY + "de " + ChatColor.YELLOW + player.getName() + ChatColor.GRAY +
                " por perder sus " + ChatColor.GOLD + "3 vidas" + ChatColor.GRAY + ". ¡Una " +
                ChatColor.DARK_RED + "muerte con sabor" + ChatColor.GRAY + "!";

        Bukkit.broadcastMessage(broadcast);
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
