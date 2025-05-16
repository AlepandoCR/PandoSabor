package pando.org.pandoSabor.discord.commands;

import github.scarsz.discordsrv.dependencies.jda.api.events.message.MessageReceivedEvent;
import pando.org.pandoSabor.PandoSabor;

public abstract class DiscordCommand {
    private final String prefix;
    private final PandoSabor plugin;
    private final String description;

    protected DiscordCommand(PandoSabor plugin) {
        this.prefix = setPrefix();
        this.plugin = plugin;
        this.description = setDescription();
    }

    public abstract String setPrefix();

    public abstract String setDescription();

    public abstract void task(MessageReceivedEvent event);

    public String getPrefix() {
        return prefix;
    }

    public PandoSabor getPlugin() {
        return plugin;
    }

    public String getDescription() {
        return description;
    }
}
