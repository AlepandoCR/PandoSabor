package pando.org.pandoSabor.game.boss.destroyer.goals;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import pando.org.pandoSabor.game.boss.destroyer.CustomVindicator;

import java.util.EnumSet;

public class GoalChaseTarget extends Goal {

    private final CustomVindicator boss;

    public GoalChaseTarget(CustomVindicator boss) {
        this.boss = boss;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = boss.getTarget();
        return target != null && !boss.isAttacking() && !boss.isFlying() && target.distanceTo(boss) > 3.5;
    }

    @Override
    public void tick() {
        LivingEntity target = boss.getTarget();
        if (target != null) {
            boss.getNavigation().moveTo(target, 1.0);
            boss.setWalking(true);
        }
    }

    @Override
    public void stop() {
        boss.setWalking(false);
        boss.getNavigation().stop();
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = boss.getTarget();

        if(target == null) return false;

        if(target.distanceTo(boss) > 3.5){
            boss.setAttackCooldown(0);
        }
        return target.isAlive() && !boss.isFlying() && !boss.isAttacking() && target.distanceTo(boss) > 3.5;
    }
}
