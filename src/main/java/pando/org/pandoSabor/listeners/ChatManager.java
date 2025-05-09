package pando.org.pandoSabor.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Axolotl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import pando.org.pandoSabor.PandoSabor;
import pando.org.pandoSabor.playerData.SaborPlayer;
import pando.org.pandoSabor.playerData.economy.WealthBlock;
import pando.org.pandoSabor.playerData.economy.WealthBlockStorage;

public class ChatManager implements Listener {

    private final PandoSabor plugin;

    public ChatManager(PandoSabor plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player sender = event.getPlayer();

        String displayName = ChatColor.GOLD + "🍻 " + ChatColor.GRAY + sender.getName();


        String message = ChatColor.DARK_GRAY + "» " + ChatColor.WHITE + event.getMessage();

        String prefix = ChatColor.DARK_PURPLE + "[" + ChatColor.LIGHT_PURPLE + "Sabor" + ChatColor.DARK_PURPLE + "] ";

        event.setFormat(prefix + displayName + " " + message);
    }


    @EventHandler
    public void onPrivateMessage(PlayerCommandPreprocessEvent event) {
        String msg = event.getMessage();
        Player sender = event.getPlayer();

        String[] aliases = {"/tell", "/msg", "/w", "/whisper", "/pm", "/reply"};
        for (String alias : aliases) {
            if (msg.toLowerCase().startsWith(alias + " ")) {
                String[] args = msg.split(" ");
                if (args.length < 3) return; // Comando mal formado

                Player target = Bukkit.getPlayerExact(args[1]);
                if (target == null || !target.isOnline()) return;

                SaborPlayer saborSender = plugin.getSaborManager().getPlayer(sender.getUniqueId());

                if (!saborSender.getUnlockedPlayers().contains(target.getUniqueId())) {
                    sender.sendMessage(ChatColor.RED + "No conoces a ese jugador aun");
                    event.setCancelled(true);
                }

                break;
            }
        }
    }
}
