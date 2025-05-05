package pando.org.pandoSabor.game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.*;

public class TablistUtils {

    private static final Map<UUID, Set<String>> fakeEntriesMap = new HashMap<>();

    /**
     * Limpia completamente la tablist del jugador (sin eliminar al jugador en sí).
     * Borra equipos y entradas personalizadas si fueron añadidas previamente.
     */
    public static void clearTablist(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        player.setScoreboard(scoreboard);
        setTabHeaderFooter(player, "", "");

        fakeEntriesMap.remove(player.getUniqueId());
    }

    /**
     * Establece el header y footer personalizado para el jugador.
     * Soporta colores y estilos.
     */
    public static void setTabHeaderFooter(Player player, String header, String footer) {
        player.setPlayerListHeaderFooter(
                ChatColor.translateAlternateColorCodes('&', header),
                ChatColor.translateAlternateColorCodes('&', footer)
        );
    }

    /**
     * Agrega entradas falsas a la tablist del jugador, simulando jugadores.
     * Se pueden usar para mostrar datos personalizados (ranking, estadísticas, etc).
     */
    public static void injectFakeEntries(Player player, List<String> entries) {
        Scoreboard scoreboard = player.getScoreboard();
        if (scoreboard == null || scoreboard == Bukkit.getScoreboardManager().getMainScoreboard()) {
            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            player.setScoreboard(scoreboard);
        }

        Team fakeTeam = scoreboard.getTeam("fake_tablist");
        if (fakeTeam == null) {
            fakeTeam = scoreboard.registerNewTeam("fake_tablist");
            fakeTeam.setCanSeeFriendlyInvisibles(false);
            fakeTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        }

        // Eliminar entradas anteriores
        Set<String> oldEntries = fakeEntriesMap.getOrDefault(player.getUniqueId(), new HashSet<>());
        for (String old : oldEntries) {
            scoreboard.resetScores(old);
            fakeTeam.removeEntry(old);
        }

        Set<String> newEntries = new HashSet<>();

        for (String rawEntry : entries) {
            String entry = ChatColor.translateAlternateColorCodes('&', rawEntry);

            // Asegurar que no sea más de 16 caracteres (limitación de nombres en tablist)
            if (entry.length() > 16) {
                entry = entry.substring(0, 16);
            }

            Score score = (Score) (scoreboard.getObjective("fake") == null ?
                                scoreboard.registerNewObjective("fake", "dummy", "FakeTab") :
                                scoreboard.getObjective("fake"));

            score.getScoreboard().getObjective("fake").getScore(entry).setScore(0);
            fakeTeam.addEntry(entry);
            newEntries.add(entry);
        }

        fakeEntriesMap.put(player.getUniqueId(), newEntries);
    }

    /**
     * Limpia solo las entradas falsas en la tablist del jugador.
     */
    public static void clearFakeEntries(Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        if (scoreboard == null) return;

        Team fakeTeam = scoreboard.getTeam("fake_tablist");
        if (fakeTeam != null) {
            Set<String> entries = fakeEntriesMap.getOrDefault(player.getUniqueId(), new HashSet<>());
            for (String entry : entries) {
                scoreboard.resetScores(entry);
                fakeTeam.removeEntry(entry);
            }
        }
    }

}
