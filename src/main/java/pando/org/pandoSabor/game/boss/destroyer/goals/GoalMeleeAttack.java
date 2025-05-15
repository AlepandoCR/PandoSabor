package pando.org.pandoSabor.game.boss.destroyer.goals;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.util.Vector;
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
        attack();
    }

    private void attack() {
        isExecuting = true;
        boss.setAttacking(true);
        boss.setWalking(false);
        boss.getModel().doAnimation("swingfast");

        AABB area = boss.getBoundingBox().inflate(5.5);
        List<Player> nearby = boss.level().getEntitiesOfClass(Player.class, area, LivingEntity::isAlive);

        for (Player player : nearby) {
            player.hurt(boss.damageSources().mobAttack(boss), 20.0f);

            Vector push = player.getBukkitEntity().getLocation().toVector().subtract(boss.getBukkitEntity().getLocation().toVector());
            if (push.lengthSquared() == 0) {
                push = new Vector(Math.random() - 0.5, 0.1, Math.random() - 0.5);
            } else {
                push = push.normalize().multiply(0.6);
            }

            player.getBukkitEntity().setVelocity(push);
        }

        // Efecto de partículas
        Location center = boss.getBukkitEntity().getLocation();
        World world = center.getWorld();
        if (world != null) {
            int points = 20;
            double radius = 5.5;
            for (int i = 0; i < points; i++) {
                double angle = 2 * Math.PI * i / points;
                double x = center.getX() + radius * Math.cos(angle);
                double z = center.getZ() + radius * Math.sin(angle);
                double y = center.getY() + 1;

                world.spawnParticle(Particle.SWEEP_ATTACK, new Location(world, x, y, z), 1, 0, 0, 0, 0);
            }
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
