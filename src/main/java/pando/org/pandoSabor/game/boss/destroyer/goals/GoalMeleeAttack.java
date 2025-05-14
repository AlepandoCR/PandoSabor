package pando.org.pandoSabor.game.boss.destroyer.goals;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.AABB;
import pando.org.pandoSabor.game.boss.destroyer.CustomVindicator;

import java.util.EnumSet;
import java.util.List;

public class GoalMeleeAttack extends Goal {

    private final CustomVindicator boss;

    private boolean isExecuting = false;

    public GoalMeleeAttack(CustomVindicator boss) {
        this.boss = boss;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return boss.getAttackCooldown() <= 0 && boss.getTarget() != null && !boss.isWalking();
    }

    @Override
    public void start() {
        isExecuting = true;
        boss.setAttacking(true);
        boss.setWalking(false);
        boss.getModel().doAnimation("swingfast");


        AABB area = boss.getBoundingBox().inflate(5.5);
        List<LivingEntity> nearby = boss.level().getEntitiesOfClass(LivingEntity.class, area,
                e -> e != boss && e.isAlive());

        for (LivingEntity entity : nearby) {
            entity.hurt(boss.damageSources().mobAttack(boss), 20.0f);
        }

        boss.setAttackCooldown(3);

        boss.setAttacking(false);
    }

    @Override
    public void tick() {

    }

    @Override
    public boolean canContinueToUse() {
        return isExecuting;
    }
}
