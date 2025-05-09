package pando.org.pandoSabor.game.rey.quests;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ProgressMessage {

    public static void sendProgressMessage(Player player, int progress, int amount){
        player.sendMessage(ChatColor.GOLD + "[" + ChatColor.YELLOW + "Misión del Rey" + ChatColor.GOLD + "]" + ChatColor.GRAY + " Progreso de misión actualizado" + ChatColor.DARK_AQUA + " |" + ChatColor.AQUA + "[" + ChatColor.GRAY + progress + ChatColor.GOLD + "/" + ChatColor.YELLOW + amount + ChatColor.AQUA + "]" + ChatColor.DARK_AQUA + "|");
    }
}
