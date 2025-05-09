package pando.org.pandoSabor.game.rey.quests;

import org.bukkit.entity.Player;

public interface Quest {
    String description();
    boolean isCompleted(Player player);
    void registerEvent();
}
