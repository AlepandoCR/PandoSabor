package pando.org.pandoSabor.game.boss.destroyer.goals;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.phys.Vec3;
import org.bukkit.event.entity.EntityTargetEvent;
import pando.org.pandoSabor.game.boss.destroyer.CustomVindicator;

import java.util.EnumSet;
import java.util.List;

public class GoalFindTarget extends Goal {

    private final CustomVindicator boss;
    private final double radius = 30;

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
        List<? extends Player> players = boss.level().players();
        Player bestTarget = null;
        double bestScore = Double.MAX_VALUE;

        for (Player player : players) {
            if (player.isSpectator() || !player.isAlive()) continue;

            double distance = player.distanceToSqr(boss);
            if (distance > radius * radius) continue;

            if (!boss.hasLineOfSight(player)) continue;

            PathNavigation nav = boss.getNavigation();
            if (!nav.isStableDestination(BlockPos.containing(Vec3.atBottomCenterOf(player.blockPosition())))) continue;

            if (!nav.moveTo(player, 1)) continue;

            if (distance < bestScore) {
                bestTarget = player;
                bestScore = distance;
            }
        }

        if (bestTarget != null) {
            boss.setTarget(bestTarget, EntityTargetEvent.TargetReason.CLOSEST_PLAYER);
        }
    }
}
