package pando.org.pandoSabor.discord;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import github.scarsz.discordsrv.dependencies.jda.api.entities.ChannelType;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import github.scarsz.discordsrv.dependencies.jda.api.events.message.MessageReceivedEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;
import pando.org.pandoSabor.PandoSabor;
import pando.org.pandoSabor.discord.tab.FakeTabUtil;

import java.util.HashMap;
import java.util.Map;

public class DiscordListener extends ListenerAdapter implements Listener {

    private final PandoSabor plugin;
    private final Map<String, Boolean> chatToggles = new HashMap<>();

    public DiscordListener(PandoSabor plugin) {
        this.plugin = plugin;
        startDiscordListener(plugin);
    }

    private void startDiscordListener(PandoSabor plugin) {
        Bukkit.getScheduler().runTaskLater(plugin, r -> {
           register(DiscordSRV.getPlugin().getJda());
        }, 20L * 6);
    }

    public void register(JDA jda) {
        jda.addEventListener(this);
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (!event.isFromType(ChannelType.PRIVATE)) return;
        if (event.getAuthor().isBot()) return;

        String senderName = event.getAuthor().getName();
        String discordUserId = event.getAuthor().getId();
        String message = event.getMessage().getContentRaw();

        if (message.equalsIgnoreCase("!chat")) {
            boolean current = chatToggles.getOrDefault(discordUserId, false);
            chatToggles.put(discordUserId, !current);

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

            return;
        }

        if (chatToggles.getOrDefault(discordUserId, false)) {

            Bukkit.getScheduler().runTask(plugin, () -> {
                String formatted = "§8[§bDiscord§8] §b🤖 §7" + senderName + " §8» §f" + message;
                Bukkit.broadcastMessage(formatted);

                chatToggles.forEach((userId, enabled) -> {
                    if (enabled) {
                        if(!userId.equals(discordUserId)){
                            User user = DiscordSRV.getPlugin().getJda().getUserById(userId);
                            if (user != null) {
                                user.openPrivateChannel().queue(privateChannel -> {
                                    privateChannel.sendMessage("[Discord] 🤖 " + senderName + " » " + message).queue();
                                });
                            }
                        }
                    }
                });
            });
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String prefix = "[Sabor] 🍻 ";
        String msg = prefix + event.getPlayer().getName() + " » " + event.getMessage();

        chatToggles.keySet().forEach(discordUserId -> {
            if (chatToggles.get(discordUserId)) {
                User user = DiscordSRV.getPlugin().getJda().getUserById(discordUserId);
                if (user != null) {
                    user.openPrivateChannel()
                            .flatMap(channel -> channel.sendMessage(msg))
                            .queue();
                }
            }
        });
    }
}
