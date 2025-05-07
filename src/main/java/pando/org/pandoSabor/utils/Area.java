package pando.org.pandoSabor.utils;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import pando.org.pandoSabor.PandoSabor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class Area {

    private static final Map<UUID, BukkitTask> activeDisplays = new HashMap<>();
    private Location corner1;
    private Location corner2;
    private final PandoSabor plugin;

    public Area(Location loc1, Location loc2, PandoSabor plugin) {
        this.plugin = plugin;
        if (!loc1.getWorld().equals(loc2.getWorld())) {
            throw new IllegalArgumentException("Las dos ubicaciones deben estar en el mismo mundo.");
        }

        this.corner1 = getMinimumCorner(loc1, loc2);
        this.corner2 = getMaximumCorner(loc1, loc2);
    }

    public Area(Location center, int radius, PandoSabor plugin) {
        this.plugin = plugin;
        World world = center.getWorld();

        Location loc1 = new Location(world,
                center.getX() - radius,
                center.getY() - radius,
                center.getZ() - radius);

        Location loc2 = new Location(world,
                center.getX() + radius,
                center.getY() + radius,
                center.getZ() + radius);

        this.corner1 = getMinimumCorner(loc1, loc2);
        this.corner2 = getMaximumCorner(loc1, loc2);
    }

    private Location getMinimumCorner(Location l1, Location l2) {
        return new Location(l1.getWorld(),
                Math.min(l1.getX(), l2.getX()),
                Math.min(l1.getY(), l2.getY()),
                Math.min(l1.getZ(), l2.getZ()));
    }

    private Location getMaximumCorner(Location l1, Location l2) {
        return new Location(l1.getWorld(),
                Math.max(l1.getX(), l2.getX()),
                Math.max(l1.getY(), l2.getY()),
                Math.max(l1.getZ(), l2.getZ()));
    }

    public boolean contains(Location loc) {
        if (!loc.getWorld().equals(getWorld())) return false;

        return loc.getX() >= corner1.getX() && loc.getX() <= corner2.getX() &&
                loc.getY() >= corner1.getY() && loc.getY() <= corner2.getY() &&
                loc.getZ() >= corner1.getZ() && loc.getZ() <= corner2.getZ();
    }

    public boolean contains(Entity entity) {
        return contains(entity.getLocation());
    }

    public World getWorld() {
        return corner1.getWorld();
    }

    public Location getCorner1() {
        return corner1.clone();
    }

    public Location getCorner2() {
        return corner2.clone();
    }

    public Location getCenter() {
        return new Location(
                getWorld(),
                (corner1.getX() + corner2.getX()) / 2,
                (corner1.getY() + corner2.getY()) / 2,
                (corner1.getZ() + corner2.getZ()) / 2
        );
    }

    public double getVolume() {
        return (corner2.getX() - corner1.getX()) *
                (corner2.getY() - corner1.getY()) *
                (corner2.getZ() - corner1.getZ());
    }

    public List<LivingEntity> getLiveEntitiesInArea() {
        World world = corner1.getWorld();

        return world.getLivingEntities().stream()
                .filter(entity -> isInArea(entity.getLocation()))
                .collect(Collectors.toList());
    }

    public List<Player> getPlayersInArea() {
        World world = corner1.getWorld();

        return world.getPlayers().stream()
                .filter(player -> isInArea(player.getLocation()))
                .collect(Collectors.toList());
    }

    public boolean overlaps(Area other) {
        if (!other.getWorld().equals(this.getWorld())) return false;

        return this.corner1.getX() <= other.corner2.getX() && this.corner2.getX() >= other.corner1.getX() &&
                this.corner1.getY() <= other.corner2.getY() && this.corner2.getY() >= other.corner1.getY() &&
                this.corner1.getZ() <= other.corner2.getZ() && this.corner2.getZ() >= other.corner1.getZ();
    }

    private boolean isInArea(Location loc) {
        if (!loc.getWorld().equals(corner1.getWorld())) return false;

        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();

        return x >= corner1.getX() && x <= corner2.getX()
                && y >= corner1.getY() && y <= corner2.getY()
                && z >= corner1.getZ() && z <= corner2.getZ();
    }

    public void expand(int amount) {
        World world = corner1.getWorld();

        // Nueva corner1 reducida en todas las direcciones
        this.corner1 = new Location(world,
                corner1.getX() - amount,
                corner1.getY() - amount,
                corner1.getZ() - amount);

        // Nueva corner2 aumentada en todas las direcciones
        this.corner2 = new Location(world,
                corner2.getX() + amount,
                corner2.getY() + amount,
                corner2.getZ() + amount);
    }
    private void showArea(Player player) {
        World world = corner1.getWorld();
        int minX = corner1.getBlockX();
        int minZ = corner1.getBlockZ();
        int maxX = corner2.getBlockX();
        int maxZ = corner2.getBlockZ();

        // Configuración de la partícula
        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(164,136,255), 2.5F);

        // Línea superior e inferior del rectángulo
        for (int x = minX; x <= maxX; x++) {
            showParticleAtTopBlock(world, x, minZ, player, dustOptions);
            showParticleAtTopBlock(world, x, maxZ, player, dustOptions);
        }

        // Lados izquierdo y derecho del rectángulo (excluyendo esquinas ya hechas)
        for (int z = minZ + 1; z < maxZ; z++) {
            showParticleAtTopBlock(world, minX, z, player, dustOptions);
            showParticleAtTopBlock(world, maxX, z, player, dustOptions);
        }
    }

    private void showParticleAtTopBlock(World world, int x, int z, Player player, Particle.DustOptions dustOptions) {
        int y = world.getHighestBlockYAt(x, z); // El bloque más alto en esa columna
        Location loc = new Location(world, x + 0.5, y + 1, z + 0.5); // Centro del bloque
        world.spawnParticle(Particle.DUST, loc, 1, 0, 0, 0, 0, dustOptions, true);
    }

    private void startShowingArea(Player player) {
        UUID uuid = player.getUniqueId();

        if (activeDisplays.containsKey(uuid)) return; // Ya está mostrándose

        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    stopShowingArea(player);
                    return;
                }
                showArea(player);
            }
        }.runTaskTimer(plugin, 0L, 20L); // Cada 1 segundo

        activeDisplays.put(uuid, task);
    }

    private void stopShowingArea(Player player) {
        UUID uuid = player.getUniqueId();
        if (activeDisplays.containsKey(uuid)) {
            activeDisplays.get(uuid).cancel();
            activeDisplays.remove(uuid);
        }
    }

    public void switchShowingArea(Player player){
        UUID uuid = player.getUniqueId();
        if (activeDisplays.containsKey(uuid)){
            stopShowingArea(player);
        }else {
            startShowingArea(player);
        }
    }

    public String toString() {
        return "Área en " + getWorld().getName() +
                " desde [" + corner1.getBlockX() + ", " + corner1.getBlockY() + ", " + corner1.getBlockZ() + "]" +
                " hasta [" + corner2.getBlockX() + ", " + corner2.getBlockY() + ", " + corner2.getBlockZ() + "]";
    }
}
