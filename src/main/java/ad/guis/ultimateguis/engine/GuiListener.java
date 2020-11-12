package ad.guis.ultimateguis.engine;

import ad.guis.ultimateguis.UltimateGuis;
import ad.guis.ultimateguis.engine.basics.BasicGui;
import ad.guis.ultimateguis.engine.interfaces.Action;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

public class GuiListener implements Listener {
    private UltimateGuis plugin;
    private Set<BasicGui> activeGuis = new HashSet<>();

    public void init(){
        this.plugin = UltimateGuis.getInstance();
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    /**
     * dodaje gui do tego listenera
     *
     * @param gui
     * @return
     */
    public boolean addGui(BasicGui gui) {
          return activeGuis.add(gui);
    }

    /**
     * zamyka przy reloadzie wszystkie gui
     */
    public void disable() {
        activeGuis.forEach(gui -> gui.getLastViewer().closeInventory());
    }

    /**
     * usuwa gui z TYLKO TEGO listenera
     *
     * @param gui
     */
    public void removeGui(BasicGui gui) {
        activeGuis.remove(gui);
    }

    /**
     * obsługuje kliknięcia gui
     *
     * @param e
     */
    @EventHandler
    void InventoryClick(InventoryClickEvent e) {
        for (BasicGui gui : activeGuis) {
            if (e.getInventory().equals(gui.getGui())) {
                if (e.getWhoClicked() instanceof Player) {
                    Player player = (Player) e.getWhoClicked();
                    int slot = e.getRawSlot();
                    if (slot == -1)
                        return;
                    for (HashMap.Entry<Integer, Action> item : gui.getActions().entrySet()) {
                        if (item.getKey() == slot) {
                            if (item.getValue() != null) item.getValue().action(player);
                            break;
                        }
                    }
                }
            e.setCancelled(true);
            break;
            }
        }
    }

    /**
     * czyści listenery po zamknięciu gui
     *
     * @param e
     */
    @EventHandler
    void guiClearer(InventoryCloseEvent e) {
        for (BasicGui activeGui : activeGuis) {
            if (e.getInventory().equals(activeGui.getGui())) {
                activeGui.removeFromListeners();
                break;
            }
        }
    }

}
