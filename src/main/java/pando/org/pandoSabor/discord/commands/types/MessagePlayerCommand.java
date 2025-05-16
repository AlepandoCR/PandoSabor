package pando.org.pandoSabor.discord.commands.types;

import github.scarsz.discordsrv.dependencies.jda.api.events.message.MessageReceivedEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import pando.org.pandoSabor.PandoSabor;
import pando.org.pandoSabor.discord.commands.DiscordCommand;

public class MessagePlayerCommand extends DiscordCommand {
    public MessagePlayerCommand(PandoSabor plugin) {
        super(plugin);
    }

    @Override
    public String setPrefix() {
        return "!msg";
    }

    @Override
    public String setDescription() {
        return "Envía un mensaje privado a un jugador (!msg Landon Hola!)";
    }

    @Override
    public void task(MessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().split(" ", 3);
        if (args.length < 3) {
            event.getChannel().sendMessage("Uso correcto: `!msg <jugador> <mensaje>`").queue();
            return;
        }

        String senderName = event.getAuthor().getName();

        String playerName = args[1];
        String message = args[2];

        Player target = Bukkit.getPlayerExact(playerName);
        if (target == null || !target.isOnline()) {
            event.getChannel().sendMessage("⚠️ El jugador `" + playerName + "` no está conectado.").queue();
            return;
        }

        target.sendMessage(ChatColor.AQUA + "§8[§b📩§8] " + ChatColor.AQUA + senderName + ChatColor.DARK_GRAY + " » " + ChatColor.GRAY + message);
        event.getChannel().sendMessage("✅ Mensaje enviado a `" + playerName + "` con éxito.").queue();
    }
}
