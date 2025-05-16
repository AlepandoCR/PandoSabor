package pando.org.pandoSabor.game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pando.org.pandoSabor.PandoSabor;
import pando.org.pandoSabor.playerData.SaborPlayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RewardManager {

    private final PandoSabor plugin;

    public RewardManager(PandoSabor plugin) {
        this.plugin = plugin;
    }


    public void distributeRewards(Map<Player, Integer> playerPoints) {
        if (playerPoints.isEmpty()) return;

        List<Map.Entry<Player, Integer>> sorted = playerPoints.entrySet().stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .toList();

        int topScore = sorted.getFirst().getValue();

        List<String> rewards = List.of(
                "Legendaria",
                "Epica",
                "Rara",
                "Comun",
                "KK"
        );

        for (int i = 0; i < sorted.size(); i++) {
            Player player = sorted.get(i).getKey();
            int points = sorted.get(i).getValue();

            double relativeScore = (double) points / topScore;

            if (relativeScore < 0.4) { // 40%
                player.sendMessage(ChatColor.GRAY + "No recibiste recompensa por ser un " + ChatColor.RED + "sobo "+  ChatColor.GOLD + "¡Mejora para la próxima! (Consigue más puntos)");
                continue;
            }

            double positionRatio = (double) i / sorted.size();
            int rewardIndex = (int) Math.floor(positionRatio * rewards.size());

            if (i == 0) rewardIndex = 0;
            rewardIndex = Math.min(rewardIndex, rewards.size() - 1);

            String reward = rewards.get(rewardIndex);
            giveReward(player, reward);

            player.sendMessage(ChatColor.GOLD + "¡Recibiste una recompensa: " + ChatColor.AQUA + reward + ChatColor.GOLD + "!");
        }
    }


    private void giveReward(Player player, String reward) {
        switch (reward) {
            case "Legendaria" -> player.getInventory().addItem(new ItemStack(Material.DIAMOND, 4));
            case "Epica" -> player.getInventory().addItem(new ItemStack(Material.DIAMOND));
            case "Rara" -> player.getInventory().addItem(new ItemStack(Material.EMERALD, 20));
            case "Comun" -> player.getInventory().addItem(new ItemStack(Material.IRON_INGOT, 5));
            case "KK" -> player.giveExp(50);
        }
    }

    private void executeReward(){
        Map<Player, Integer> playerPoints = new HashMap<>();

        for (SaborPlayer saborPlayer : plugin.getSaborManager().getSaborPlayers()) {
            Player player = Bukkit.getPlayer(saborPlayer.getUuid());

            if(player == null) continue;

            playerPoints.put(player,saborPlayer.getPoints());
        }

        distributeRewards(playerPoints);
    }


    public void automateRewards(){
        Bukkit.getScheduler().runTaskTimer(plugin, r -> executeReward(),0L,20L * 60 * 60);
    }


}
