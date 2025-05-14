package pando.org.pandoSabor.game.boss.destroyer;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.level.Level;
import org.bukkit.Bukkit;
import pando.org.pandoSabor.PandoSabor;
import pando.org.pandoSabor.utils.Model;

public class CustomVindicator extends Vindicator {

    private final PandoSabor plugin;

    private Model model;

    public CustomVindicator(EntityType<? extends Vindicator> entityType, Level level, PandoSabor plugin) {
        super(entityType, level);
        this.plugin = plugin;

        initModel();
    }

    public void initModel(){
        Bukkit.getScheduler().runTaskLater(plugin, r -> this.model = plugin.getModelManager().getModel(this.getBukkitEntity()),20);
    }
}
