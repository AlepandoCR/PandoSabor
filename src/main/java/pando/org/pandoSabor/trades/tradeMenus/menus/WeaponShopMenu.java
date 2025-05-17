package pando.org.pandoSabor.trades.tradeMenus.menus;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import pando.org.pandoSabor.PandoSabor;
import pando.org.pandoSabor.trades.tradeItems.BlockTadeItem;
import pando.org.pandoSabor.trades.tradeMenus.TradeMenu;

public class WeaponShopMenu extends TradeMenu {

    public WeaponShopMenu(PandoSabor plugin, Player player) {
        super(plugin, player);

        addAll(
                new BlockTadeItem(Material.TOTEM_OF_UNDYING, 5, plugin),
                new BlockTadeItem(Material.END_CRYSTAL, 10, plugin),
                new BlockTadeItem(Material.ENCHANTED_GOLDEN_APPLE, 5, plugin),
                new BlockTadeItem(Material.OBSIDIAN, 1, plugin)
        );
    }

    @Override
    public Inventory createInventory() {
        return Bukkit.createInventory(null, 54, ChatColor.DARK_GRAY.toString() + ChatColor.BOLD + "Tienda del Sabor");
    }
}
