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
import pando.org.pandoSabor.discord.commands.types.*;
import pando.org.pandoSabor.discord.commands.DiscordCommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiscordListener extends ListenerAdapter implements Listener {

    private final PandoSabor plugin;
    private final List<DiscordCommand> commands = new ArrayList<>();
    private final Map<String, Boolean> chatToggles = new HashMap<>();

    public DiscordListener(PandoSabor plugin) {
        this.plugin = plugin;
        startDiscordListener(plugin);
        startCommands();
    }

    private void startCommands(){
        commands.add(new ChatCommand(plugin));
        commands.add(new HelpCommand(plugin));
        commands.add(new MessagePlayerCommand(plugin));
        commands.add(new OnlinePlayersCommand(plugin));
        commands.add(new PlayerDataCommand(plugin));
    }

    public Map<String, Boolean> getChatToggles() {
        return chatToggles;
    }

    private void startDiscordListener(PandoSabor plugin) {
        Bukkit.getScheduler().runTaskLater(plugin, r -> register(DiscordSRV.getPlugin().getJda()), 20L * 6);
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

        boolean commandExecuted = false;

        for (DiscordCommand command : commands) {
            if(message.startsWith(command.getPrefix())){
                command.task(event);
                commandExecuted = true;
            }
        }

        if(commandExecuted) return;

        relayChat(discordUserId, senderName, message);
    }

    private void relayChat(String discordUserId, String senderName, String message) {
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

    public List<DiscordCommand> getCommands() {
        return commands;
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
