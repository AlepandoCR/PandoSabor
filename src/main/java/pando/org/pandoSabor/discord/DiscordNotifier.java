package pando.org.pandoSabor.discord;

import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import github.scarsz.discordsrv.DiscordSRV;
import java.util.UUID;

public class DiscordNotifier {

    public static void notifyRobbery(UUID player) {
        String discordId = DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(player);

        if (discordId == null) {
            return;
        }

        User user = DiscordSRV.getPlugin().getJda().retrieveUserById(discordId).complete();

        if (user != null) {
            user.openPrivateChannel().queue(channel -> channel.sendMessage("⚠️ ¡Alerta! Están robando tus diamantes en el Reino del Sabor...").queue());
        }
    }
}
