package pando.org.pandoSabor.playerData;

import org.jetbrains.annotations.NotNull;
import pando.org.pandoSabor.PandoSabor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class SaborManager {
    private final List<SaborPlayer> saborPlayers = new ArrayList<>();
    private final PandoSabor plugin;

    public SaborManager(PandoSabor plugin) {
        this.plugin = plugin;
    }

    public List<SaborPlayer> getSaborPlayers() {
        return saborPlayers;
    }

    public void addSaborPlayer(@NotNull SaborPlayer player) {
        saborPlayers.add(player);
    }

    public void removeSaborPlayer(SaborPlayer player) {
        saborPlayers.remove(player);
    }

    public SaborPlayer getPlayer(@NotNull UUID uuid) {
        return saborPlayers.stream().filter(Objects::nonNull)
                .filter(player -> player.getUuid().equals(uuid))
                .findFirst()
                .orElse(null);
    }

    public void closePlayer(UUID uuid) {
        SaborPlayer aux = getPlayer(uuid);
        if (aux != null) {
            removeSaborPlayer(aux);
            plugin.getSaborPlayerStorage().save(aux);
        }
    }

    public void startPlayer(UUID uuid) {
        SaborPlayer player = plugin.getSaborPlayerStorage().load(uuid);
        addSaborPlayer(player);
    }
}
