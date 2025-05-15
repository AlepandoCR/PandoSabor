package pando.org.pandoSabor.listeners;

import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Piglin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import pando.org.pandoSabor.PandoSabor;
import pando.org.pandoSabor.game.arena.ArenaManager;
import pando.org.pandoSabor.game.arena.PiglinAmbushManager;
import pando.org.pandoSabor.game.boss.destroyer.CustomVindicator;

public class EntityListener implements Listener {

    private final PandoSabor plugin;
    private final ArenaManager arenaManager;
    private final PiglinAmbushManager piglinAmbushManager;
    private static final Location FIRE_PIT = new Location(Bukkit.getWorld("overworld"),-1861.5,22,548.5);

    public EntityListener(PandoSabor plugin) {
        this.plugin = plugin;
        this.arenaManager = plugin.getArenaManager();
        this.piglinAmbushManager = arenaManager.getPiglinAmbushManager();
    }

    @EventHandler
    public void onEntityTarget(EntityTargetLivingEntityEvent event){
        if(event.getEntity() instanceof LivingEntity livingEntity){
            if(livingEntity instanceof Piglin piglin){
                if(piglinAmbushManager.isAmbushPiglin(piglin)){
                    Player target = piglinAmbushManager.getAmbushPiglin(piglin).getTarget();
                    if(target.isValid()){
                        event.setTarget(target);
                    }else {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event){
        if(event.getEntity() instanceof CustomVindicator vindicator){
            if(event.getDamager() instanceof Player player){
                if(!vindicator.getPlayerDamage().containsKey(player)){
                    vindicator.getPlayerDamage().put(player,event.getDamage());
                }else{
                   double total = vindicator.getPlayerDamage().get(player) + event.getDamage();

                   vindicator.getPlayerDamage().put(player,total);
                }
            }
        }
    }

    @EventHandler
    public void onPiglinDeath(EntityDeathEvent event){
        if(event.getEntity() instanceof Piglin piglin){
            if(piglinAmbushManager.isAmbushPiglin(piglin)){
                Location init = piglinAmbushManager.getAmbushPiglin(piglin).getPiglin().getLocation();

                animatePiglinHead(init,FIRE_PIT);
            }
        }
    }

    public void animatePiglinHead(Location start, Location end) {
        World world = start.getWorld();
        if (world == null || !world.equals(end.getWorld())) return;

        // Crear item de cabeza de piglin
        ItemStack head = new ItemStack(Material.PIGLIN_HEAD);
        Item item = world.dropItem(start.clone().add(0, 0.5, 0), head);
        item.setGravity(false);
        item.setGlowing(true);
        item.setPickupDelay(Integer.MAX_VALUE);
        item.setInvulnerable(true);

        Location p0 = start.clone();
        Location p2 = end.clone();
        Location p1 = p0.clone().add(p2).multiply(0.5).add(0, 3, 0);

        int totalTicks = 40;
        new BukkitRunnable() {
            int tick = 0;

            @Override
            public void run() {
                if (!item.isValid()) {
                    cancel();
                    return;
                }

                Location nextPos = getNextPos();

                Vector velocity = nextPos.toVector().subtract(item.getLocation().toVector());
                item.setVelocity(velocity);

                world.spawnParticle(Particle.SOUL_FIRE_FLAME, item.getLocation(), 2, 0.05, 0.05, 0.05, 0);

                tick++;
                if (tick > totalTicks) {
                    // Final de la animación
                    item.remove();
                    world.spawnParticle(Particle.SOUL, end, 30, 0.3, 0.3, 0.3, 0.02);
                    world.spawnParticle(Particle.SOUL_FIRE_FLAME, end, 10, 0.2, 0.2, 0.2, 0.01);
                    checkDeath();
                    cancel();
                }
            }

            private @NotNull Location getNextPos() {
                double t = (double) tick / totalTicks;

                // Curva cuadrática Bézier
                double invT = 1 - t;
                double x = invT * invT * p0.getX() + 2 * invT * t * p1.getX() + t * t * p2.getX();
                double y = invT * invT * p0.getY() + 2 * invT * t * p1.getY() + t * t * p2.getY();
                double z = invT * invT * p0.getZ() + 2 * invT * t * p1.getZ() + t * t * p2.getZ();

                Location nextPos = new Location(world, x, y, z);
                return nextPos;
            }
        }.runTaskTimer(plugin, 0L, 1L); // cada tick
    }

    private void checkDeath() {
        arenaManager.addDeathPiglin();
        if(arenaManager.getDeathPiglins() % 40 == 0){
            launchDiamondBurst(FIRE_PIT);
        }
    }


    public void launchDiamondBurst(Location origin) {
        World world = origin.getWorld();
        if (world == null) return;

        int totalDiamonds = 10;
        double radius = 0.5;
        double upwardVelocity = 0.3;
        double horizontalSpeed = 0.4;

        for (int i = 0; i < totalDiamonds; i++) {
            // Ángulo en sentido horario
            double angle = 2 * Math.PI * i / totalDiamonds;
            double x = Math.cos(angle) * radius;
            double z = Math.sin(angle) * radius;

            // Crear un diamante individual (no stackeable en el piso)
            ItemStack diamond = new ItemStack(Material.DIAMOND, 1);
            Item item = world.dropItem(origin.clone().add(0, 1, 0), diamond);
            item.setPickupDelay(10); // Delay de recogida
            item.setVelocity(new Vector(x, upwardVelocity, z).normalize().multiply(horizontalSpeed));
        }
    }


    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public PandoSabor getPlugin() {
        return plugin;
    }
}
