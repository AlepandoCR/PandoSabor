package pando.org.pandoSabor.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import pando.org.pandoSabor.PandoSabor;
import pando.org.pandoSabor.playerData.economy.WealthBlock;
import pando.org.pandoSabor.utils.Model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

// Clase del comando
public class ModelCommand implements CommandExecutor, TabCompleter {

    private final PandoSabor plugin;

    public ModelCommand(PandoSabor plugin) {
        this.plugin = plugin;

        this.plugin.getCommand("model").setExecutor(this);

        this.plugin.getCommand("model").setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player) || !sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "Este comando solo está disponible para operadores.");
            return true;
        }
        boolean scaled = false;

        float scale = 1;

        if(args.length == 2){
            scaled = true;
            try{
                scale = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                scale = 0.9f;
            }

        }

        if(args.length == 1 || scaled){
            ArmorStand armorStand = ((Player) sender).getWorld().spawn(((Player) sender).getLocation(), ArmorStand.class, CreatureSpawnEvent.SpawnReason.CUSTOM);

            armorStand.setPersistent(true);
            armorStand.setInvulnerable(true);

            Model model = new Model(plugin);

            model.createModel(args[0],armorStand,scale);
        }

        return true;

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.isOp()) return Collections.emptyList();


        if (args.length == 1) {
            return Arrays.asList("king","soul");
        }

        return Collections.emptyList();
    }
}
