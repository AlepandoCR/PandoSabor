package pando.org.pandoSabor.game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
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
    public static void startLiveTablist(Player viewer, @NotNull Supplier<SaborPlayer> supplier, PandoSabor plugin) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            if (!viewer.isOnline()) return;

            SaborPlayer saborPlayer = supplier.get();

            String playerName = getName(saborPlayer.getUuid());

            int dinero = plugin.getWealthBlockStorage().getAllBlocksByPlayer(viewer.getUniqueId()).size();
            int puntos = saborPlayer.getPoints();
            int muertes = saborPlayer.getDeaths();

            // Header estilizado
            String header = "\n"
                    + "&e\uD83C\uDF7B &b&lReino del Sabor &e\uD83C\uDF7B \n\n"
                    + "&6&l⟪ " + playerName + " &6&l⟫\n"
                    + "&6Dinero: &a$" + dinero
                    + " &8| &6Puntos: &a" + puntos
                    + " &8| &6Muertes: &c" + muertes + "/3"
                    + "\n";

            // Footer decorado
            String footer = "\n"
                    + "&7Jugadores conocidos: &b" + saborPlayer.getUnlockedPlayers().size()
                    + " &8| &7Asesinados: &c" + saborPlayer.getKilledPlayers().size()
                    + "\n" + "\n" + "&7Infamia: &e" + saborPlayer.getInfamy() + " &6🔥" + "\n" + "\n" + "\n"
                    + "&7¡No sabrás nada de quien no conoces!";

            TablistUtils.clearTablist(viewer, plugin);
            TablistUtils.setTabHeaderFooter(viewer, header, footer, plugin);
        }, 0L, 20L); // Actualiza cada segundo
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
