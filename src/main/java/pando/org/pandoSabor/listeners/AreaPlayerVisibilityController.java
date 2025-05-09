package pando.org.pandoSabor.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;
import pando.org.pandoSabor.PandoSabor;
import pando.org.pandoSabor.utils.Area;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class AreaPlayerVisibilityController implements Listener {

    private final Area area;
    private final Set<UUID> playersInArea = new HashSet<>();
    private final PandoSabor plugin;

    public AreaPlayerVisibilityController(Area area, PandoSabor plugin) {
        this.area = area;
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        boolean wasInside = playersInArea.contains(player.getUniqueId());
        boolean isNowInside = area.contains(event.getTo());

        if (!wasInside && isNowInside) {
            playersInArea.add(player.getUniqueId());
            plugin.getInfamyDisplayManager().removeDisplay(player);
            updateAllVisibilities();
        } else if (wasInside && !isNowInside) {
            playersInArea.remove(player.getUniqueId());
            plugin.getInfamyDisplayManager().checkPlayer(player);
            updateAllVisibilities();
        }
    }

    private void updateAllVisibilities() {
        for (Player playerA : Bukkit.getOnlinePlayers()) {
            boolean isAInArea = playersInArea.contains(playerA.getUniqueId());

            for (Player playerB : Bukkit.getOnlinePlayers()) {
                if (playerA.equals(playerB)) continue;

                boolean isBInArea = playersInArea.contains(playerB.getUniqueId());

                if (isAInArea && isBInArea) {
                    // Ambos dentro: no
                    if(!playerA.isOp())  playerA.hidePlayer(plugin, playerB);

                } else if (!isAInArea && isBInArea) {
                    // A fuera, B dentro: no
                    if(!playerA.isOp()) playerA.hidePlayer(plugin, playerB);
                } else {
                    // A puede ver a B
                    playerA.showPlayer(plugin, playerB);
                }
            }
        }
    }
}
