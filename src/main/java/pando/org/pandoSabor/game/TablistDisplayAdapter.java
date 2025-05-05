package pando.org.pandoSabor.game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import pando.org.pandoSabor.PandoSabor;
import pando.org.pandoSabor.playerData.SaborPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class TablistDisplayAdapter {

    /**
     * Muestra continuamente la información de un SaborPlayer en la tablist, usando un Supplier para que sea reactiva.
     */
    public static void startLiveTablist(Player viewer, Supplier<SaborPlayer> supplier, PandoSabor plugin) {
        // Ejecuta una tarea periódica para actualizar la tablist
        Bukkit.getScheduler().runTaskTimerAsynchronously(
                plugin,
                () -> {
                    if (!viewer.isOnline()) return;

                    SaborPlayer saborPlayer = supplier.get();

                    // Header
                    String header = "&6&l» " + getName(saborPlayer.getUuid()) + " &7[Datos]\n"
                            + "&ePuntos: &a" + saborPlayer.getPoints() + "   "
                            + "&cMuertes: &4" + saborPlayer.getDeaths();

                    // Footer
                    String footer = "&7Jugadores conocidos: &b" + saborPlayer.getUnlockedPlayers().size()
                            + " &8| &7Asesinados: &4" + saborPlayer.getKilledPlayers().size();

                    // Mostrar en la tablist
                    TablistUtils.clearTablist(viewer,plugin);
                    TablistUtils.setTabHeaderFooter(viewer, header, footer,plugin);

                },
                0L, 60L // Ejecuta cada 3 segundos aproximadamente
        );
    }

    private static String getName(UUID uuid) {
        OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
        return p.getName() != null ? p.getName() : "Desconocido";
    }

    private static List<String> formatList(List<UUID> uuids, String color) {
        List<String> list = new ArrayList<>();
        for (UUID id : uuids) {
            list.add(color + getName(id));
        }
        return list;
    }
}
