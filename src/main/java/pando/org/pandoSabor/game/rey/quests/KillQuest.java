package pando.org.pandoSabor.game.rey.quests;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import pando.org.pandoSabor.PandoSabor;

import java.util.*;

public class KillQuest implements Quest, Listener {

    private final PandoSabor plugin;
    private final EntityType type;
    private final int amount;
    private int progress;
    private final Player questPlayer;

    public KillQuest(EntityType type, int amount, PandoSabor plugin, Player questPlayer) {
        this.type = type;
        this.plugin = plugin;
        this.amount = amount;
        this.questPlayer = questPlayer;
        this.progress = 0;
        registerEvent();
    }

    @Override
    public String description() {
        return ChatColor.GOLD + "[" + ChatColor.YELLOW + "Misión del Rey" + ChatColor.GOLD + "]" +
                ChatColor.GRAY + " Mata " + ChatColor.GOLD + amount + ChatColor.GRAY + " " +
                ChatColor.YELLOW + type.name().toLowerCase(Locale.ROOT).replace("_", " ");
    }

    @Override
    public boolean isCompleted(Player player) {
        return progress >= amount;
    }

    @Override
    public void registerEvent() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onEntityKill(EntityDeathEvent event) {
        if (event.getEntity().getKiller() == null) return;
        Player killer = event.getEntity().getKiller();

        if(isCompleted(questPlayer)) return;

        if(!killer.equals(questPlayer)) return;

        if (event.getEntity().getType() == type) {
            progress++;
            ProgressMessage.sendProgressMessage(questPlayer,progress,amount);
        }
    }
}
