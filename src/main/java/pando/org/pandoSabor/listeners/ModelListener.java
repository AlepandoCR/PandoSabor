package pando.org.pandoSabor.listeners;

import kr.toxicity.model.api.event.ModelDamagedEvent;
import kr.toxicity.model.api.event.ModelInteractAtEvent;
import kr.toxicity.model.api.event.ModelInteractEvent;
import org.bukkit.*;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import pando.org.pandoSabor.PandoSabor;
import pando.org.pandoSabor.game.rey.quests.Quest;
import pando.org.pandoSabor.utils.Model;
import pando.org.pandoSabor.utils.ModelManager;

import java.util.Optional;

public class ModelListener implements Listener {

    private final Location kingTrigger = new Location(
            Bukkit.getWorld("overworld"), -342.59, 92.00, 1648.98
    );

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

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction().isLeftClick() || event.getAction().isRightClick()) {

            Player player = event.getPlayer();
            World world = player.getWorld();

            if (!world.equals(kingTrigger.getWorld())) return;

            RayTraceResult result = world.rayTraceBlocks(
                    player.getEyeLocation(),
                    player.getLocation().getDirection(),
                    5.0,
                    FluidCollisionMode.NEVER,
                    true
            );

            Vector destino;

            if (result != null) {
                destino = result.getHitPosition().toLocation(world).toVector();
            } else {
                destino = player.getEyeLocation().toVector()
                        .add(player.getLocation().getDirection().normalize().multiply(5));
            }

            if (destino.distance(kingTrigger.toVector()) <= 3) {
                checkForQuest(player);
            }
        }
    }


    private void checkForQuest(Player player) {
        if(plugin.getQuestManager().hasQuest(player)){
            Optional<Quest> q = plugin.getQuestManager().getQuest(player);
            q.ifPresent(quest -> player.sendMessage(quest.description()));
        }else{
            plugin.getKing().createQuest(player);
        }

    }

    public PandoSabor getPlugin() {
        return plugin;
    }
}
