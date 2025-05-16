package pando.org.pandoSabor;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import pando.org.pandoSabor.commands.BossCommand;
import pando.org.pandoSabor.commands.CobrarCommand;
import pando.org.pandoSabor.commands.ModelCommand;
import pando.org.pandoSabor.database.MySQL;
import pando.org.pandoSabor.database.SaborPlayerStorage;
import pando.org.pandoSabor.advancements.AdvancementManager;
import pando.org.pandoSabor.discord.DiscordListener;
import pando.org.pandoSabor.game.InfamyManager;
import pando.org.pandoSabor.game.RewardManager;
import pando.org.pandoSabor.game.arena.ArenaManager;
import pando.org.pandoSabor.game.rey.King;
import pando.org.pandoSabor.game.rey.KingAngerSystem;
import pando.org.pandoSabor.game.rey.quests.QuestGenerator;
import pando.org.pandoSabor.game.rey.quests.QuestManager;
import pando.org.pandoSabor.game.time.TimeController;
import pando.org.pandoSabor.listeners.*;
import pando.org.pandoSabor.playerData.SaborManager;
import pando.org.pandoSabor.database.WealthBlockStorage;
import pando.org.pandoSabor.trades.tradeMenus.TradeMenusManager;
import pando.org.pandoSabor.listeners.MenuListener;
import pando.org.pandoSabor.utils.Area;
import pando.org.pandoSabor.utils.ModelManager;

import java.sql.SQLException;

import static pando.org.pandoSabor.discord.tab.FakeTabUtil.startTabUpdater;

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
    private ModelManager modelManager;
    private ModelListener modelListener;
    private QuestManager questManager;
    private King king;
    private KingAngerSystem kingAngerSystem;
    private TradeMenusManager tradeMenusManager;
    private MenuListener menuListener;
    private DiscordListener discordListener;
    private BossCommand bossCommand;
    private RewardManager rewardManager;
    private DiamondBlockRestrictions diamondBlockRestrictions;

    private TimeController timeController;

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
            tradeMenusManager = new TradeMenusManager(this);
            rewardManager = new RewardManager(this);

            modelManager = new ModelManager(this);

            cobrarCommand = new CobrarCommand(this);
            modelCommand = new ModelCommand(this);
            bossCommand = new BossCommand(this);

            infamyManager = new InfamyManager(this);
            modelListener = new ModelListener(this);
            menuListener = new MenuListener(this);
            discordListener = new DiscordListener(this);
            diamondBlockRestrictions = new DiamondBlockRestrictions(this);


            startArenaManager();

            startTabUpdater(this);

            king = new King(this, new QuestGenerator(this));
            kingAngerSystem = new KingAngerSystem(this);

            enableListeners();

            questManager = new QuestManager(this);

            questManager.startCheckingQuests();

            kingAngerSystem.start();

            rewardManager.automateRewards();

            startTime();
        } catch (SQLException e) {
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    public void startTime(){
        Bukkit.getScheduler().runTaskLater(this, r -> {
            timeController = TimeController.startup(this);
        },20L);
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
        enableListener(diamondBlockRestrictions,discordListener ,menuListener, modelListener, new EntityListener(this),new ChatManager(this),infamyDisplayManager ,new BlockListener(this),new PlayerListener(this), new AreaPlayerVisibilityController(CASTLE_AREA,this));
    }

    private void enableListener(Listener... listeners){
        for (Listener listener : listeners) {
            getServer().getPluginManager().registerEvents(listener,this);
        }
    }

    public TimeController getTimeController() {
        return timeController;
    }

    public RewardManager getRewardManager() {
        return rewardManager;
    }

    public ModelListener getModelListener() {
        return modelListener;
    }

    public Area getCASTLE_AREA() {
        return CASTLE_AREA;
    }

    public ModelCommand getModelCommand() {
        return modelCommand;
    }

    public MenuListener getMenuListener() {
        return menuListener;
    }

    public KingAngerSystem getKingAngerSystem() {
        return kingAngerSystem;
    }

    public BossCommand getBossCommand() {
        return bossCommand;
    }

    public DiscordListener getDiscordListener() {
        return discordListener;
    }

    public TradeMenusManager getTradeMenusManager() {
        return tradeMenusManager;
    }

    public QuestManager getQuestManager() {
        return questManager;
    }

    public King getKing() {
        return king;
    }

    public ModelManager getModelManager() {
        return modelManager;
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
