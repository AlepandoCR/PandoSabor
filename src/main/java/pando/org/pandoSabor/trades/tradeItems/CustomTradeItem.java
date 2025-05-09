package pando.org.pandoSabor.trades.tradeItems;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import pando.org.pandoSabor.PandoSabor;

public abstract class CustomTradeItem extends TradeItem{
    protected CustomTradeItem(int price, PandoSabor plugin) {
        super(new ItemStack(Material.AIR), price, plugin);
        setStack(createItem());
    }

    public abstract ItemStack createItem();
}
