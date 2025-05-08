package pando.org.pandoSabor.game.arena;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import pando.org.pandoSabor.PandoSabor;
import pando.org.pandoSabor.utils.Area;
import pando.org.pandoSabor.utils.ManagedArea;
import pando.org.pandoSabor.utils.Model;

public class ArenaManager {
    private final Area arenaArea;
    private final ManagedArea arenaAreaManager;
    private final PandoSabor plugin;
    private final PiglinAmbushManager piglinAmbushManager;
    private final MiniMessage mini = MiniMessage.miniMessage();
    private int deathPiglins = 0;
    private Model firePitModel;

    private final static Location ARENA_SPAWN = new Location(Bukkit.getWorld("overworld"),-1861.5,19.5,548.5);

    public ArenaManager(Area arenaArea, PandoSabor plugin) {
        this.arenaArea = arenaArea;
        this.plugin = plugin;
        this.piglinAmbushManager = new PiglinAmbushManager(plugin,arenaArea);
        this.arenaAreaManager = new ManagedArea(arenaArea,plugin);

        arenaAreaManager.hidePlayers();

        firePitModel = spawnFirePitModel(plugin);

        run();
    }

    @NotNull
    private Model spawnFirePitModel(PandoSabor plugin) {

        Location spawnModel = ARENA_SPAWN.clone().add(0,-0.5,0);

        Model r = new Model(plugin, "souls",spawnModel);

        r.createModel(2);
        return r;
    }

    public void run(){
        new BukkitRunnable() {
            int seconds = 0;
            boolean onEvent = false;
            int eventSeconds = 0;
            @Override
            public void run() {
                spawnAmbushes();
                manageEvents();

                seconds++;
            }

            private void manageEvents() {
                if (onEvent) {
                    if (eventSeconds % 60 == 0 && eventSeconds != 0) {
                        onEvent = false;
                        eventSeconds = 0;
                        arenaAreaManager.hidePlayers();
                        arenaAreaManager.announce(mini.deserialize("<gold>[<yellow>Arena del Sabor<gold>] <aqua>Evento PvP desactivado"));
                    }
                    eventSeconds++;
                } else {
                    if (deathPiglins % 40 == 0) {
                        if (!arenaArea.getPlayersInArea().isEmpty()) {
                            onEvent = true;
                            arenaAreaManager.showPlayers();
                            arenaAreaManager.announce(mini.deserialize("<gold>[<yellow>Arena del Sabor<gold>] <red>Evento PvP activado (<gray>Duración : 1 minuto<red>)"));
                        }
                    }
                }
            }

            private void spawnAmbushes() {
                if (seconds % 30 == 0) {
                    for (Player player : arenaArea.getPlayersInArea()) {
                        if(piglinAmbushManager.getPiglins(player).size() < 10){
                            int lvl = plugin.getSaborManager().getPlayer(player.getUniqueId()).getInfamy();
                            piglinAmbushManager.spawnAmbush(player, lvl, ARENA_SPAWN);
                            player.sendMessage(mini.deserialize("<gold>[<yellow>Arena del Sabor<gold>] <gray>Nueva oleada!"));
                        }
                    }
                }
            }

        }.runTaskTimer(plugin,0L,20L);
    }

    public Model getFirePitModel() {
        return firePitModel;
    }

    public int getDeathPiglins() {
        return deathPiglins;
    }

    public void setDeathPiglins(int deathPiglins) {
        this.deathPiglins = deathPiglins;
    }

    public void addDeathPiglin(){
        this.deathPiglins++;
    }

    public Area getArenaArea() {
        return arenaArea;
    }

    public ManagedArea getArenaAreaManager() {
        return arenaAreaManager;
    }

    public PandoSabor getPlugin() {
        return plugin;
    }

    public PiglinAmbushManager getPiglinAmbushManager() {
        return piglinAmbushManager;
    }
}
