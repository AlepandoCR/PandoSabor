package pando.org.pandoSabor.trades.tradeItems;

import org.bukkit.inventory.ItemStack;
import pando.org.pandoSabor.PandoSabor;

public abstract class PrizeItem {
    protected ItemStack item;
    protected PandoSabor plugin;

    public PrizeItem(PandoSabor plugin){
        this.plugin = plugin;
        item = createItem();
    }

    public PrizeItem(){
        item = createItem();
    }

    protected abstract ItemStack createItem();


    public ItemStack getItem() {
        return item;
    }
}
