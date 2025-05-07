package pando.org.pandoSabor.utils;

import kr.toxicity.model.api.BetterModel;
import kr.toxicity.model.api.BetterModelPlugin;
import kr.toxicity.model.api.data.blueprint.ModelBlueprint;
import kr.toxicity.model.api.data.raw.ModelData;
import kr.toxicity.model.api.data.renderer.BlueprintRenderer;
import kr.toxicity.model.api.tracker.EntityTracker;
import kr.toxicity.model.api.tracker.TrackerModifier;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import pando.org.pandoSabor.PandoSabor;

public class Model {
    private final PandoSabor plugin;
    private final BetterModelPlugin betterModelPlugin;

    public Model(PandoSabor plugin) {
        this.plugin = plugin;
        this.betterModelPlugin = BetterModel.inst();
    }

    public void createModel(String name, Entity base, float scale){
        BlueprintRenderer renderer = betterModelPlugin.modelManager().renderer(name);

        if(renderer != null){
            EntityTracker entityTracker = renderer.create(base, TrackerModifier.builder().scale(() -> scale).build());
            entityTracker.spawnNearby();
        }
    }

    public PandoSabor getPlugin() {
        return plugin;
    }

    public BetterModelPlugin getBetterModelPlugin() {
        return betterModelPlugin;
    }
}
