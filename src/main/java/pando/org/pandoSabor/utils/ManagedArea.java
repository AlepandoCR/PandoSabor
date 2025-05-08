package pando.org.pandoSabor.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import pando.org.pandoSabor.PandoSabor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ManagedArea {

    private final Area area;
    private final PandoSabor plugin;
    private final Set<UUID> trackedPlayers = new HashSet<>();
    private BukkitTask task;

    private boolean hidingPlayers = false;
    private boolean showingPlayers = false;

    public ManagedArea(Area area, PandoSabor plugin) {
        this.area = area;
        this.plugin = plugin;
        startTracking();
    }

    public void announce(Component... components){
        for (Player player : area.getPlayersInArea()) {
            for (Component component : components) {
                player.sendMessage(component);
            }
        }
    }


    private void startTracking() {
        task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            List<Player> currentPlayers = area.getPlayersInArea();

            Set<UUID> currentIds = new HashSet<>();
            for (Player player : currentPlayers) {
                currentIds.add(player.getUniqueId());
            }

            if (!currentIds.equals(trackedPlayers)) {
                trackedPlayers.clear();
                trackedPlayers.addAll(currentIds);


                if (hidingPlayers) {
                    applyHide();
                } else if (showingPlayers) {
                    applyShow();
                }
            }

        }, 0L, 20L);
    }


    public void hidePlayers() {
        hidingPlayers = true;
        showingPlayers = false;
        applyHide();
    }

    public void showPlayers() {
        showingPlayers = true;
        hidingPlayers = false;
        applyShow();
    }


    private void applyHide() {
        List<Player> players = area.getPlayersInArea();
        for (Player p1 : players) {
            for (Player p2 : players) {
                if (!p1.equals(p2)) {
                    if(!p1.isOp())   p1.hidePlayer(plugin, p2);
                }
            }
        }
    }


    private void applyShow() {
        List<Player> players = area.getPlayersInArea();
        for (Player p1 : players) {
            for (Player p2 : players) {
                if (!p1.equals(p2)) {
                    if(!p2.isOp()) p1.showPlayer(plugin, p2);
                }
            }
        }
    }

    public void stopManaging() {
        if (task != null) {
            task.cancel();
        }
        applyShow();
        trackedPlayers.clear();
    }

    public Area getArea() {
        return area;
    }
}
