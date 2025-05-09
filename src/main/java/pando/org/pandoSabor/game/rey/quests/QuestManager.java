package pando.org.pandoSabor.game.rey.quests;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import pando.org.pandoSabor.PandoSabor;
import pando.org.pandoSabor.playerData.SaborPlayer;

import java.util.*;

public class QuestManager {

    private final PandoSabor plugin;

    private final Map<UUID, Quest> activeQuests = new HashMap<>();

    public QuestManager(PandoSabor plugin) {
        this.plugin = plugin;
    }

    public void assingQuest(Player player, Quest mision) {
        activeQuests.put(player.getUniqueId(), mision);
        player.sendMessage(mision.description());
    }

    public Optional<Quest> getQuest(Player player) {
        return Optional.ofNullable(activeQuests.get(player.getUniqueId()));
    }

    public void removeQuest(Player player) {
        activeQuests.remove(player.getUniqueId());
    }

    public boolean hasQuest(Player player) {
        return activeQuests.containsKey(player.getUniqueId());
    }

    public void checkCompletedQuests() {
        Iterator<Map.Entry<UUID, Quest>> it = activeQuests.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, Quest> entry = it.next();
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player != null && entry.getValue().isCompleted(player)) {
                player.sendMessage("§6¡Has completado la misión del Rey!");
                rewardPlayer(player);
                it.remove();
            }
        }
    }

    public void rewardPlayer(Player player){
        player.getInventory().addItem(ItemStack.of(Material.DIAMOND,Math.max(1, plugin.getKing().getIraLevel() / 4)));
        SaborPlayer saborPlayer = plugin.getSaborManager().getPlayer(player.getUniqueId());
        saborPlayer.addPoints(1);
        plugin.getSaborPlayerStorage().save(saborPlayer);
    }

    public void startCheckingQuests(){
        new BukkitRunnable(){

            @Override
            public void run() {
                checkCompletedQuests();
            }
        }.runTaskTimer(plugin,0,20L);
    }
}
