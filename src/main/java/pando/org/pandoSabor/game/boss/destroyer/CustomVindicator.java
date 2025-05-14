package pando.org.pandoSabor.game.boss.destroyer;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.level.Level;
import org.bukkit.Bukkit;
import org.bukkit.event.entity.CreatureSpawnEvent;
import pando.org.pandoSabor.PandoSabor;
import pando.org.pandoSabor.game.boss.destroyer.goals.*;
import pando.org.pandoSabor.utils.Model;

public class CustomVindicator extends Vindicator {

    private final PandoSabor plugin;
    private boolean isWalking;
    private boolean isAttacking;
    private boolean isFliping;
    private boolean isFlying;

    private int attackCooldown = 0;

    private Model model;

    public CustomVindicator(EntityType<? extends Vindicator> entityType, Level level, PandoSabor plugin) {
        super(entityType, level);
        this.plugin = plugin;


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

    public void updateCooldowns(){
        Bukkit.getScheduler().runTaskTimer(plugin,r -> {
            if(getTarget() != null && getTarget().distanceTo(this) <= 4 ){
                setAttackCooldown(0);
            }
            if(attackCooldown > 0){
                attackCooldown--;
            }
        },0,20L);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.removeAllGoals(goal -> true);

        this.goalSelector.addGoal(0, new GoalFindTarget(this));// persecución
        this.goalSelector.addGoal(1, new GoalChaseTarget(this)); // encuentra targets
        this.goalSelector.addGoal(2, new GoalFlyToCenter(this));      // animación de vuelo (solo si no hay nadie cerca, ideal)
        this.goalSelector.addGoal(3, new GoalRangedBlast(this));      // ataque a distancia
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
}
