package pando.org.pandoSabor.game.boss.destroyer.goals;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import pando.org.pandoSabor.game.boss.destroyer.CustomVindicator;

import java.util.EnumSet;
import java.util.List;
import java.util.Random;

public class GoalFlyToCenter extends Goal {

    private final CustomVindicator boss;
    private long flyStartTime;

    public GoalFlyToCenter(CustomVindicator boss) {
        this.boss = boss;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        List<LivingEntity> nearby = boss.level().getEntitiesOfClass(LivingEntity.class, boss.getBoundingBox().inflate(20), e -> e != boss);
        return !boss.isFlying() && !nearby.isEmpty() && new Random().nextDouble() < 0.005;
    }

    @Override
    public void start() {
        boss.setFlying(true);
        boss.setAttacking(false);
        boss.setFliping(false);
        boss.setWalking(false);

        boss.getModel().doAnimation("fly");

        List<LivingEntity> nearby = boss.level().getEntitiesOfClass(LivingEntity.class, boss.getBoundingBox().inflate(20), e -> e != boss);
        double x = 0, y = boss.getY() + 8, z = 0;
        for (LivingEntity entity : nearby) {
            x += entity.getX();
            z += entity.getZ();
        }
        x /= nearby.size();
        z /= nearby.size();

        boss.getNavigation().stop();
        boss.setDeltaMovement(0, 0, 0);
        boss.teleportTo(x, y, z);

        flyStartTime = System.currentTimeMillis();
    }

    @Override
    public void tick() {
        boss.setDeltaMovement(0, 0, 0);
    }

    @Override
    public boolean canContinueToUse() {
        return System.currentTimeMillis() - flyStartTime < 10_000;
    }

    @Override
    public void stop() {
        boss.setFlying(false);
    }
}
