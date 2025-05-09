package pando.org.pandoSabor.playerData.economy;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import pando.org.pandoSabor.PandoSabor;

import javax.annotation.Nullable;
import java.util.UUID;

public class WealthBlock {
    private final UUID ownerUuid;
    private final String world;
    private final int x;
    private final int y;
    private final int z;
    private final String material;

    public WealthBlock(UUID ownerUuid, String world, int x, int y, int z, String material) {
        this.ownerUuid = ownerUuid;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.material = material;
    }

    public WealthBlock(UUID ownerUuid, String world, Location location, String material) {
        this.ownerUuid = ownerUuid;
        this.world = world;
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
        this.material = material;
    }

    public UUID getOwnerUuid() { return ownerUuid; }
    public String getWorld() { return world; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getZ() { return z; }
    public String getMaterial() { return material; }

    public String getLocationKey() {
        return world + ":" + x + "," + y + "," + z;
    }

    public Location getLocation() {
        return new Location(Bukkit.getWorld(world), x, y, z);
    }

    public void chargeBlock(PandoSabor plugin){
        if(getLocation() != null){
            plugin.getWealthBlockStorage().removeBlock(world,getLocation());
            getLocation().getBlock().setType(Material.AIR);
        }
    }
}