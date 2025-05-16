package pando.org.pandoSabor.discord.commands.types;

import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.events.message.MessageReceivedEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pando.org.pandoSabor.PandoSabor;
import pando.org.pandoSabor.discord.commands.DiscordCommand;
import pando.org.pandoSabor.playerData.SaborPlayer;

import java.awt.*;
import java.util.UUID;

public class PlayerDataCommand extends DiscordCommand {

    public PlayerDataCommand(PandoSabor plugin) {
        super(plugin);
    }

    @Override
    public String setPrefix() {
        return "!jugador";
    }

    @Override
    public String setDescription() {
        return "Investiga al Jugador especificado";
    }

    @Override
    public void task(MessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().split("\\s+");

        if (args.length < 2) {
            event.getChannel().sendMessage("❌ Usa el comando así: `!jugador <nombreJugador>`").queue();
            return;
        }


        String playerName = args[1];

        UUID playerUUID = Bukkit.getOfflinePlayer(playerName).getUniqueId();

        if(!Bukkit.getOfflinePlayer(playerName).hasPlayedBefore()) {
            event.getChannel().sendMessage("❌ El jugador nunca ha entrado al Reino del Sabor").queue();
            return;
        }

        SaborPlayer loaded = getPlugin().getSaborPlayerStorage().load(playerUUID);

        EmbedBuilder embed = getEmbed(loaded, playerUUID, playerName);

        event.getChannel().sendMessageEmbeds(embed.build()).queue();
    }

    @NotNull
    private static EmbedBuilder getEmbed(SaborPlayer loaded, UUID playerUUID, String playerName) {
        int points = loaded.getPoints();

        int deaths = loaded.getDeaths();

        int infamy = loaded.getInfamy();

        String skinUrl = "https://minotar.net/armor/body/" + playerUUID + "/100.png";

        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Archivo **oficial** extraído del castillo 👑");
        embed.setTitle(playerName,"https://discord.com/channels/626162191145500693/1368737008650027088");
        embed.setImage(skinUrl);
        embed.setColor(Color.CYAN);
        embed.setFooter("Base de datos de los investigadores del Sabor 🗂️","https://minotar.net/cube/" + playerName);
        embed.addField("⭐ Puntos", String.valueOf(points), true);
        embed.addField("💀 Muertes", String.valueOf(deaths), true);
        embed.addField("🔥 Infamia", String.valueOf(infamy), true);
        return embed;
    }
}
