package pando.org.pandoSabor.utils;

import kr.toxicity.model.api.BetterModel;
import kr.toxicity.model.api.BetterModelPlugin;
import kr.toxicity.model.api.animation.AnimationModifier;
import kr.toxicity.model.api.bone.RenderedBone;
import kr.toxicity.model.api.data.renderer.ModelRenderer;
import kr.toxicity.model.api.tracker.EntityTracker;
import kr.toxicity.model.api.tracker.TrackerModifier;
import kr.toxicity.model.api.util.BonePredicate;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.scheduler.BukkitRunnable;
import pando.org.pandoSabor.PandoSabor;

import java.util.Objects;
import java.util.function.Predicate;

public class Model {
    private final PandoSabor plugin;
    private final BetterModelPlugin betterModelPlugin;
    private EntityTracker tracker;
    private Entity base;
    private Chunk chunk;
    private float scale;
    private final String name;
    private final ModelRenderer modelRenderer;
    private final Location location;
    private boolean updateChunks = true;

    public Model(PandoSabor plugin, String name, Location location) {
        this.plugin = plugin;
        this.betterModelPlugin = BetterModel.inst();
        this.chunk = null;

        this.name = name;

        this.location = location;

        this.modelRenderer = betterModelPlugin.modelManager().renderer(name);

        defaults();

        startChunkUpdate();
    }


    private void defaults() {
        this.scale = 1;
        this.chunk = null;
        this.base = null;
    }

    private void startChunkUpdate(){
        new BukkitRunnable(){

            @Override
            public void run() {
                if(updateChunks){
                    saveChunk();
                }else{
                    cancel();
                }
            }

        }.runTaskTimer(plugin,0,20L);
    }

    public void setUpdateChunks(boolean updateChunks) {
        this.updateChunks = updateChunks;
    }

    public void createModel(float scale){

        this.scale = scale;

        spawn();

        plugin.getModelManager().addModel(this);
    }

    public void createModel(float scale,LivingEntity base){

        this.scale = scale;

        spawn(base);

        plugin.getModelManager().addModel(this);
    }

    public String getName() {
        return name;
    }

    private void spawnBase(Location location) {

        if(base != null && (!base.isDead() || base.isValid())){
            base.remove();
        }

        this.base = location.getWorld().spawn(location, ArmorStand.class, CreatureSpawnEvent.SpawnReason.CUSTOM);

        base.setPersistent(true);
        base.setInvulnerable(true);
        base.addScoreboardTag(name);
    }

    public ModelRenderer getModelRenderer() {
        return modelRenderer;
    }

    public void spawn() {
        if(modelRenderer != null){

            spawnBase(location);

            tracker = modelRenderer.create(base, TrackerModifier.builder().scale(() -> scale).build());
            tracker.autoSpawn(true);
            tracker.forRemoval(false);
            tracker.spawnNearby();
        }

        saveChunk();
    }

    public void spawn(LivingEntity base) {
        if(modelRenderer != null){
            this.base = base;

            if(base == null) return;

            tracker = modelRenderer.create(base, TrackerModifier.builder().scale(() -> scale).build());
            tracker.autoSpawn(true);
            tracker.forRemoval(false);
            tracker.spawnNearby();
        }
        saveChunk();
    }

    public void despawn(){
        if(modelRenderer != null){
            setUpdateChunks(false);
            tracker.despawn();
            base.remove();
        }
    }

    public void doAnimation(String name){
        for (String animation : modelRenderer.animations()) {
            if(Objects.equals(animation, name)){
                tracker.animate(name, AnimationModifier.DEFAULT);
            }
        }

    }

    public void stopAnimation(String name){
        for (String animation : modelRenderer.animations()) {
            if(Objects.equals(animation, name)){
                tracker.stopAnimation(name);
            }
        }

    }

    public void setGlowing(int rgb){
        for (RenderedBone bone : tracker.bones()) {
            Predicate<RenderedBone> renderedBonePredicate =  Predicate.isEqual(bone);
            bone.glow(BonePredicate.of(renderedBonePredicate), true, rgb);
        }
    }

    public void stopGlowing(){
        for (RenderedBone bone : tracker.bones()) {
            Predicate<RenderedBone> renderedBonePredicate =  Predicate.isEqual(bone);
            bone.glow(BonePredicate.of(renderedBonePredicate), false, 0);
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
