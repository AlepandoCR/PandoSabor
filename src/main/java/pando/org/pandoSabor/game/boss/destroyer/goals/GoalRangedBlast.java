package pando.org.pandoSabor.game.boss.destroyer.goals;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import pando.org.pandoSabor.game.boss.destroyer.CustomVindicator;

import java.util.EnumSet;
import java.util.List;
import java.util.Random;

public class GoalRangedBlast extends Goal {
    private final CustomVindicator boss;
    private final Random random = new Random();
    private int cooldown = 0;
    private Vec3 average = new Vec3(0,0,0);

    public GoalRangedBlast(CustomVindicator boss) {
        this.boss = boss;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (cooldown > 0) {
            cooldown--;
            return false;
        }

        List<LivingEntity> nearby = boss.level().getEntitiesOfClass(LivingEntity.class,
                boss.getBoundingBox().inflate(12),
                e -> e != boss && e.isAlive());

        return !nearby.isEmpty() && random.nextDouble() < 0.02; // 2% chance
    }

    @Override
    public void start() {
        boss.setAttacking(false);
        boss.setWalking(false);
        boss.setFliping(false);
        boss.setFlying(false);

        List<LivingEntity> nearby = boss.level().getEntitiesOfClass(LivingEntity.class,
                boss.getBoundingBox().inflate(10),
                e -> e != boss && e.isAlive());

        if (!nearby.isEmpty()) {
             average = nearby.stream()
                    .map(LivingEntity::position)
                    .reduce(Vec3::add)
                    .map(pos -> pos.scale(1.0 / nearby.size()))
                    .orElse(boss.position());

            boss.lookAt(EntityAnchorArgument.Anchor.EYES,average);
        }

        for (LivingEntity entity : nearby) {
            entity.hurt(boss.damageSources().mobAttack(boss), 10.0f);
        }

        boss.getModel().doAnimation("jump");

        // Efecto

        cooldown = 40;
    }

    @Override
    public void tick() {
        if (!boss.isFlying()) return;

        Vec3 target = new Vec3(average.x, average.y + 10, average.z);
        boss.getNavigation().moveTo(target.x, target.y, target.z, 1.2);
    }

}
