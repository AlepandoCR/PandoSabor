package pando.org.pandoSabor.game.time;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import pando.org.pandoSabor.PandoSabor;

public class TimeController {

    private final PandoSabor plugin;
    private final World world;
    private long currentTime = 0;

    public TimeController(PandoSabor plugin, World world) {
        this.plugin = plugin;
        this.world = world;
        this.currentTime = world.getTime();
    }

    public void startTimeCycle() {
        new BukkitRunnable() {
            @Override
            public void run() {
                boolean isDay = currentTime < 12000;

                long interval = isDay ? 20L : 10L;

                currentTime = (currentTime + 1) % 24000;
                world.setTime(currentTime);

                startNextTick(interval);
            }
        }.runTaskLater(plugin, 0L);
    }

    public void startNextTick(long interval) {
        new BukkitRunnable() {
            @Override
            public void run() {
                startTimeCycle();
            }
        }.runTaskLater(plugin, interval);
    }

    public static TimeController startup(PandoSabor plugin){
        TimeController controller = new TimeController(plugin, Bukkit.getWorld("overworld"));

        controller.startTimeCycle();

        return controller;
    }

    public long getCurrentTime() {
        return currentTime;
    }
}
