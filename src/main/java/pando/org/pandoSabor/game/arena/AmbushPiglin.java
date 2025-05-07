package pando.org.pandoSabor.game.arena;

import org.bukkit.Bukkit;
import org.bukkit.entity.Piglin;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pando.org.pandoSabor.PandoSabor;

import java.util.List;

public class AmbushPiglin {
    private final Piglin piglin;
    private final Player target;
    private final PandoSabor plugin;

    public AmbushPiglin(Piglin piglin, Player target, PandoSabor plugin) {
        this.piglin = piglin;
        this.target = target;
        this.plugin = plugin;

        piglin.setTarget(target);
        piglin.setAware(true);
        piglin.setAI(true);
        piglin.setRemoveWhenFarAway(false);
        piglin.setImmuneToZombification(true);

        update();
    }

    public void update() {
        if (!target.isOnline() || piglin.isDead()) piglin.remove();

    }

    public void hideFromOthers(Player... players) {
        for (Player p : players) {
            if (!p.equals(target)) {
                p.hideEntity(plugin, piglin);
            }
        }
    }

    public void hideFromOthers(List<Player> players) {
        for (Player p : players) {
            if (!p.equals(target)) {
                p.hideEntity(plugin, piglin);
            }
        }
    }

    public boolean isValid() {
        return piglin.isValid() && !piglin.isDead();
    }

    public void remove() {
        piglin.remove();
    }

    public Piglin getPiglin() {
        return piglin;
    }

    @NotNull
    public Player getTarget() {
        return target;
    }
}
