package pando.org.pandoSabor.discord.commands.types;

import github.scarsz.discordsrv.dependencies.jda.api.events.message.MessageReceivedEvent;
import org.bukkit.Bukkit;
import pando.org.pandoSabor.PandoSabor;
import pando.org.pandoSabor.discord.commands.DiscordCommand;
import pando.org.pandoSabor.discord.tab.FakeTabUtil;

public class ChatCommand extends DiscordCommand {
    public ChatCommand(PandoSabor plugin) {
        super(plugin);
    }

    @Override
    public String setPrefix() {
        return "!chat";
    }

    @Override
    public String setDescription() {
        return "Te conecta con el chat del servidor";
    }

    @Override
    public void task(MessageReceivedEvent event) {
        String senderName = event.getAuthor().getName();
        String discordUserId = event.getAuthor().getId();

        boolean current = getPlugin().getDiscordListener().getChatToggles().getOrDefault(discordUserId, false);
        getPlugin().getDiscordListener().getChatToggles().put(discordUserId, !current);

        event.getChannel().sendMessage(
                current ?
                        "🛑 Chat relay desactivado. Ya no verás ni enviarás mensajes al servidor." :
                        "✅ Ahora estás conectado al chat del servidor. Tus mensajes serán reenviados."
        ).queue();

        String connection = current ? "§cdesconectado" : "§6conectado";
        String formatted = "§8[§bDiscord§8] §b🤖 §7" + senderName + " §8» §7" + "Se ha " + connection + " §7al chat desde §bDiscord";
        Bukkit.broadcastMessage(formatted);

        if (current) {
            FakeTabUtil.removeFakePlayer(senderName);
        } else {
            FakeTabUtil.addFakePlayer(senderName);
        }
    }
}
