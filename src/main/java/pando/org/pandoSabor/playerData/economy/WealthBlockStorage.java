package pando.org.pandoSabor.playerData.economy;

import org.bukkit.Location;
import pando.org.pandoSabor.PandoSabor;

import java.sql.*;
import java.util.*;

public class WealthBlockStorage {

    private final Connection connection;
    private final PandoSabor plugin;

    public WealthBlockStorage(Connection connection, PandoSabor plugin) {
        this.connection = connection;
        this.plugin = plugin;

        createTableIfNeeded();
    }

    private void createTableIfNeeded() {
        String sql = "CREATE TABLE IF NOT EXISTS wealth_blocks (" +
                "owner_uuid VARCHAR(36), " +
                "world VARCHAR(100), " +
                "x INT, y INT, z INT, " +
                "material VARCHAR(50), " +
                "PRIMARY KEY (world, x, y, z)" +
                ")";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            plugin.getLogger().severe("[WealthBlockStorage] Error creando tabla: " + e.getMessage());
        }
    }

    public void saveBlock(WealthBlock block) {
        String sql = "REPLACE INTO wealth_blocks (owner_uuid, world, x, y, z, material) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, block.getOwnerUuid().toString());
            ps.setString(2, block.getWorld());
            ps.setInt(3, block.getX());
            ps.setInt(4, block.getY());
            ps.setInt(5, block.getZ());
            ps.setString(6, block.getMaterial());
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe("[WealthBlockStorage] Error guardando bloque: " + e.getMessage());
        }
    }

    public void removeBlock(String world, int x, int y, int z) {
        String sql = "DELETE FROM wealth_blocks WHERE world = ? AND x = ? AND y = ? AND z = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, world);
            ps.setInt(2, x);
            ps.setInt(3, y);
            ps.setInt(4, z);
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe("[WealthBlockStorage] Error eliminando bloque: " + e.getMessage());
        }
    }

    public void removeBlock(String world, Location location) {
        removeBlock(world,location.getBlockX(),location.getBlockY(),location.getBlockZ());
    }

    public WealthBlock getBlock(String world, int x, int y, int z) {
        String sql = "SELECT * FROM wealth_blocks WHERE world = ? AND x = ? AND y = ? AND z = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, world);
            ps.setInt(2, x);
            ps.setInt(3, y);
            ps.setInt(4, z);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                UUID owner = UUID.fromString(rs.getString("owner_uuid"));
                String material = rs.getString("material");
                return new WealthBlock(owner, world, x, y, z, material);
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("[WealthBlockStorage] Error buscando bloque: " + e.getMessage());
        }
        return null;
    }

    public WealthBlock getBlock(String world, Location location) {
        return getBlock(world,location.getBlockX(),location.getBlockY(),location.getBlockZ());
    }

    public List<WealthBlock> getAllBlocksByPlayer(UUID playerUuid) {
        List<WealthBlock> blocks = new ArrayList<>();
        String sql = "SELECT * FROM wealth_blocks WHERE owner_uuid = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, playerUuid.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String world = rs.getString("world");
                int x = rs.getInt("x");
                int y = rs.getInt("y");
                int z = rs.getInt("z");
                String material = rs.getString("material");

                blocks.add(new WealthBlock(playerUuid, world, x, y, z, material));
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("[WealthBlockStorage] Error obteniendo bloques del jugador: " + e.getMessage());
        }
        return blocks;
    }
}
