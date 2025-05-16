package pando.org.pandoSabor.discord.commands.types;

import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.events.message.MessageReceivedEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pando.org.pandoSabor.PandoSabor;
import pando.org.pandoSabor.discord.commands.DiscordCommand;

import java.awt.Color;
import java.util.Collection;

public class OnlinePlayersCommand extends DiscordCommand {
    public OnlinePlayersCommand(PandoSabor plugin) {
        super(plugin);
    }

    @Override
    public String setPrefix() {
        return "!online";
    }

    @Override
    public String setDescription() {
        return "Muestra los jugadores actualmente conectados al servidor.";
    }

    @Override
    public void task(MessageReceivedEvent event) {
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("👥 Jugadores conectados");
        embed.setColor(Color.CYAN);

        if (players.isEmpty()) {
            embed.setDescription("No hay jugadores conectados en este momento.");
        } else {
            StringBuilder sb = new StringBuilder();
            for (Player player : players) {
                sb.append("• ").append(player.getName()).append("\n");
            }
            embed.setDescription(sb.toString());
            embed.setFooter("Total: " + players.size() + " jugador(es)");
        }

        event.getChannel().sendMessageEmbeds(embed.build()).queue();
    }
}
