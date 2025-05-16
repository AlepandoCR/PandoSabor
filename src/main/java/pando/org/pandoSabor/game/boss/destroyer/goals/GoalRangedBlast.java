package pando.org.pandoSabor.game.boss.destroyer.goals;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import pando.org.pandoSabor.PandoSabor;
import pando.org.pandoSabor.game.boss.destroyer.CustomVindicator;

import java.util.EnumSet;
import java.util.List;
import java.util.Random;

public class GoalRangedBlast extends Goal {
    private final CustomVindicator boss;
    private final Random random = new Random();
    private int cooldown = 0;
    private Vec3 average = new Vec3(0,0,0);

    private final PandoSabor plugin;

    public GoalRangedBlast(CustomVindicator boss, PandoSabor plugin) {
        this.boss = boss;
        this.plugin = plugin;
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

        return !nearby.isEmpty() && random.nextDouble() < 0.20; // 20% chance
    }

    @Override
    public void start() {
        boss.setAttacking(false);
        boss.setWalking(false);
        boss.setFliping(false);
        boss.setFlying(false);

        List<LivingEntity> nearby = boss.level().getEntitiesOfClass(LivingEntity.class,
                boss.getBoundingBox().inflate(3.3),
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

        attackIfRanged();

        cooldown = 20;
    }

    private void attackIfRanged() {
        Location location = new Location(boss.level().getWorld(), boss.getX(), boss.getY(), boss.getZ());
        List<Player> targets = boss.getTopDamagers(location, 5, 30);

        if (targets.isEmpty()) return;

        Player player = targets.get(new Random().nextInt(targets.size()));
        launchTerrainSample(location, 3, player);
    }


    @Override
    public void tick() {
        if (!boss.isFlying()) return;

        Vec3 target = new Vec3(average.x, average.y + 10, average.z);
        boss.getNavigation().moveTo(target.x, target.y, target.z, 1.2);
    }

    public void launchTerrainSample(Location center, int radius, Player target) {
        World world = center.getWorld();
        if (world == null) return;

        Location playerLoc = target.getLocation().add(0, 1, 0); // donde apunta
        Vector targetVec = playerLoc.toVector();

        int y = center.getBlockY();

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                Location blockLoc = center.clone().add(x, 0, z);
                Block block = blockLoc.getBlock();
                Material mat = block.getType();

                if (mat.isAir()) continue;

                Location startLoc = blockLoc.clone().add(0.5, 1.5, 0.5);
                BlockDisplay display = (BlockDisplay) world.spawnEntity(startLoc, EntityType.BLOCK_DISPLAY);
                display.setBlock(block.getBlockData());

                Vector p0 = startLoc.toVector();
                Vector p3 = targetVec.clone().add(new Vector(0, 1.5, 0));

                Vector mid = p0.clone().midpoint(p3).add(new Vector(0, 4 + Math.random() * 2, 0));
                Vector p1 = p0.clone().midpoint(mid);
                Vector p2 = mid.clone().midpoint(p3);

                new BukkitRunnable() {
                    int tick = 0;
                    final int totalTicks = 40; // 2 segundos

                    @Override
                    public void run() {
                        if (tick >= totalTicks || !display.isValid()) {
                            this.cancel();
                            display.remove();
                            damageClose();
                            return;
                        }

                        double t = (double) tick / totalTicks;

                        // Curva de Bézier cúbica
                        Vector bezierPos = cubicBezier(t, p0, p1, p2, p3);
                        Location loc = bezierPos.toLocation(world);
                        display.teleport(loc);

                        tick++;
                    }

                    private void damageClose() {
                        for (Entity nearbyEntity : display.getNearbyEntities(1.5, 1.5, 1.5)) {
                            if(nearbyEntity instanceof org.bukkit.entity.LivingEntity livingEntity){
                                livingEntity.damage(5,boss.getBukkitEntity());
                            }
                        }
                    }
                }.runTaskTimer(boss.getPlugin(), 0L, 1L);
            }
        }
    }

    private Vector cubicBezier(double t, Vector p0, Vector p1, Vector p2, Vector p3) {
        double u = 1 - t;
        double tt = t * t;
        double uu = u * u;
        double uuu = uu * u;
        double ttt = tt * t;

        return p0.clone().multiply(uuu)
                .add(p1.clone().multiply(3 * uu * t))
                .add(p2.clone().multiply(3 * u * tt))
                .add(p3.clone().multiply(ttt));
    }



}
