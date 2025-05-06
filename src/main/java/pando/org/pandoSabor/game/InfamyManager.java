package pando.org.pandoSabor.game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import pando.org.pandoSabor.PandoSabor;
import pando.org.pandoSabor.playerData.SaborPlayer;

import java.util.*;

public class InfamyManager {

    private final PandoSabor plugin;

    public InfamyManager(PandoSabor plugin) {
        this.plugin = plugin;
    }

    public void addInfamy(UUID playerId, int amount) {
        SaborPlayer sp = plugin.getSaborManager().getPlayer(playerId);
        if (sp == null) return;

        int oldInfamy = sp.getInfamy();
        int newInfamy = oldInfamy + amount;
        sp.setInfamy(newInfamy);

        checkMilestones(playerId, oldInfamy, newInfamy);

        plugin.getSaborPlayerStorage().save(sp);
    }

    public void reduceInfamy(UUID playerId, int amount) {
        SaborPlayer sp = plugin.getSaborManager().getPlayer(playerId);
        if (sp == null) return;

        int oldInfamy = sp.getInfamy();
        int newInfamy = Math.max(0, oldInfamy - amount);
        sp.setInfamy(newInfamy);

        plugin.getSaborPlayerStorage().save(sp);
    }

    private void checkMilestones(UUID playerId, int oldInfamy, int newInfamy) {
        Player player = Bukkit.getPlayer(playerId);
        if (player == null) return;

        List<Integer> milestones = Arrays.asList(10, 25, 50, 100);
        for (int milestone : milestones) {
            if (oldInfamy < milestone && newInfamy >= milestone) {
                player.sendMessage(ChatColor.RED + "¡Has alcanzado un nuevo nivel de infamia: " + milestone + "!");
            }
        }
    }
}
