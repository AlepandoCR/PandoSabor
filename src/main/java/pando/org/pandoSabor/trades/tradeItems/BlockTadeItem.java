package pando.org.pandoSabor.trades.tradeItems;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import pando.org.pandoSabor.PandoSabor;

public class BlockTadeItem extends TradeItem{
    public BlockTadeItem(Material material, int price, PandoSabor plugin) {
        super(new ItemStack(Material.AIR), price, plugin);
        setStack(new ItemStack(material));
    }
}
