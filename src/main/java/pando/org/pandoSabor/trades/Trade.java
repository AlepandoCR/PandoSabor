package pando.org.pandoSabor.trades;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pando.org.pandoSabor.PandoSabor;
import pando.org.pandoSabor.playerData.economy.WealthBlock;

import java.util.List;

public class Trade {
    private final ItemStack stack;
    private final int price;
    private final PandoSabor plugin;
    private final Player player;

    private final String PREFIX = ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + "SaborShop" + ChatColor.DARK_GRAY + "] " + ChatColor.RESET;

    public Trade(ItemStack stack, int price, PandoSabor plugin, Player player) {
        this.stack = stack;
        this.price = price;
        this.plugin = plugin;
        this.player = player;
    }

    public int getPrice() {
        return price;
    }

    public ItemStack getStack() {
        return stack;
    }

    private boolean charge() {

        List<WealthBlock> bloques = plugin.getWealthBlockStorage().getAllBlocksByPlayer(player.getUniqueId());

        if (bloques.size() < price) {
            player.sendMessage(ChatColor.RED + "No tiene suficientes bloques (" + bloques.size() + " disponibles).");
            return false;
        }


        for (int i = 0; i < price; i++) {
            bloques.removeLast().chargeBlock(plugin);
        }

        return true;
    }

    public void execute() {
        if (!playerHasSpace()) {
            player.sendMessage(PREFIX + ChatColor.RED + "No tienes espacio en el inventario.");
            return;
        }

        if (charge()) {
            player.getInventory().addItem(stack);
            String itemName = getItemDisplayName(stack);
            player.sendMessage(PREFIX + ChatColor.GREEN + "Has comprado " + itemName + ChatColor.GREEN + " por " + ChatColor.YELLOW + price + ChatColor.AQUA + " bloques.");
        } else {
            player.sendMessage(PREFIX + ChatColor.RED + "No tienes suficientes bloques para comprar este objeto.");
        }
    }

    public Player getPlayer() {
        return player;
    }

    private boolean playerHasSpace() {
        for (ItemStack itemStack : player.getInventory()) {
            if (itemStack == null || itemStack.getType().equals(Material.AIR)) {
                return true;
            }
        }
        return false;
    }

    public PandoSabor getPlugin() {
        return plugin;
    }

    private String getItemDisplayName(ItemStack item) {
        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            Component displayName = item.getItemMeta().displayName();

            return ChatColor.AQUA + LegacyComponentSerializer.legacySection().serialize(displayName);
        } else {
            return ChatColor.AQUA + item.getType().toString().toLowerCase().replace("_", " ");
        }
    }
}
