package pando.org.pandoSabor.game.boss.destroyer;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.level.Level;
import org.bukkit.*;
import org.bukkit.entity.Evoker;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.scheduler.BukkitRunnable;
import pando.org.pandoSabor.PandoSabor;
import pando.org.pandoSabor.game.boss.destroyer.goals.*;
import pando.org.pandoSabor.utils.Model;

import java.util.*;

public class CustomVindicator extends Vindicator {

    private final PandoSabor plugin;
    private boolean isWalking;
    private boolean isAttacking;
    int time;
    private boolean isFliping;
    private boolean hasGuardians;
    private boolean isFlying;

    private final Map<Player, Double> playerDamage = new HashMap<>();

    private int attackCooldown = 0;

    private Model model;

    public CustomVindicator(EntityType<? extends Vindicator> entityType, Level level, PandoSabor plugin) {
        super(entityType, level);
        this.plugin = plugin;
        this.hasGuardians = false;
        this.time = 0;

        // Aumentar vida máxima
        AttributeInstance maxHealth = this.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth != null) {
            maxHealth.setBaseValue(1000.00);
        }

        this.setHealth(this.getMaxHealth());

        updateCooldowns();
    }

    public static CustomVindicator spawn(Level level, double x, double y, double z, PandoSabor plugin) {
        CustomVindicator vindicator = new CustomVindicator(EntityType.VINDICATOR, level, plugin);
        vindicator.teleportTo(x, y, z);
        level.addFreshEntity(vindicator, CreatureSpawnEvent.SpawnReason.CUSTOM);
        return vindicator;
    }

    public int getAttackCooldown() {
        return attackCooldown;
    }

    public void setAttackCooldown(int attackCooldown) {
        this.attackCooldown = attackCooldown;
    }

    public void updateCooldowns() {
        new BukkitRunnable() {

            @Override
            public void run() {
                if (time != 0 && time % 60 == 0 && !hasGuardians) {
                    summonEvokerGuardians();
                }else if(hasGuardians){
                    plugin.getLogger().warning("has guardians");
                }else{
                    plugin.getLogger().warning("Time: " + time);
                }

                if (getTarget() != null && getTarget().distanceTo(CustomVindicator.this) <= 4) {
                    setAttackCooldown(0);
                }
                if (attackCooldown > 0) {
                    attackCooldown--;
                }

                time++;
            }

        }.runTaskTimer(plugin, 0, 20L);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.removeAllGoals(goal -> true);

        this.goalSelector.addGoal(0, new GoalFindTarget(this));// persecución
        this.goalSelector.addGoal(1, new GoalChaseTarget(this)); // encuentra targets
        this.goalSelector.addGoal(2, new GoalFlyToCenter(this,plugin));      // animación de vuelo (solo si no hay nadie cerca, ideal)
        this.goalSelector.addGoal(3, new GoalRangedBlast(this, plugin));      // ataque a distancia
        this.goalSelector.addGoal(4, new GoalMeleeAttack(this));      // ataque cuerpo a cuerpo
    }

    public void setAttacking(boolean attacking) {
        isAttacking = attacking;
    }

    public void setFlying(boolean flying) {
        isFlying = flying;
    }

    public void setFliping(boolean jumping) {
        isFliping = jumping;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public void setWalking(boolean moving) {
        isWalking = moving;
    }

    public Model getModel() {
        return model;
    }

    public PandoSabor getPlugin() {
        return plugin;
    }

    public boolean isAttacking() {
        return isAttacking;
    }

    public boolean isFliping() {
        return isFliping;
    }

    public boolean isFlying() {
        return isFlying;
    }

    public boolean isWalking() {
        return isWalking;
    }

    public boolean canFly(){
        return !isAttacking && !isFliping;
    }

    public boolean canMove(){
        return !isAttacking && !isFliping;
    }

    public List<Player> getTopDamagers(Location center, int maxPlayers, double maxDistance) {
        World world = center.getWorld();
        if (world == null) return Collections.emptyList();

        return world.getPlayers().stream()
                .filter(p -> p.getLocation().distanceSquared(center) <= maxDistance * maxDistance)
                .sorted(Comparator.comparingDouble(p -> p.getLocation().distanceSquared(center)))
                .limit(maxPlayers)
                .toList();
    }

    public Map<Player, Double> getPlayerDamage() {
        return playerDamage;
    }


    public void summonEvokerGuardians() {
        hasGuardians = true;
        LivingEntity boss = (LivingEntity) this.getBukkitEntity();
        World world = boss.getWorld();
        Location center = boss.getLocation();
        List<LivingEntity> evokers = new ArrayList<>();

        model.setGlowing(16755200);

        boss.setInvulnerable(true);
        boss.setGlowing(true);

        int summoned = 0;
        int attempts = 0;

        while (summoned < 5 && attempts < 50) {
            attempts++;

            double minDistance = 5;
            double maxDistance = 10;

            double angle = Math.random() * 2 * Math.PI;
            double distance = minDistance + Math.random() * (maxDistance - minDistance);

            double dx = Math.cos(angle) * distance;
            double dz = Math.sin(angle) * distance;

            Location spawnLoc = center.clone().add(dx, 0, dz);
            spawnLoc.setY(world.getHighestBlockYAt(spawnLoc) + 1);

            spawnLoc.setY(world.getHighestBlockYAt(spawnLoc) + 1);

            if (!spawnLoc.getBlock().getType().isSolid()) {
                Evoker evoker = (Evoker) world.spawnEntity(spawnLoc, org.bukkit.entity.EntityType.EVOKER);
                evoker.setCustomName("Guardian Evoker");
                evoker.setCustomNameVisible(true);
                evoker.setAI(false);
                evoker.setInvulnerable(false);
                evoker.setGlowing(true);
                evoker.setMaxHealth(100.0);
                evoker.setHealth(100.0);


                evokers.add(evoker);
                summoned++;
                plugin.getLogger().warning("Invocando Evoker: " + spawnLoc);
            }
        }

       new BukkitRunnable() {
            @Override
            public void run() {
                evokers.removeIf(e -> e == null || e.isDead());

                if (evokers.isEmpty()) {
                    boss.setInvulnerable(false);
                    boss.setGlowing(false);
                    model.stopGlowing();
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0,20L);
    }


}
