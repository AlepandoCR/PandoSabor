package pando.org.pandoSabor.game.rey;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import pando.org.pandoSabor.PandoSabor;
import pando.org.pandoSabor.game.rey.quests.Quest;
import pando.org.pandoSabor.game.rey.quests.QuestGenerator;
import pando.org.pandoSabor.utils.Model;

public class King {

    private final PandoSabor plugin;
    private int nivelIra; // 0 - 100
    private final QuestGenerator generador;
    private final Model kingModel;
    private final Model kingModelColiseum;

    public King(PandoSabor plugin, QuestGenerator generador) {
        this.plugin = plugin;
        this.generador = generador;
        this.kingModelColiseum = spawnKingModelColiseum();
        this.nivelIra = 0;

        this.kingModel = spawnKingModel();
    }

    public Humor getActualState() {
        return Humor.fromLvl(nivelIra);
    }

    public Component getDialog() {
        return Dialog.getDialog(getActualState());
    }

    public void addIra(int cantidad) {
        this.nivelIra = Math.min(100, this.nivelIra + cantidad);
    }

    public void reduceIra(int cantidad) {
        this.nivelIra = Math.max(0, this.nivelIra - cantidad);
    }

    public Model getKingModel() {
        return kingModel;
    }

    public Model getKingModelColiseum() {
        return kingModelColiseum;
    }

    public void createQuest(Player player){
        player.sendMessage(getDialog());
        Quest q = assingQuest(player);
        plugin.getQuestManager().assingQuest(player,q);
    }

    private Quest assingQuest(Player jugador) {
        return generador.generateQuest(jugador, nivelIra);
    }

    public int getIraLevel() {
        return nivelIra;
    }

    public QuestGenerator getGenerator() {
        return generador;
    }

    public Location getKingLocation(){
        return new Location(Bukkit.getWorld("overworld"),-342.5, 92.00, 1649.5,180,0);
    }


    public Model spawnKingModel(){
        Model r = new Model(plugin, "king" ,getKingLocation());

        r.createModel(1);

        return r;
    }

    public Model spawnKingModelColiseum(){
        Model r = new Model(plugin, "king" ,new Location(Bukkit.getWorld("overworld"),-1140.5,145.00,321.5,0,0));

        r.createModel(1);

        return r;
    }
}
