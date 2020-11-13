package ad.guis.ultimateguis.engine;

import ad.guis.ultimateguis.UltimateGuis;
import ad.guis.ultimateguis.engine.basics.BasicGui;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GuiListener implements Listener {
    private UltimateGuis plugin;
    private Set<BasicGui> activeGuis = new HashSet<>();

    public void init(){
        this.plugin = UltimateGuis.getInstance();
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }


    public synchronized boolean addGui(BasicGui gui) {
          return activeGuis.add(gui);
    }

    public void disable() {
        activeGuis.forEach(gui -> gui.getLastViewer().closeInventory());
    }

    public synchronized void removeGui(BasicGui gui) {
        activeGuis.remove(gui);
    }


    @EventHandler
    void InventoryClick(InventoryClickEvent e) {
        if(!(e.getWhoClicked() instanceof Player) || e.getRawSlot() < 0) return;

        List<BasicGui> filteredGuis = activeGuis.parallelStream().filter(gui -> e.getInventory().equals(gui.getGui()))
                .collect(Collectors.toList());
        if(!filteredGuis.isEmpty()) e.setCancelled(true);
        filteredGuis.forEach(
                gui -> gui.getActions().entrySet().stream()
                .filter(intActionEntry -> (intActionEntry.getKey() == e.getRawSlot() && intActionEntry.getValue()!=null))
                .forEach(intActionEntry -> intActionEntry.getValue().action((Player)e.getWhoClicked())));
    }


    @EventHandler
    void guiClearer(InventoryCloseEvent e) {
        activeGuis.removeIf(gui ->{
            gui.onClose();
            return gui.getGui().equals(e.getInventory());});
    }

}
