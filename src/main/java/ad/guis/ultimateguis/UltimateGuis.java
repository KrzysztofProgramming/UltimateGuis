package ad.guis.ultimateguis;

import ad.guis.ultimateguis.engine.basics.GuiListener;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class UltimateGuis extends JavaPlugin {
    private static UltimateGuis instance;
    private final GuiListener guiListener = new GuiListener();

    public GuiListener getGuiListener() {
        return guiListener;
    }

    public static UltimateGuis getInstance(){
        if(instance==null){
            System.out.println("New instance created");
            instance = new UltimateGuis();
        }
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        guiListener.init();

    }

    @Override
    public void onDisable() {
        this.guiListener.disable();
        instance = null;
    }

    public static OfflinePlayer getOfflinePlayer(String name) {
        OfflinePlayer[] offlinePlayers = Bukkit.getServer().getOfflinePlayers();
        for (OfflinePlayer offlinePlayer : offlinePlayers) {
            if (offlinePlayer.getName().equalsIgnoreCase(name)) {
                return offlinePlayer;
            }
        }
        return null;
    }
    public static List<OfflinePlayer> getPlayers(List<UUID> players) {
        List<OfflinePlayer> list = new ArrayList<>();
        OfflinePlayer p;
        for (UUID uuid : players) {
            p = Bukkit.getOfflinePlayer(uuid);
            if (p != null) {
                list.add(p);
            }
        }
        return list;
    }

    public static OfflinePlayer getOfflinePlayer(UUID playerUUID) {
        return Bukkit.getOfflinePlayer(playerUUID);
    }
}
