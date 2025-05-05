package pando.org.pandoSabor.playerData;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SaborPlayer {

    private UUID uuid;
    private List<UUID> targets;
    private List<UUID> unlockedPlayers;
    private List<UUID> killedPlayers;
    private int points;
    private int deaths;

    public SaborPlayer(UUID uuid) {
        this.uuid = uuid;
        this.points = 0;
        this.deaths = 0;
        this.targets = new ArrayList<>();
        this.unlockedPlayers = new ArrayList<>();
        this.killedPlayers = new ArrayList<>();
    }

    public SaborPlayer() {
        this.targets = new ArrayList<>();
        this.unlockedPlayers = new ArrayList<>();
    }


    public UUID getUuid() {
        return uuid;
    }

    public List<UUID> getUnlockedPlayers() {
        return unlockedPlayers;
    }

    public int getDeaths() {
        return deaths;
    }

    public int getPoints() {
        return points;
    }

    public List<UUID> getTargets() {
        return targets;
    }

    public List<UUID> getKilledPlayers() {
        return killedPlayers;
    }

    public void setUnlockedPlayers(List<UUID> players) {
        this.unlockedPlayers = players;
    }

    public void setKilledPlayers(List<UUID> players) {
        this.killedPlayers = players;
    }

    public void setTargets(List<UUID> targets) {
        this.targets = targets;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void addPoints(int amount) {
        this.points += amount;
    }

    public void addDeath() {
        this.deaths++;
    }

    public void addTarget(UUID target) {
        if (!targets.contains(target)) {
            this.targets.add(target);
        }
    }

    public void addUnlockedPlayers(UUID target) {
        if (!unlockedPlayers.contains(target)) {
            this.unlockedPlayers.add(target);
        }
    }

    public void addKilledPlayer(UUID target) {
        if (!killedPlayers.contains(target)) {
            this.killedPlayers.add(target);
        }
    }

    public void removeTarget(UUID target) {
        this.targets.remove(target);
    }

    public boolean hasTarget(UUID target) {
        return this.targets.contains(target);
    }

    public String unlockedToString() {
        if (unlockedPlayers.isEmpty()) return ChatColor.GRAY + "Ninguno";
        StringBuilder sb = new StringBuilder();
        for (UUID id : unlockedPlayers) {
            OfflinePlayer p = Bukkit.getOfflinePlayer(id);
            sb.append(ChatColor.GREEN).append("✓ ").append(ChatColor.YELLOW).append(p.getName()).append(ChatColor.GRAY).append(", ");
        }
        return sb.substring(0, sb.length() - 2);
    }

    public String killedToString() {
        if (killedPlayers.isEmpty()) return ChatColor.GRAY + "Ninguno";
        StringBuilder sb = new StringBuilder();
        for (UUID id : killedPlayers) {
            OfflinePlayer p = Bukkit.getOfflinePlayer(id);
            sb.append(ChatColor.RED).append("⚔️ ").append(ChatColor.YELLOW).append(p.getName()).append(ChatColor.GRAY).append(", ");
        }
        return sb.substring(0, sb.length() - 2);
    }

    public String targetsToString() {
        if (targets.isEmpty()) return ChatColor.GRAY + "Ninguno";
        StringBuilder sb = new StringBuilder();
        for (UUID id : targets) {
            OfflinePlayer p = Bukkit.getOfflinePlayer(id);
            sb.append(ChatColor.LIGHT_PURPLE).append("➤ ").append(ChatColor.YELLOW).append(p.getName()).append(ChatColor.GRAY).append(", ");
        }
        return sb.substring(0, sb.length() - 2);
    }
}
