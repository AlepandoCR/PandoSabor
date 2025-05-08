package pando.org.pandoSabor.utils;

import kr.toxicity.model.api.BetterModel;
import kr.toxicity.model.api.BetterModelPlugin;
import kr.toxicity.model.api.data.renderer.BlueprintRenderer;
import kr.toxicity.model.api.tracker.EntityTracker;
import kr.toxicity.model.api.tracker.TrackerModifier;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import pando.org.pandoSabor.PandoSabor;

public class Model {
    private final PandoSabor plugin;
    private final BetterModelPlugin betterModelPlugin;
    private EntityTracker tracker;
    private Entity base;
    private Chunk chunk;
    private float scale;
    private String name;
    private final BlueprintRenderer blueprintRenderer;
    private final Location location;

    public Model(PandoSabor plugin, String name, Location location) {
        this.plugin = plugin;
        this.betterModelPlugin = BetterModel.inst();
        this.chunk = null;

        this.name = name;

        this.location = location;

        this.blueprintRenderer = betterModelPlugin.modelManager().renderer(name);

        defaults();
    }

    private void defaults() {
        this.scale = 1;
        this.chunk = null;
        this.base = null;
    }

    public void createModel(float scale){

        this.scale = scale;

        spawn();

        plugin.getModelManager().addModel(this);
    }

    private void spawnBase(Location location) {

        if(base != null && (!base.isDead() || base.isValid())){
            base.remove();
        }

        this.base = location.getWorld().spawn(location, ArmorStand.class, CreatureSpawnEvent.SpawnReason.CUSTOM);

        base.setPersistent(true);
        base.setInvulnerable(true);
    }

    public BlueprintRenderer getBlueprintRenderer() {
        return blueprintRenderer;
    }

    public void spawn() {
        if(blueprintRenderer != null){

            spawnBase(location);

            tracker = blueprintRenderer.create(base, TrackerModifier.builder().scale(() -> scale).build());
            tracker.autoSpawn(true);
            tracker.forRemoval(false);
            tracker.spawnNearby();
        }

        saveChunk();
    }

    public void despawn(){
        if(blueprintRenderer != null){
            tracker.despawn();
            base.remove();
        }
    }

    private void saveChunk() {
        if(tracker != null){
            chunk = tracker.location().getChunk();
        }
    }

    public float getScale() {
        return scale;
    }

    public PandoSabor getPlugin() {
        return plugin;
    }

    public BetterModelPlugin getBetterModelPlugin() {
        return betterModelPlugin;
    }

    public Chunk getChunk() {
        return chunk;
    }

    public Entity getBase() {
        return base;
    }

    public EntityTracker getTracker() {
        return tracker;
    }
}
