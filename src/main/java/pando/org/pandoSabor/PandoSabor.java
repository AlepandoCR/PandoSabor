package pando.org.pandoSabor;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.units.qual.A;
import pando.org.pandoSabor.commands.CobrarCommand;
import pando.org.pandoSabor.commands.ModelCommand;
import pando.org.pandoSabor.database.MySQL;
import pando.org.pandoSabor.database.SaborPlayerStorage;
import pando.org.pandoSabor.advancements.AdvancementManager;
import pando.org.pandoSabor.game.InfamyManager;
import pando.org.pandoSabor.game.arena.ArenaManager;
import pando.org.pandoSabor.listeners.*;
import pando.org.pandoSabor.playerData.SaborManager;
import pando.org.pandoSabor.playerData.economy.WealthBlockStorage;
import pando.org.pandoSabor.utils.Area;

import java.sql.SQLException;

public final class PandoSabor extends JavaPlugin {

    private final MySQL database = new MySQL("mysql.apexhosting.gdn","3306","apexMC2821456","apexMC2821456","k2IZ2hliI7JivAAscfl&zTM8");
    private SaborPlayerStorage saborPlayerStorage;
    private final SaborManager saborManager = new SaborManager(this);
    private AdvancementManager advancementManager;
    private WealthBlockStorage wealthBlockStorage;
    private InfamyManager infamyManager;
    private CobrarCommand cobrarCommand;
    private InfamyDisplayManager infamyDisplayManager;
    private ArenaManager arenaManager;
    private ModelCommand modelCommand;

    private Area CASTLE_AREA;


    @Override
    public void onEnable() {

        Location corner1 = new Location(Bukkit.getWorld("overworld"),-384,87,-1601);
        Location corner2 = new Location(Bukkit.getWorld("overworld"),-312,165,1689);

        CASTLE_AREA = new Area(corner1,corner2,this);

        try {
            database.connect();

            wealthBlockStorage = new WealthBlockStorage(database.getConnection(),this);
            saborPlayerStorage = new SaborPlayerStorage(database.getConnection(), this);

            saborPlayerStorage.syncTableStructure();

            advancementManager = new AdvancementManager(this);
            infamyDisplayManager = new InfamyDisplayManager(this);

            cobrarCommand = new CobrarCommand(this);
            modelCommand = new ModelCommand(this);

            infamyManager = new InfamyManager(this);

            startArenaManager();

            enableListeners();
        } catch (SQLException e) {
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        arenaManager.getArenaAreaManager().stopManaging();
    }

    private void startArenaManager(){
        Area arena = new Area(new Location(Bukkit.getWorld("overworld"),-1906.5,65,592.5),new Location(Bukkit.getWorld("overworld"),-1802.5,-41,488.5),this);
        arenaManager = new ArenaManager(arena,this);
    }

    private void enableListeners(){
        enableListener(new EntityListener(this),new ChatManager(this),infamyDisplayManager ,new BlockListener(this),new PlayerListener(this), new AreaPlayerVisibilityController(CASTLE_AREA,this));
    }

    private void enableListener(Listener... listeners){
        for (Listener listener : listeners) {
            getServer().getPluginManager().registerEvents(listener,this);
        }
    }

    public Area getCastleArea() {
        return CASTLE_AREA;
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public WealthBlockStorage getWealthBlockStorage() {
        return wealthBlockStorage;
    }

    public MySQL getDatabase() {
        return database;
    }

    public InfamyDisplayManager getInfamyDisplayManager() {
        return infamyDisplayManager;
    }

    public CobrarCommand getCobrarCommand() {
        return cobrarCommand;
    }

    public InfamyManager getInfamyManager() {
        return infamyManager;
    }

    public SaborPlayerStorage getSaborPlayerStorage() {
        return saborPlayerStorage;
    }

    public SaborManager getSaborManager() {
        return saborManager;
    }

    public AdvancementManager getAdvancementManager() {
        return advancementManager;
    }
}
