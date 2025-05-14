package pando.org.pandoSabor.utils;

import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import pando.org.pandoSabor.PandoSabor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModelManager {
    private final PandoSabor plugin;

    private final List<Model> models = new ArrayList<>();

    public ModelManager(PandoSabor plugin) {
        this.plugin = plugin;
    }

    public PandoSabor getPlugin() {
        return plugin;
    }

    public List<Model> getModels() {
        return models;
    }

    public Model getModel(Entity base){
        Model r = null;

        for (Model model : models) {
            if (model.getBase().equals(base)) {
                r = model;
                break;
            }
        }

        return r;
    }

    public void addModel(Model... models){
        this.models.addAll(Arrays.asList(models));
    }

    public void removeModel(Model... models){
        this.models.removeAll(Arrays.asList(models));
    }

    public void unloadModelsOnChunk(Chunk chunk){
        for (Model model : models) {
            Chunk modelChunk = model.getChunk();

            if(modelChunk == null) continue;

            if(modelChunk.equals(chunk)){
                model.despawn();
            }
        }
    }

    public void loadModelsOnChunk(Chunk chunk){
        for (Model model : models) {
            Chunk modelChunk = model.getChunk();

            if(modelChunk == null) continue;

            if(modelChunk.equals(chunk)){
                model.spawn();
            }
        }
    }
}
