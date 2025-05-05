package pando.org.pandoSabor;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import pando.org.pandoSabor.database.MySQL;
import pando.org.pandoSabor.database.SaborPlayerStorage;
import pando.org.pandoSabor.advancements.AdvancementManager;
import pando.org.pandoSabor.listeners.PlayerListener;
import pando.org.pandoSabor.playerData.SaborManager;

import java.sql.SQLException;

public final class PandoSabor extends JavaPlugin {

    private final MySQL database = new MySQL("mysql.apexhosting.gdn","3306","apexMC2821456","apexMC2821456","k2IZ2hliI7JivAAscfl&zTM8");
    private SaborPlayerStorage saborPlayerStorage;
    private final SaborManager saborManager = new SaborManager(this);
    private AdvancementManager advancementManager;
    @Override
    public void onEnable() {
        advancementManager = new AdvancementManager(this);


        // Plugin startup logic
        try {
            database.connect();
            saborPlayerStorage = new SaborPlayerStorage(database.getConnection(), this);
            saborPlayerStorage.syncTableStructure();
            enableListeners();
        } catch (SQLException e) {
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    private void enableListeners(){
        enableListener(new PlayerListener(this));
    }

    private void enableListener(Listener... listeners){
        for (Listener listener : listeners) {
            getServer().getPluginManager().registerEvents(listener,this);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public MySQL getDatabase() {
        return database;
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
