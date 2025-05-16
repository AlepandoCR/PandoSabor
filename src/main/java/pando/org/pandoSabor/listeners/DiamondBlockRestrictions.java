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

        ItemStack cursor = event.getCursor();
        ItemStack current = event.getCurrentItem();
        Inventory topInv = event.getView().getTopInventory();
        Inventory clickedInventory = event.getClickedInventory();
        InventoryAction action = event.getAction();
        ClickType click = event.getClick();

        if(current != null){
            if (current.getItemMeta() instanceof BundleMeta && isDiamondBlock(cursor)) {
                event.setCancelled(true);
                return;
            }

            if (cursor.getItemMeta() instanceof BundleMeta && isDiamondBlock(current)) {
                event.setCancelled(true);
                return;
            }
        }


        if (clickedInventory != null && isPlayerInventory(clickedInventory) && topInv.getType().equals(InventoryType.CRAFTING) && !(action == InventoryAction.PLACE_ALL_INTO_BUNDLE || action == InventoryAction.PLACE_SOME_INTO_BUNDLE)) return;

        if ((action == InventoryAction.PLACE_ALL || action == InventoryAction.PLACE_ONE || action == InventoryAction.PLACE_SOME || action == InventoryAction.PLACE_ALL_INTO_BUNDLE || action == InventoryAction.PLACE_SOME_INTO_BUNDLE)
                && isDiamondBlock(cursor)) {

            boolean cancel = true;

            if(clickedInventory != null){
                if(isPlayerInventory(clickedInventory)){
                    cancel  = false;
                }
            }

            event.setCancelled(cancel);

            return;
        }

        if (action == InventoryAction.MOVE_TO_OTHER_INVENTORY && isDiamondBlock(current)) {
            event.setCancelled(true);
            return;
        }

        if (clickedInventory != null && clickedInventory.getType() == InventoryType.SHULKER_BOX && isDiamondBlock(cursor)) {
            event.setCancelled(true);
            return;
        }

        if (cursor.getType() == Material.BUNDLE) {
            event.setCancelled(true);
            return;
        }

        if (current != null && current.getType() == Material.BUNDLE && isDiamondBlock(cursor)) {
            event.setCancelled(true);
            return;
        }

        if (isDiamondBlock(cursor) && cursor.getItemMeta() instanceof BlockStateMeta meta) {
            if (meta.getBlockState() instanceof InventoryHolder holder) {
                for (ItemStack content : holder.getInventory().getContents()) {
                    if (isDiamondBlock(content)) {
                        event.setCancelled(true);
                        return;
                    }
                }
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
