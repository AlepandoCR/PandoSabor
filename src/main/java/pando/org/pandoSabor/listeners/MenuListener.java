package pando.org.pandoSabor.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import pando.org.pandoSabor.PandoSabor;
import pando.org.pandoSabor.trades.tradeItems.TradeItem;
import pando.org.pandoSabor.trades.tradeMenus.TradeMenu;
import pando.org.pandoSabor.trades.tradeMenus.TradeMenusManager;
import pando.org.pandoSabor.trades.tradeMenus.menus.WeaponShopMenu;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MenuListener implements Listener {

    private final TradeMenusManager tradeMenusManager;
    private final PandoSabor plugin;
    private final Map<UUID, Long> tradeCooldowns = new HashMap<>();
    private final long COOLDOWN_TICKS = 40;

    public MenuListener(PandoSabor plugin) {
        this.plugin = plugin;
        this.tradeMenusManager = plugin.getTradeMenusManager();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getClickedInventory() == null) return;

        TradeMenu menu = tradeMenusManager.getMenu(player);
        if (menu == null) return;

        if (!menu.playerHasOpenInv()) return;

        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        String name = clicked.getItemMeta() != null ? clicked.getItemMeta().getDisplayName() : "";

        if (name.contains("Página anterior")) {
            menu.previousPage();
            return;
        }

        if (name.contains("Página siguiente")) {
            menu.nextPage();
            return;
        }

        // Cooldown check
        long currentTick = System.currentTimeMillis() / 50;
        long lastTrade = tradeCooldowns.getOrDefault(player.getUniqueId(), 0L);
        if ((currentTick - lastTrade) < COOLDOWN_TICKS) {
            player.sendMessage("§8[§6SaborShop§8] §c¡Espera un momento antes de comprar otro objeto!");
            return;
        }

        TradeItem clickedTradeItem = menu.getTradeItemFromClick(event);
        if (clickedTradeItem == null) return;

        clickedTradeItem.toTrade(player).execute();

        tradeCooldowns.put(player.getUniqueId(), currentTick);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;

        TradeMenu menu = tradeMenusManager.getMenu(player);
        if (menu != null) {
            tradeMenusManager.removeMenu(menu);
        }
    }

    @EventHandler
    public void onPlayerClickVillager(PlayerInteractEntityEvent event){
        Player player = event.getPlayer();

        if(event.getRightClicked() instanceof Villager v){
            if(v.getScoreboardTags().contains("shop")){
                event.setCancelled(true);
                new WeaponShopMenu(plugin,player).open();
            }
        }


    }

}
