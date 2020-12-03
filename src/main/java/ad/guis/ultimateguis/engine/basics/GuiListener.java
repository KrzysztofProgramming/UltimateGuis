package ad.guis.ultimateguis.engine.basics;

import ad.guis.ultimateguis.UltimateGuis;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
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
    private final Set<BasicGui> activeGuis = new HashSet<>();
    private static final int clickCooldown = 100; //in millis;
    private boolean locked = false;

    public void init() {
        this.plugin = UltimateGuis.getInstance();
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }


    public synchronized void addGui(BasicGui gui) {
        if (locked)
            Bukkit.getScheduler().scheduleSyncDelayedTask(UltimateGuis.getInstance(), () -> activeGuis.add(gui));
        else activeGuis.add(gui);
    }

    public void disable() {
        activeGuis.forEach(gui -> gui.getGui().getViewers().forEach(HumanEntity::closeInventory));
    }

    public synchronized void removeGui(BasicGui gui) {
        if (locked)
            Bukkit.getScheduler().scheduleSyncDelayedTask(UltimateGuis.getInstance(), () -> activeGuis.remove(gui));
        else activeGuis.remove(gui);
    }

//    public synchronized void setClickCooldown(int cooldown){
//        clickCooldown = cooldown;
//    }

    public static int getClickCooldown() {
        return clickCooldown;
    }

    @EventHandler
    void InventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player) || e.getRawSlot() < 0) return;
        this.lock();
        List<BasicGui> filteredGuis = activeGuis.stream().filter(gui ->
            e.getInventory().equals(gui.getGui()))
                .collect(Collectors.toList());
        this.unlock();

        if(!filteredGuis.isEmpty()) e.setCancelled(true);
        filteredGuis.stream().filter(gui -> gui.getLastClick() + clickCooldown < System.currentTimeMillis())
                .forEach(
                        gui -> {
                            gui.lock();
                            gui.getActions().entrySet().stream()
                                    .filter(intActionEntry -> (intActionEntry.getKey() == e.getRawSlot() && intActionEntry.getValue() != null))
                                    .forEach(intActionEntry -> intActionEntry.getValue().action((Player) e.getWhoClicked()));
                            gui.unlock();
                            gui.setLastClick(System.currentTimeMillis());
                        });
    }

    private void lock() {
        this.locked = true;
    }

    private void unlock() {
        this.locked = false;
    }

    @EventHandler
    void guiClearer(InventoryCloseEvent e) {
        this.lock();
        activeGuis.removeIf(gui -> {
            boolean equals = gui.getGui().equals(e.getInventory());
            if (equals) {
                gui.onClose();
                gui.setClosed();
            }
            return equals;
        });
        this.unlock();
    }

}
