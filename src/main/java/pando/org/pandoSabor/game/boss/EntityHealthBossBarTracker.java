package pando.org.pandoSabor.game.boss;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.BarFlag;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import pando.org.pandoSabor.PandoSabor;

public class EntityHealthBossBarTracker {

    private final LivingEntity entity;
    private final BossBar bossBar;
    private final PandoSabor plugin;
    private final String title;

    private BukkitRunnable updater;

    public EntityHealthBossBarTracker(PandoSabor plugin, LivingEntity entity, String title, BarColor color) {
        this.plugin = plugin;
        this.entity = entity;
        this.title = title;
        this.bossBar = Bukkit.createBossBar(title, color, BarStyle.SEGMENTED_10, BarFlag.CREATE_FOG);
        this.bossBar.setVisible(true);
    }

    public void addViewer(Player player) {
        bossBar.addPlayer(player);
    }

    public void removeViewer(Player player) {
        bossBar.removePlayer(player);
    }

    public void startTracking() {
        if (updater != null) updater.cancel();

        updater = new BukkitRunnable() {
            @Override
            public void run() {
                if (entity.isDead() || !entity.isValid()) {
                    bossBar.setProgress(0);
                    bossBar.removeAll();
                    cancel();
                    return;
                }

                double health = entity.getHealth();
                double maxHealth = entity.getMaxHealth();
                double progress = Math.max(0.0, Math.min(1.0, health / maxHealth));

                bossBar.setProgress(progress);
            }
        };

        updater.runTaskTimer(plugin, 0L, 5L);
    }

    public void stopTracking() {
        if (updater != null) {
            updater.cancel();
        }
        bossBar.removeAll();
    }

    public BossBar getBossBar() {
        return bossBar;
    }
}
