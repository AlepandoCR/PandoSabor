package pando.org.pandoSabor.game.rey.quests;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import pando.org.pandoSabor.PandoSabor;

import java.util.*;

public class BreakQuest implements Quest, Listener {

    private final PandoSabor plugin;
    private final Material type;
    private final int amount;
    private int progress;
    private final Player questPlayer;

    public BreakQuest(PandoSabor plugin, Material tipo, int cantidad, Player questPlayer) {
        this.plugin = plugin;
        this.type = tipo;
        this.amount = cantidad;
        this.questPlayer = questPlayer;
        this.progress = 0;
        registerEvent();
    }

    @Override
    public String description() {
        return ChatColor.GOLD + "[" + ChatColor.YELLOW + "Misión del Rey" + ChatColor.GOLD + "]" +
                ChatColor.GRAY + " Rompe " + ChatColor.GOLD + amount + ChatColor.GRAY + " bloques de " +
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
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if(!player.equals(questPlayer)) return;

        Block block = event.getBlock();
        if (block.getType() == type) {
            progress++;
            ProgressMessage.sendProgressMessage(questPlayer,progress,amount);
        }
    }
}
