package pando.org.pandoSabor.commands;

import net.minecraft.server.level.ServerLevel;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;
import pando.org.pandoSabor.PandoSabor;
import pando.org.pandoSabor.game.boss.BossSpawner;
import pando.org.pandoSabor.game.boss.destroyer.CustomVindicator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BossCommand implements CommandExecutor, TabCompleter {

    private final PandoSabor plugin;

    public BossCommand(PandoSabor plugin) {
        this.plugin = plugin;

        this.plugin.getCommand("boss").setExecutor(this);

        this.plugin.getCommand("boss").setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player) || !player.isOp()) {
            sender.sendMessage(ChatColor.RED + "Este comando solo está disponible para operadores.");
            return true;
        }

        ServerLevel serverLevel = ((CraftWorld) player.getWorld()).getHandle();

        double x = player.getLocation().getX();
        double y = player.getLocation().getY();
        double z = player.getLocation().getZ();

        CustomVindicator vindicator = CustomVindicator.spawn(serverLevel,
                x, y, z,
                plugin
        );

        new BossSpawner(plugin).spawnBoss(vindicator);

        return true;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.isOp()) return Collections.emptyList();


        if (args.length == 1) {
            return Arrays.asList("destroyer");
        }

        return Collections.emptyList();
    }
}
