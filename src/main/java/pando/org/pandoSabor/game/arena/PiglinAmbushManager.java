package pando.org.pandoSabor.game.arena;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Piglin;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import pando.org.pandoSabor.PandoSabor;
import pando.org.pandoSabor.utils.Area;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PiglinAmbushManager {

    private final PandoSabor plugin;
    private final List<AmbushPiglin> activeAmbushPiglins = new ArrayList<>();
    private final Area area;

    public PiglinAmbushManager(PandoSabor plugin, Area area) {
        this.plugin = plugin;
        this.area = area;
        startUpdater();
    }

    public void spawnAmbush(Player target, int nivel, Location location) {
        int pigAmount = Math.max(3, nivel/2);
        for (int i = 0; i < pigAmount; i++) {
            Piglin piglin = (Piglin) location.getWorld().spawnEntity(location, EntityType.PIGLIN);
            AmbushPiglin ambush = new AmbushPiglin(piglin, target, plugin);
            ambush.hideFromOthers(area.getPlayersInArea());
            activeAmbushPiglins.add(ambush);
        }
    }

    private void startUpdater() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Iterator<AmbushPiglin> iterator = activeAmbushPiglins.iterator();
                while (iterator.hasNext()) {
                    AmbushPiglin ambush = iterator.next();
                    if (!ambush.isValid()) {
                        iterator.remove();
                        continue;
                    }
                    ambush.update();
                    ambush.hideFromOthers(area.getPlayersInArea());
                }
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }

    public List<AmbushPiglin> getActiveAmbushPiglins() {
        return activeAmbushPiglins;
    }

    public List<AmbushPiglin> getPiglins(Player player){
        List<AmbushPiglin> r = new ArrayList<>();
        for (AmbushPiglin activeAmbushPiglin : activeAmbushPiglins) {
            if(activeAmbushPiglin.getTarget().equals(player)){
                r.add(activeAmbushPiglin);
            }
        }
        return r;
    }

    public boolean isAmbushPiglin(Piglin piglin){
        for (AmbushPiglin activeAmbushPiglin : activeAmbushPiglins) {
            if(activeAmbushPiglin.getPiglin().equals(piglin)){
                return true;
            }
        }
        return false;
    }

    public AmbushPiglin getAmbushPiglin(Piglin piglin){
        AmbushPiglin r = null;
        for (AmbushPiglin activeAmbushPiglin : activeAmbushPiglins) {
            if(activeAmbushPiglin.getPiglin().equals(piglin)){
                r = activeAmbushPiglin;
            }
        }
        return r;
    }
}
