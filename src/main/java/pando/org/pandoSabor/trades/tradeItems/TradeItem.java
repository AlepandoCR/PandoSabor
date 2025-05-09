package pando.org.pandoSabor.trades.tradeItems;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pando.org.pandoSabor.PandoSabor;
import pando.org.pandoSabor.trades.Trade;

import java.util.List;

public abstract class TradeItem {
    private ItemStack stack;
    private final int price;
    private final PandoSabor plugin;

    protected TradeItem(ItemStack stack, int price, PandoSabor plugin) {
        this.stack = stack;
        this.price = price;
        this.plugin = plugin;
    }

    public ItemStack getStack() {
        return stack;
    }

    public PandoSabor getPlugin() {
        return plugin;
    }

    public int getPrice() {
        return price;
    }

    public Trade toTrade(Player player) {
        return new Trade(stack, price, plugin, player);
    }

    public void setStack(ItemStack stack) {
        this.stack = stack;
    }

    public ItemStack getDisplay() {
        ItemStack display = new ItemStack(getStack());
        ItemMeta meta = display.getItemMeta();

        if (meta != null) {
            List<Component> lore = List.of(
                    Component.text("Cantidad: ", NamedTextColor.AQUA)
                            .append(Component.text(stack.getAmount(), NamedTextColor.WHITE)),

                    Component.text("Precio: ", NamedTextColor.YELLOW)
                            .append(Component.text(price + " ₡", NamedTextColor.GOLD))
            );

            meta.lore(lore);
            display.setItemMeta(meta);
        }

        return display;
    }
}
