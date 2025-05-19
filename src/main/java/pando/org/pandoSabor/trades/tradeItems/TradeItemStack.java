package pando.org.pandoSabor.trades.tradeItems;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import pando.org.pandoSabor.PandoSabor;

public class TradeItemStack extends TradeItem{
    public TradeItemStack(ItemStack stack, int price, PandoSabor plugin) {
        super(new ItemStack(Material.AIR), price, plugin);
        setStack(stack);
    }
}
