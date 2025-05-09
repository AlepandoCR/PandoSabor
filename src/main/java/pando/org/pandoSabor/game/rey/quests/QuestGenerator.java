package pando.org.pandoSabor.game.rey.quests;

import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import pando.org.pandoSabor.PandoSabor;

import java.util.*;

public class QuestGenerator {

    private final PandoSabor plugin;

    private static final Random random = new Random();

    public QuestGenerator(PandoSabor plugin) {
        this.plugin = plugin;
    }

    public Quest generateQuest(Player jugador, int ira) {
        int tipo = random.nextInt(3);

        return switch (tipo) {
            case 0 -> new KillQuest(randomMob(),dificultad(ira), plugin, jugador);
            case 1 -> new BreakQuest(plugin,randomBlock(), dificultad(ira), jugador);
            case 2 -> new ResearchQuest(plugin,randomLocation(jugador), jugador);
            default -> throw new IllegalStateException("Tipo de misión no válido");
        };
    }

    private int dificultad(int ira) {
        return Math.max(1, ira / 10 + random.nextInt(3));
    }

    private EntityType randomMob() {
        EntityType[] mobs = {EntityType.ZOMBIE, EntityType.SKELETON, EntityType.CREEPER, EntityType.SPIDER};
        return mobs[random.nextInt(mobs.length)];
    }

    private Material randomBlock() {
        Material[] types = {Material.IRON_ORE, Material.LAPIS_ORE, Material.GOLD_ORE, Material.NETHER_GOLD_ORE};
        return types[random.nextInt(types.length)];
    }

    private Location randomLocation(Player player) {
        Collection<? extends Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());

        players.remove(player);

        if (players.isEmpty()) return new Location(Bukkit.getWorld("overworld"), 0, 100, 0);

        Player chosen = players.stream().toList().get(random.nextInt(players.size()));
        Location base = chosen.getLocation();
        return base.clone().add(random.nextInt(20) - 10, 0, random.nextInt(20) - 10);
    }

}
