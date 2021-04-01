package ad.guis.ultimateguis.engine.basics;

import ad.guis.ultimateguis.engine.interfaces.Action;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ModifiableGui extends BasicGui {

    @Setter
    @Getter
    protected int firstUnmodifiableSlot = 0;

    public ModifiableGui(int rowsAmount, String title, BasicGui previousGui) throws IllegalArgumentException {
        super(rowsAmount, title, previousGui);
    }

    @Override
    protected boolean advancedClickHandler(InventoryClickEvent e, Action defaultAction) {
        if(firstUnmodifiableSlot <= 0) return true;

        if(e.getRawSlot() < firstUnmodifiableSlot ||
            e.getRawSlot() >= this.getGui().getSize()){
            e.setCancelled(false);
            return false;
        }
        return true;
    }
}
