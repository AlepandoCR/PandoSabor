package pando.org.pandoSabor.trades.tradeMenus;

import org.bukkit.entity.Player;
import pando.org.pandoSabor.PandoSabor;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class TradeMenusManager {
    private final List<TradeMenu> menus = new ArrayList<>();
    private final PandoSabor plugin;

    public TradeMenusManager(PandoSabor plugin) {
        this.plugin = plugin;
    }

    public void addMenu(TradeMenu tradeMenu){
        getMenus().add(tradeMenu);
    }

    public void removeMenu(TradeMenu tradeMenu){
        getMenus().remove(tradeMenu);
    }

    @Nullable
    public TradeMenu getMenu(Player player) {
        plugin.getLogger().info("Buscando menú para " + player.getName());

        for (TradeMenu menu : menus) {
            plugin.getLogger().info("Menú registrado para " + menu.getPlayer().getName());
            if (menu.getPlayer().getUniqueId().equals(player.getUniqueId())) {
                plugin.getLogger().info("¡Menú encontrado!");
                return menu;
            }
        }

        plugin.getLogger().info("No se encontró menú para " + player.getName());
        return null;
    }



    public List<TradeMenu> getMenus() {
        return menus;
    }

    public PandoSabor getPlugin() {
        return plugin;
    }
}
