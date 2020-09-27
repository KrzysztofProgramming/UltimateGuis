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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GuiListener implements Listener {
    private UltimateGuis plugin;
    private List<BasicGui> activeGuis = new ArrayList<>();

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
        if (!activeGuis.contains(gui)) {
            activeGuis.add(gui);
            return true;
        }
        return false;
    }

    /**
     * zamyka przy reloadzie wszystkie gui
     */
    public void disable() {
        for (BasicGui gui : activeGuis) {
            gui.getLastViewer().closeInventory();
        }
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
        //  Bukkit.broadcastMessage("GuiListeners: " +this.activeGuis.size());
        for (BasicGui gui : activeGuis) {
            if (e.getInventory().equals(gui.getGui())) {
                if (e.getWhoClicked() instanceof Player) {
                    Player player = (Player) e.getWhoClicked();
                    ItemStack stack = e.getCurrentItem();
                    if (stack == null || stack.getItemMeta() == null)
                        return;
                    for (HashMap.Entry<ItemStack, Action> item : gui.getActions().entrySet()) {
                        if (item.getKey().equals(stack)) {
                            if (item.getValue() != null) item.getValue().action(player);
                            break;
                        } else if (item.getKey().getItemMeta() instanceof SkullMeta) {
                            if (item.getKey().getItemMeta().getDisplayName().equals(stack.getItemMeta().getDisplayName())) {
                                if (item.getValue() != null) item.getValue().action(player);
                                break;
                            }
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
