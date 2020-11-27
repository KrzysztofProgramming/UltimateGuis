package ad.guis.ultimateguis.engine;

import ad.guis.ultimateguis.UltimateGuis;
import ad.guis.ultimateguis.engine.basics.BasicGui;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GuiListener implements Listener {
    private UltimateGuis plugin;
    private Set<BasicGui> activeGuis = new HashSet<>();
    private int clickCooldown = 100; //in millis;

    public void init(){
        this.plugin = UltimateGuis.getInstance();
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }


    public synchronized boolean addGui(BasicGui gui) {
          return activeGuis.add(gui);
    }

    public void disable() {
        activeGuis.forEach(gui -> gui.getGui().getViewers().forEach(HumanEntity::closeInventory));
    }

    public synchronized void removeGui(BasicGui gui) {
        activeGuis.remove(gui);
    }

    public synchronized void setClickCooldown(int cooldown){
        clickCooldown = cooldown;
    }

    public int getClickCooldown(){
        return clickCooldown;
    }

    @EventHandler
    void InventoryClick(InventoryClickEvent e) {
        if(!(e.getWhoClicked() instanceof Player) || e.getRawSlot() < 0) return;

        List<BasicGui> filteredGuis = activeGuis.stream().filter(gui ->
            e.getInventory().equals(gui.getGui()))
                .collect(Collectors.toList());

        if(!filteredGuis.isEmpty()) e.setCancelled(true);
        filteredGuis.stream().filter(gui -> gui.getLastClick() + clickCooldown < System.currentTimeMillis())
                .forEach(
                gui ->{
                    gui.getActions().entrySet().stream()
                    .filter(intActionEntry -> (intActionEntry.getKey() == e.getRawSlot() && intActionEntry.getValue()!=null))
                    .forEach(intActionEntry ->Bukkit.getScheduler().scheduleSyncDelayedTask(UltimateGuis.getInstance(),
                            () -> intActionEntry.getValue().action((Player)e.getWhoClicked())));
                    gui.setLastClick(System.currentTimeMillis());
                });
    }


    @EventHandler
    void guiClearer(InventoryCloseEvent e) {
        activeGuis.removeIf(gui ->{
            gui.onClose();
            return gui.getGui().equals(e.getInventory());});
    }

}
