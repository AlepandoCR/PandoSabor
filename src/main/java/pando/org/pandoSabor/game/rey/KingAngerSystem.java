package pando.org.pandoSabor.game.rey;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import pando.org.pandoSabor.PandoSabor;

import java.util.Random;

public class KingAngerSystem {

    private final PandoSabor plugin;
    private final King king;
    private final Random random = new Random();

    public KingAngerSystem(PandoSabor plugin) {
        this.plugin = plugin;
        this.king = plugin.getKing();
    }

    public void start() {
        new BukkitRunnable() {
            @Override
            public void run() {
                int roll = random.nextInt(100);

                if (roll < 60) {
                    int amount = 1 + random.nextInt(3);
                    king.addIra(amount);
                    plugin.getLogger().info("⚠️ Ira del Rey aumentada en " + amount + ". Nueva ira: " + king.getIraLevel());
                } else if (roll < 90) {
                    int amount = 1 + random.nextInt(2);
                    king.reduceIra(amount);
                    plugin.getLogger().info("✅ Ira del Rey reducida en " + amount + ". Nueva ira: " + king.getIraLevel());
                } else {
                    // No cambia
                    plugin.getLogger().info("ℹ️ Ira del Rey se mantiene en " + king.getIraLevel());
                }
            }
        }.runTaskTimer(plugin, 20L * 60, 20L * 60);
    }


    public void addExternally(int cantidad) {
        king.addIra(cantidad);
    }

    public void reduceExternally(int cantidad) {
        king.reduceIra(cantidad);
    }
}
