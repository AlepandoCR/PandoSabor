package pando.org.pandoSabor.game.boss.destroyer.goals;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import pando.org.pandoSabor.game.boss.destroyer.CustomVindicator;

import java.util.EnumSet;

public class GoalChaseTarget extends Goal {

    private final CustomVindicator boss;
    private int stuckCounter = 0;

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
        if (target == null) return;

        boolean canPath = boss.getNavigation().moveTo(target, 1.0);

        if (!canPath) {
            stuckCounter++;

            if (stuckCounter > 20) {
                stuckCounter = 0;

                if (!target.isFallFlying()) return;

                Vec3 bossPos = boss.position();
                Vec3 targetPos = target.position();

                double dx = targetPos.x - bossPos.x;
                double dz = targetPos.z - bossPos.z;
                double distance = Math.sqrt(dx * dx + dz * dz);

                if (distance < 12) {
                    double midX = (bossPos.x + targetPos.x) / 2;
                    double midZ = (bossPos.z + targetPos.z) / 2;
                    double midY = Math.max(bossPos.y, targetPos.y) + 3;

                    Vec3 controlPoint = new Vec3(midX, midY, midZ);

                    Vec3 bezier = bezierCurve(bossPos, controlPoint, targetPos, 0.5);

                    boss.setDeltaMovement(bezier.subtract(bossPos).normalize().scale(0.8).add(0, 0.5, 0));
                }
            }
        } else {
            stuckCounter = 0;
        }
    }

    private Vec3 bezierCurve(Vec3 start, Vec3 control, Vec3 end, double t) {
        // Fórmula cuadrática Bézier: B(t) = (1-t)^2 * P0 + 2(1-t)t * P1 + t^2 * P2
        double u = 1 - t;
        double tt = t * t;
        double uu = u * u;

        double x = uu * start.x + 2 * u * t * control.x + tt * end.x;
        double y = uu * start.y + 2 * u * t * control.y + tt * end.y;
        double z = uu * start.z + 2 * u * t * control.z + tt * end.z;

        return new Vec3(x, y, z);
    }


    @Override
    public boolean canContinueToUse() {
        if(boss.getTarget() == null){
            return false;
        }
        return (boss.getNavigation().moveTo(boss.getTarget(), 1));
    }
}
