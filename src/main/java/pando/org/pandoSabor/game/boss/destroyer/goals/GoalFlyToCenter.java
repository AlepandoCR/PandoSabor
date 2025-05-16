package pando.org.pandoSabor.game.boss.destroyer.goals;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import pando.org.pandoSabor.PandoSabor;
import pando.org.pandoSabor.game.boss.destroyer.CustomVindicator;

import java.util.EnumSet;
import java.util.List;
import java.util.Random;

public class GoalFlyToCenter extends Goal {

    private final CustomVindicator boss;
    private final PandoSabor plugin;
    private long flyStartTime;

    public GoalFlyToCenter(CustomVindicator boss, PandoSabor plugin) {
        this.boss = boss;
        this.plugin = plugin;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        List<LivingEntity> nearby = boss.level().getEntitiesOfClass(LivingEntity.class, boss.getBoundingBox().inflate(20), e -> e != boss);
        return !boss.isFlying() && !nearby.isEmpty() && new Random().nextDouble() < 0.0015;
    }

    @Override
    public void start() {
        boss.setFlying(true);
        boss.setAttacking(false);
        boss.setFliping(false);
        boss.setWalking(false);

        boss.getModel().doAnimation("fly");

        World world = boss.level().getWorld();

        Location center = new Location(world,boss.getX(),boss.getY(),boss.getZ());

        startStormEvent(boss,boss.getTopDamagers(center,5,30));

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

    private void startStormEvent(CustomVindicator center, List<Player> targets) {

        World world = center.level().getWorld();
        Location centerLoc = new Location(world,center.getX(),center.getY(),center.getZ()) ;
        int radius = 40;

        world.setStorm(true);
        int durationTicks = 15 * 20;
        int lightningRounds = 3;
        int lightningInterval = durationTicks / lightningRounds;

        playWindParticles(centerLoc);

        // Viento circular
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= durationTicks) {
                    world.setStorm(false);
                    this.cancel();
                    return;
                }

                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getWorld().equals(world) && player.getLocation().distance(centerLoc) <= radius) {
                        Location loc = player.getLocation();
                        Vector toCenter = centerLoc.toVector().subtract(loc.toVector());

                        if (toCenter.lengthSquared() < 0.0001) continue;

                        Vector perpendicular = new Vector(-toCenter.getZ(), 0, toCenter.getX());
                        if (perpendicular.lengthSquared() < 0.0001) continue;


                        double distance = loc.distance(centerLoc);
                        double force = 0.02 + (radius - distance) * 0.005;
                        Vector windForce = perpendicular.normalize().multiply(force);
                        player.setVelocity(player.getVelocity().add(windForce));


                    }
                }


                // Rayos
                if (ticks % lightningInterval == 0 && ticks <= durationTicks - 20) {
                    for (Player target : targets) {
                        if (target.getWorld().equals(world) && target.getLocation().distance(centerLoc) <= radius) {
                            Location strikeLoc = target.getLocation();

                            target.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 40, 1, false, false, false));
                            world.spawnParticle(Particle.CLOUD, strikeLoc.clone().add(0, 2, 0), 30, 0.5, 0.5, 0.5, 0.01);
                            world.playSound(strikeLoc, Sound.ENTITY_ILLUSIONER_CAST_SPELL, 2f, 0.6f);

                            new BukkitRunnable() {
                                @Override
                                public void run() {

                                    Location eye = target.getEyeLocation();
                                    if (eye.getBlock().getLightFromSky() == 15) {
                                        world.strikeLightningEffect(strikeLoc);
                                        target.damage(3.0); // daño del rayo
                                    } else {
                                        target.sendMessage(ChatColor.AQUA + "¡Te has salvado del rayo por estar bajo techo!");
                                    }
                                }
                            }.runTaskLater(boss.getPlugin(), 60L);
                        }
                    }
                }

                ticks++;
            }
        }.runTaskTimer(boss.getPlugin(), 0L, 1L);
    }

    private void playWindParticles(Location center) {
        World world = center.getWorld();
        int durationTicks = 15 * 20;
        int radius = 40;

        new BukkitRunnable() {
            int ticks = 0;
            final Random random = new Random();

            @Override
            public void run() {
                if (ticks >= durationTicks) {
                    this.cancel();
                    return;
                }

                for (int i = 0; i < 25; i++) {
                    double angle = random.nextDouble() * 2 * Math.PI;
                    double distance = random.nextDouble() * radius;
                    double x = center.getX() + Math.cos(angle) * distance;
                    double z = center.getZ() + Math.sin(angle) * distance;
                    double y = center.getY() + random.nextDouble() * 5;

                    Location particleLoc = new Location(world, x, y, z);

                    Vector windVector = new Vector(
                            -Math.sin(angle) * 0.2 + (random.nextDouble() - 0.5) * 0.1,
                            (random.nextDouble() - 0.5) * 0.1,
                            Math.cos(angle) * 0.2 + (random.nextDouble() - 0.5) * 0.1
                    );

                    Particle particle = switch (random.nextInt(3)) {
                        case 0 -> Particle.SWEEP_ATTACK;
                        case 1 -> Particle.GUST;
                        case 2 -> Particle.FALLING_SPORE_BLOSSOM;
                        default -> throw new IllegalStateException("Unexpected value");
                    };

                    world.spawnParticle(particle, particleLoc, 0, windVector.getX(), windVector.getY(), windVector.getZ(), 0.1);
                }

                ticks++;
            }
        }.runTaskTimer(boss.getPlugin(), 0L, 1L);
    }


}
