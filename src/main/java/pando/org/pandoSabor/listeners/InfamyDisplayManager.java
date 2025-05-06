package pando.org.pandoSabor.listeners;

import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;
import pando.org.pandoSabor.PandoSabor;

import java.util.*;

public class InfamyDisplayManager implements Listener {

    private final PandoSabor plugin;
    private final Map<UUID, DisplayPair> activeDisplays = new HashMap<>();


    public InfamyDisplayManager(PandoSabor plugin) {
        this.plugin = plugin;
        startDisplayUpdater();
    }

    public void checkPlayer(Player player) {
        var saborPlayer = plugin.getSaborManager().getPlayer(player.getUniqueId());
        int infamy = saborPlayer.getInfamy();

        if (infamy >= 10) {
            if (!activeDisplays.containsKey(player.getUniqueId())) {
                spawnDisplay(player, infamy);
            } else {
                updateDisplay(player, infamy);
            }
        } else {
            removeDisplay(player);
        }
    }

    private void spawnDisplay(Player player, int infamy) {
        Location loc = player.getLocation().add(0, 2.5, 0);
        World world = loc.getWorld();

        // Item Display
        ItemDisplay itemDisplay = (ItemDisplay) world.spawnEntity(loc, EntityType.ITEM_DISPLAY);
        itemDisplay.setItemStack(new ItemStack(Material.DIAMOND_BLOCK));
        itemDisplay.setBillboard(Display.Billboard.VERTICAL);
        itemDisplay.setInterpolationDuration(1);
        itemDisplay.setTransformation(new Transformation(
                new Vector3f(),                       // posición
                new AxisAngle4f(),                   // rotación inicial
                new Vector3f(0.3f, 0.3f, 0.3f),      // escala pequeña
                new AxisAngle4f()                    // rotación final
        ));


        // Text Display
        TextDisplay textDisplay = (TextDisplay) world.spawnEntity(loc.clone().add(0, 0.75, 0), EntityType.TEXT_DISPLAY);
        textDisplay.setBillboard(Display.Billboard.CENTER);
        textDisplay.text(Component.text("§b" + getDiamondsForInfamy(infamy)));
        textDisplay.setSeeThrough(false);
        textDisplay.setBackgroundColor(Color.fromARGB(128, 0, 0, 0));

        activeDisplays.put(player.getUniqueId(), new DisplayPair(itemDisplay, textDisplay));
    }

    private void updateDisplay(Player player, int infamy) {
        DisplayPair pair = activeDisplays.get(player.getUniqueId());
        if (pair != null && pair.textDisplay != null && pair.textDisplay.isValid()) {
            pair.textDisplay.text(Component.text("§b" + getDiamondsForInfamy(infamy)));
        }
    }

    public void removeDisplay(Player player) {
        DisplayPair pair = activeDisplays.remove(player.getUniqueId());
        if (pair != null) {
            if (pair.itemDisplay != null && pair.itemDisplay.isValid()) {
                pair.itemDisplay.remove();
            }
            if (pair.textDisplay != null && pair.textDisplay.isValid()) {
                pair.textDisplay.remove();
            }
        }
    }

    private int getDiamondsForInfamy(int infamy) {
        return (int) (1 + 0.25 * infamy);
    }

    private void startDisplayUpdater() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<UUID, DisplayPair> entry : activeDisplays.entrySet()) {
                    Player player = Bukkit.getPlayer(entry.getKey());
                    if (player == null || !player.isOnline()) {
                        plugin.getLogger().warning("Player null when moving display");
                        continue;
                    }

                    Location baseLoc = player.getLocation().clone().add(0, 2.5, 0).setRotation(0,0);
                    DisplayPair pair = entry.getValue();

                    pair.itemDisplay.teleport(baseLoc);
                    pair.textDisplay.teleport(baseLoc.clone().add(0, 0.32, 0));
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }


    private void removeDisplayByUUID(UUID uuid) {
        DisplayPair pair = activeDisplays.remove(uuid);
        if (pair != null) {
            if (pair.itemDisplay != null && pair.itemDisplay.isValid()) {
                pair.itemDisplay.remove();
            }
            if (pair.textDisplay != null && pair.textDisplay.isValid()) {
                pair.textDisplay.remove();
            }
        }
    }

    public boolean hasDisplay(Player player){
        return activeDisplays.containsKey(player.getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        removeDisplay(event.getPlayer());
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        removeDisplay(event.getPlayer());
        checkPlayer(event.getPlayer());
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        removeDisplay(event.getEntity());
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        checkPlayer(event.getPlayer());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskLater(plugin,r -> checkPlayer(event.getPlayer()),40L);
    }

    @EventHandler
    public void onUnload(PluginDisableEvent event) {
        for (UUID uuid : new HashSet<>(activeDisplays.keySet())) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                removeDisplay(player);
            }
        }
    }

    // Util
    private static class DisplayPair {
        ItemDisplay itemDisplay;
        TextDisplay textDisplay;

        public DisplayPair(ItemDisplay itemDisplay, TextDisplay textDisplay) {
            this.itemDisplay = itemDisplay;
            this.textDisplay = textDisplay;
        }
    }
}
