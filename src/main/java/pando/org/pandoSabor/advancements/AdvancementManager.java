package pando.org.pandoSabor.advancements;

import com.fren_gor.ultimateAdvancementAPI.AdvancementTab;
import com.fren_gor.ultimateAdvancementAPI.UltimateAdvancementAPI;
import com.fren_gor.ultimateAdvancementAPI.advancement.BaseAdvancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.RootAdvancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.FancyAdvancementDisplay;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementFrameType;
import org.bukkit.Material;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.entity.Player;
import pando.org.pandoSabor.PandoSabor;
import pando.org.pandoSabor.playerData.SaborPlayer;

public class AdvancementManager {

    private final PandoSabor plugin;

    public AdvancementManager(PandoSabor plugin) {
        this.plugin = plugin;
    }

    public void createPlayerTab(Player target) {
        String namespace = "jugador_" + target.getName().toLowerCase();
        UltimateAdvancementAPI api = UltimateAdvancementAPI.getInstance(plugin);

        if (api.getAdvancementTab(namespace) != null) return; // Ya existe

        AdvancementTab tab = api.createAdvancementTab(namespace);

        registerAdvancements(target, tab);
    }

    private void registerAdvancements(Player target, AdvancementTab tab) {
        // ROOT: "Conocer a Jugador"
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        if (meta != null) {
            meta.setOwningPlayer(target); // Esto pone la skin del jugador
            skull.setItemMeta(meta);
        }

        RootAdvancement root = new RootAdvancement(
                tab,
                "root",
                new FancyAdvancementDisplay(
                        skull, // Usamos la cabeza personalizada
                        "Conocer a " + target.getName(),
                        AdvancementFrameType.TASK,
                        true,
                        true,
                        0f,
                        0f,
                        "",
                        "Encuentra a " + target.getName()
                ),
                "textures/block/bamboo_block.png",
                1
        );


        BaseAdvancement matar = new BaseAdvancement(
                "matar_" + target.getName().toLowerCase(),
                new FancyAdvancementDisplay(
                        Material.NETHERITE_SWORD,
                        "Derrotar a " + target.getName(),
                        AdvancementFrameType.CHALLENGE,
                        true,
                        true,
                        0f,
                        1f,
                        "",
                        "Mata a " + target.getName()
                ),
                root
        );

        registerListeners(target, tab, matar, root);

        tab.registerAdvancements(root, matar);

        root.grant(target);
    }

    private void registerListeners(Player target, AdvancementTab tab, BaseAdvancement matar, RootAdvancement root) {
        tab.registerEvent(PlayerDeathEvent.class, event -> {
            Player player = event.getEntity(); // El que murió
            Player killer = player.getKiller(); // El que lo mató

            if (killer != null && player.equals(target)) {
                if (!matar.isGranted(killer)) {
                    matar.grant(killer);
                    SaborPlayer saborPlayer = plugin.getSaborManager().getPlayer(killer.getUniqueId());
                    saborPlayer.addKilledPlayer(player.getUniqueId());
                    plugin.getSaborPlayerStorage().save(saborPlayer);
                }
            }
        });



        tab.registerEvent(org.bukkit.event.player.PlayerMoveEvent.class, event -> {
            Player mover = event.getPlayer();

            // Evita que el target se auto-conceda su propio logro
            if (mover.equals(target)) return;

            // Asegúrate de que ambos estén en el mismo mundo
            if (!mover.getWorld().equals(target.getWorld())) return;

            // Comprueba la distancia
            if (mover.getLocation().distance(target.getLocation()) <= 5) {
                if(!root.isGranted(mover)){
                    SaborPlayer saborPlayer =  plugin.getSaborManager().getPlayer(mover.getUniqueId());
                    saborPlayer.addUnlockedPlayers(target.getUniqueId());
                    plugin.getSaborPlayerStorage().save(saborPlayer);
                    root.grant(mover);
                }
            }
        });
    }
}
