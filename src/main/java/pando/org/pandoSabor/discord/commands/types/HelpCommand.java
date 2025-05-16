package pando.org.pandoSabor.discord.commands.types;

import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.events.message.MessageReceivedEvent;
import github.scarsz.discordsrv.dependencies.kyori.adventure.bossbar.BossBar;
import pando.org.pandoSabor.PandoSabor;
import pando.org.pandoSabor.discord.commands.DiscordCommand;

import java.util.List;

public class HelpCommand extends DiscordCommand {
    public HelpCommand(PandoSabor plugin) {
        super(plugin);
    }

    @Override
    public String setPrefix() {
        return "!help";
    }

    @Override
    public String setDescription() {
        return "Despliega el menu de comandos";
    }

    @Override
    public void task(MessageReceivedEvent event) {
        List<DiscordCommand> commands = getPlugin().getDiscordListener().getCommands();

        StringBuilder msg = new StringBuilder(">>> **📜 Lista de comandos disponibles:**\n\n");

        for (DiscordCommand command : commands) {
            String commandString = command.getPrefix();
            String description = command.getDescription();

            msg.append("🔹 `")
                    .append(commandString)
                    .append("` - *")
                    .append(description)
                    .append("*\n");
        }

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(BossBar.Color.PURPLE.ordinal());
        embed.setTitle("Comandos del Bot 🤖");
        embed.setDescription(msg.toString());
        embed.setFooter("Bot de soporte - PandoSabor", "https://imgur.com/sii16BQ.png");

        event.getAuthor().openPrivateChannel().queue(privateChannel -> {
            privateChannel.sendMessageEmbeds(embed.build()).queue();
        });
    }


}
