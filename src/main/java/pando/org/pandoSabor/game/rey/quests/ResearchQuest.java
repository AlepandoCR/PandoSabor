package pando.org.pandoSabor.game.rey.quests;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import pando.org.pandoSabor.PandoSabor;

import java.util.UUID;

public class ResearchQuest implements Quest, Listener {

    private final PandoSabor plugin;
    private final Location objective;
    private static final double range = 10.0;
    private final Player questPlayer;

    private boolean completado = false;

    public ResearchQuest(PandoSabor plugin, Location objetivo, Player questPlayer) {
        this.plugin = plugin;
        this.objective = objetivo;
        this.questPlayer = questPlayer;
        registerEvent();
    }

    @Override
    public String description() {
        return ChatColor.GOLD + "[" + ChatColor.YELLOW + "Misión del Rey" + ChatColor.GOLD + "]" +
                ChatColor.GRAY + " Investiga una " + ChatColor.AQUA + "ubicación misteriosa" + ChatColor.GRAY +
                " cerca de otro " + ChatColor.YELLOW + "aventurero" + ChatColor.GRAY + "[" + ChatColor.DARK_GRAY + objective.getX() + ChatColor.GRAY + "," + ChatColor.DARK_GRAY + objective.getY() + ChatColor.GRAY + "," + ChatColor.DARK_GRAY + objective.getZ() + ChatColor.GRAY + "]";
    }

    @Override
    public boolean isCompleted(Player player) {
        return completado;
    }

    @Override
    public void registerEvent() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!event.getPlayer().equals(questPlayer)) return;

        if (event.getTo().distance(objective) <= range) {
            completado = true;
        }
    }
}
