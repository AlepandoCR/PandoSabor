package pando.org.pandoSabor.discord.tab;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.world.level.GameType;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import pando.org.pandoSabor.PandoSabor;

import java.util.EnumSet;
import java.util.List;
import java.util.UUID;


public class FakeTabUtil {


    private static final List<ClientboundPlayerInfoUpdatePacket.Entry> fakePlayers = new java.util.concurrent.CopyOnWriteArrayList<>();

    public static void addFakePlayer(String username) {
        UUID uuid = UUID.nameUUIDFromBytes(("DISCORD_" + username).getBytes());
        ClientboundPlayerInfoUpdatePacket.Entry entry = getEntry(username, uuid);

        fakePlayers.add(entry);

        ClientboundPlayerInfoUpdatePacket packet = new ClientboundPlayerInfoUpdatePacket(
                EnumSet.of(
                        ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER,
                        ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME,
                        ClientboundPlayerInfoUpdatePacket.Action.UPDATE_GAME_MODE,
                        ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LISTED
                ),
                List.of(entry)
        );

        for (Player player : Bukkit.getOnlinePlayers()) {
            ((CraftPlayer) player).getHandle().connection.send(packet);
        }
    }

    public static void startTabUpdater(PandoSabor plugin) {
        new org.bukkit.scheduler.BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    var handle = ((CraftPlayer) player).getHandle();
                    for (ClientboundPlayerInfoUpdatePacket.Entry entry : fakePlayers) {
                        ClientboundPlayerInfoUpdatePacket packet = new ClientboundPlayerInfoUpdatePacket(
                                EnumSet.of(
                                        ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER,
                                        ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME,
                                        ClientboundPlayerInfoUpdatePacket.Action.UPDATE_GAME_MODE,
                                        ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LISTED
                                ),
                                List.of(entry)
                        );
                        handle.connection.send(packet);
                    }
                }
            }
        }.runTaskTimerAsynchronously(plugin, 20L * 5, 20L);
    }


    private static ClientboundPlayerInfoUpdatePacket.Entry getEntry(String username, UUID uuid) {
        GameProfile profile = new GameProfile(uuid, username);

        String value = "ewogICJ0aW1lc3RhbXAiIDogMTYyMTcyODgwOTkxMywKICAicHJvZmlsZUlkIiA6ICJlNzkzYjJjYTdhMmY0MTI2YTA5ODA5MmQ3Yzk5NDE3YiIsCiAgInByb2ZpbGVOYW1lIiA6ICJUaGVfSG9zdGVyX01hbiIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9kMzQwMTM3ZGNkNjc3MTI4ZThhZGE0MzQwNGUxNzIxOGY4MTBlOTU3NmMzYmY3Mjk0N2UzMTkxODI3MmU1MmIxIgogICAgfQogIH0KfQ==";
        String signature = "qBn2/G+CmQ47NvtL/S6lZLbUkbXiELA9jGiOOEB0gFTqRL5WIY6ILsfnY97pPTdk+HFB8A5OZqUV+iSBO0OVrBt7jD7IWtW9UbHVszCZ6l4ryuDhsfqib1y5uludHyIbprD1C5/T7agSgI3iPBPpUnI6Hmawe1E9ATA2usTMgwMzeawMu/v/xwozPZ/u2nBe24Ymet5NWY1dMTwy5zj73BLz73DkjBKNEweMQbdhiz8BxLw40wAh48nIYzAclHDX+nXaRs0qitPh2RmMQYFgOK7YEgVt5976m25WZlPe9nIos8RocwkJ5WXtAvnewu+k8zaNuzXk5XcYJWb2ftK5akhbcbQE4R8+9TmlGPdIyFfOy1gNaHitrbnm0+404HpbGJhaF0ziV4Via8N1C3MmnHZE23H/FTKqloleh7Jcr9bdHTFEjWLZzy2vIp5JupqtQ/SUDguW4pTgPPSQeAhJMbOChOiEgcIrgNUywyPSQ2jpg8PGMmmbEdsqtq12mCdjz4jAN+jUjOomr8NQrGdOoF7O/MS9QYCEkcp5vEYyXR4BwLTNJ2jBiy6rDs7mjPjzTNFhmOPdTrzfNOWsQ4Pz50FOhEESrMRyk7KKmJL3k4/4/LDjChyKqm6JH5gIF7fXoLlGKhPfwIeZXB6ywH6gnqx15Ac+cy/AzSqf6fho42A=";

        profile.getProperties().put("textures", new Property("textures", value, signature));

        Component displayName = Component.literal("§8[§bDiscord§8] §7" + username);

        return new ClientboundPlayerInfoUpdatePacket.Entry(
                uuid,
                profile,
                true,
                0,
                GameType.SPECTATOR,
                displayName,
                false,
                0,
                null
        );
    }

    public static void removeFakePlayer(String username) {
        UUID uuid = UUID.nameUUIDFromBytes(("DISCORD_" + username).getBytes());

        fakePlayers.removeIf(entry -> entry.profileId().equals(uuid));

        ClientboundPlayerInfoRemovePacket removePacket = new ClientboundPlayerInfoRemovePacket(List.of(uuid));

        for (Player online : Bukkit.getOnlinePlayers()) {
            ((CraftPlayer) online).getHandle().connection.send(removePacket);
        }
    }



}
