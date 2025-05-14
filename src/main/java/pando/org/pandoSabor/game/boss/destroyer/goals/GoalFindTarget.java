package pando.org.pandoSabor.game.boss.destroyer.goals;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import org.bukkit.event.entity.EntityTargetEvent;
import pando.org.pandoSabor.game.boss.destroyer.CustomVindicator;

import java.util.EnumSet;

public class GoalFindTarget extends Goal {

    private final CustomVindicator boss;

    public GoalFindTarget(CustomVindicator boss) {
        this.boss = boss;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        return true;
    }

    @Override
    public void tick() {
        Player closest = boss.level().getNearestPlayer(boss, 30);
        if (closest != null) {
            boss.setTarget(closest, EntityTargetEvent.TargetReason.CUSTOM);
        }
    }
}
