package pando.org.pandoSabor.listeners;

import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import pando.org.pandoSabor.PandoSabor;
import pando.org.pandoSabor.utils.Model;
import pando.org.pandoSabor.utils.ModelManager;

public class ModelListener implements Listener {

    private final PandoSabor plugin;

    private final ModelManager modelManager;

    public ModelListener(PandoSabor plugin) {
        this.plugin = plugin;
        this.modelManager = plugin.getModelManager();
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event){
        Chunk eventChunk = event.getChunk();

        modelManager.unloadModelsOnChunk(eventChunk);
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event){
        Chunk eventChunk = event.getChunk();

        modelManager.loadModelsOnChunk(eventChunk);
    }

    @EventHandler
    public void onDisable(PluginDisableEvent event){
        for (Model model : modelManager.getModels()) {
            model.despawn();
        }
    }

    public PandoSabor getPlugin() {
        return plugin;
    }
}
