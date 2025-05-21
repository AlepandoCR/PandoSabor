package pando.org.pandoSabor.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.BundleMeta;
import pando.org.pandoSabor.PandoSabor;

import java.util.function.Predicate;

public class DiamondBlockRestrictions implements Listener {

    private final PandoSabor plugin;

    public DiamondBlockRestrictions(PandoSabor plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    private boolean isDiamondBlock(ItemStack item) {
        return item != null && item.getType() == Material.DIAMOND_BLOCK;
    }

    private boolean isPlayerInventory(Inventory inventory) {
        return inventory.getType() == InventoryType.PLAYER || inventory.getHolder() instanceof Player;
    }

    @EventHandler
    public void onCraftBundle(CraftItemEvent event) {
        ItemStack result = event.getRecipe().getResult();
        if (result.getItemMeta() instanceof BundleMeta) {
            event.setCancelled(true);
            event.getWhoClicked().sendMessage("§cNo puedes craftear Bundles.");
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (player.isOp()) return;

        ItemStack current = event.getCurrentItem();
        ItemStack cursor = event.getCursor();
        Inventory clickedInventory = event.getClickedInventory();
        Inventory topInventory = event.getView().getTopInventory();
        Inventory bottomInventory = event.getView().getBottomInventory();
        InventoryAction action = event.getAction();
        ClickType click = event.getClick();

        if(clickedInventory.getType().equals(InventoryType.CRAFTING)){
            return;
        }

        boolean isMovingToPlayerInv = clickedInventory != null && clickedInventory.equals(topInventory) &&
                event.getRawSlot() >= player.getInventory().getSize();

        boolean b = clickedInventory == null || (!clickedInventory.equals(bottomInventory) && !isMovingToPlayerInv);
        if (isDiamondBlock(cursor)) {
            if (b) {
                event.setCancelled(true);
                return;
            }
        }

        if (isDiamondBlock(current)) {
            if (clickedInventory != null && clickedInventory.equals(bottomInventory)) {
                return;
            }

            if (event.isShiftClick()) {

                if(clickedInventory == null){
                    event.setCancelled(true);
                    return;
                }

                Inventory destination = (clickedInventory.equals(topInventory)) ? bottomInventory : topInventory;
                if (!destination.equals(player.getInventory())) {
                    event.setCancelled(true);
                    return;
                }
            }

            if (b) {
                event.setCancelled(true);
            }
        }
    }


    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        ItemStack item = event.getOldCursor();
        if (!isDiamondBlock(item)) return;

        for (int slot : event.getRawSlots()) {
            if (slot >= event.getView().getTopInventory().getSize()) continue;
            event.setCancelled(true);
            break;
        }
    }


    @EventHandler
    public void onHopperPickup(InventoryPickupItemEvent event) {
        if (isDiamondBlock(event.getItem().getItemStack())) {
            event.setCancelled(true);
        }
    }
}
