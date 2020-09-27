package ad.guis.ultimateguis.engine.interfaces;

import org.bukkit.OfflinePlayer;

/**
 * pozwala na przesyłanie akcji dotyczących graczy offline
 */
public interface OfflineAction {
    void action(OfflinePlayer offlinePlayer);
}
