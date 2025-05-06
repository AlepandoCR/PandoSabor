package pando.org.pandoSabor.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import pando.org.pandoSabor.PandoSabor;
import pando.org.pandoSabor.playerData.economy.WealthBlock;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

// Clase del comando
public class CobrarCommand implements CommandExecutor, TabCompleter {

    private final PandoSabor plugin;

    public CobrarCommand(PandoSabor plugin) {
        this.plugin = plugin;

        this.plugin.getCommand("cobrar").setExecutor(this);

        this.plugin.getCommand("cobrar").setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player) || !sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "Este comando solo está disponible para operadores.");
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage(ChatColor.YELLOW + "Uso: /cobrar <jugador> <cantidad>");
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Jugador no encontrado.");
            return true;
        }

        int cantidad;
        try {
            cantidad = Integer.parseInt(args[1]);
            if (cantidad <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "La cantidad debe ser un número positivo.");
            return true;
        }

        List<WealthBlock> bloques = plugin.getWealthBlockStorage().getAllBlocksByPlayer(target.getUniqueId());

        if (bloques.size() < cantidad) {
            sender.sendMessage(ChatColor.RED + "El jugador no tiene suficientes bloques (" + bloques.size() + " disponibles).");
            return true;
        }

        // Eliminar la cantidad especificada (asumiendo que se puede modificar la lista directamente)
        for (int i = 0; i < cantidad; i++) {
            bloques.removeLast().chargeBlock(plugin);
        }

        sender.sendMessage(ChatColor.GREEN + "Has cobrado " + cantidad + " bloque(s) a " + target.getName() + ".");
        target.sendMessage(ChatColor.RED + "Se te han cobrado " + cantidad + " bloque(s).");

        return true;

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.isOp()) return Collections.emptyList();

        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        } else if (args.length == 2) {
            return Arrays.asList("100", "250", "500", "1000");
        }

        return Collections.emptyList();
    }
}
