package pando.org.pandoSabor.game.boss;

import net.minecraft.core.BlockPos;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import pando.org.pandoSabor.PandoSabor;
import pando.org.pandoSabor.game.boss.destroyer.CustomVindicator;
import pando.org.pandoSabor.utils.Model;

public class BossSpawner {

    private final PandoSabor plugin;

    public BossSpawner(PandoSabor plugin) {
        this.plugin = plugin;
    }

    public void spawnBoss(CustomVindicator base){
        BlockPos blockPos = base.blockPosition();
        Location location = new Location(base.level().getWorld(),blockPos.getX(),blockPos.getY(),blockPos.getZ());

        Model model = new Model(plugin,"destructor",location);

        base.setModel(model);

        model.createModel(1.7f, (LivingEntity) base.getBukkitEntity());
    }

}
