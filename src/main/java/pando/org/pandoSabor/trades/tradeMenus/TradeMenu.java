package pando.org.pandoSabor.trades.tradeMenus;


import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pando.org.pandoSabor.PandoSabor;
import pando.org.pandoSabor.trades.tradeItems.TradeItem;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class TradeMenu {

    private final List<TradeItem> items = new ArrayList<>();
    private final PandoSabor plugin;
    private final Inventory inventory;
    private int currentPage = 0;
    private final int itemsPerPage = 36; // Espacio sin botones (6 filas * 9 columnas - 6 slots para botones o decoración)
    private final Player player;

    protected TradeMenu(PandoSabor plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.inventory = createInventory();

        plugin.getTradeMenusManager().addMenu(this);
    }

    public List<TradeItem> getItems() {
        return items;
    }

    public void addItem(TradeItem tradeItem) {
        items.add(tradeItem);
    }

    public void addAll(TradeItem... tradeItems) {
        items.addAll(List.of(tradeItems));
    }

    public Inventory getInventory() {
        return inventory;
    }

    public PandoSabor getPlugin() {
        return plugin;
    }

    public abstract Inventory createInventory();

    public void updateInventoryPage() {
        inventory.clear();

        int start = currentPage * itemsPerPage;
        int end = Math.min(start + itemsPerPage, items.size());

        List<TradeItem> visibleItems = items.subList(start, end);
        for (int i = 0; i < visibleItems.size(); i++) {
            inventory.setItem(i, visibleItems.get(i).getDisplay());
        }

        // Botón página anterior
        if (currentPage > 0) {
            inventory.setItem(45, createControlItem(Material.ARROW, ChatColor.BOLD + "Página anterior"));
        }

        // Botón página siguiente
        if ((currentPage + 1) * itemsPerPage < items.size()) {
            inventory.setItem(53, createControlItem(Material.ARROW, ChatColor.BOLD + "Página siguiente"));
        }
    }

    private ItemStack createControlItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text(name));
            item.setItemMeta(meta);
        }
        return item;
    }

    @Nullable
    public TradeItem getTradeItemFromClick(InventoryClickEvent event) {
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null) return null;

        for (TradeItem tradeItem : getItems()) {
            if (tradeItem.getDisplay().isSimilar(clicked)) {
                return tradeItem;
            }
        }

        return null;
    }

    public void nextPage() {
        if ((currentPage + 1) * itemsPerPage < items.size()) {
            currentPage++;
            updateInventoryPage();
        }
    }

    public void previousPage() {
        if (currentPage > 0) {
            currentPage--;
            updateInventoryPage();
        }
    }

    public Player getPlayer() {
        return player;
    }

    public void open(){
        if(inventory != null){
            player.openInventory(inventory);
            updateInventoryPage();
        }
    }

    public boolean playerHasOpenInv() {
        return player.getOpenInventory().getTopInventory().equals(inventory);
    }

}
