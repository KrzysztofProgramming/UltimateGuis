package ad.guis.ultimateguis.engine.basics;

import ad.guis.ultimateguis.engine.interfaces.Action;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SwitchGuiElement extends BasicGui {
    private final SwitchGui parentGui;
    int pageNumber;

    SwitchGuiElement(int rowsAmout, String title, SwitchGui parentGui, int pageNumber, BasicGui previousGui) throws IllegalArgumentException {
        super(rowsAmout, title, previousGui);
        this.pageNumber = pageNumber;
        this.parentGui = parentGui;
    }

    /**
     *
     * @return referencję do SwitchGui, do którego należy element
     */
    public SwitchGui getParent(){
        return parentGui;
    }

    @Override
    public boolean addItem(ItemStack item, Action action) {
        return super.addItem(item, action);
    }


    @Override
    public boolean setItem(int positionX, int positionY, ItemStack item, Action action) {
        if(positionY >= 5) return false;
        return super.setItem(positionX, positionY, item, action);
    }

    /**
     *
     * @param positionX
     * @param positionY
     * @param item
     * @param action
     * @param force jeśli true można edytować najniższą linię
     * @return
     */
    boolean setItem(int positionX, int positionY, ItemStack item, Action action, boolean force){
        if(force) return super.setItem(positionX, positionY, item, action);
        return this.setItem(positionX, positionY, item, action);
    }

    /**
     * @return numer strony, który posiada to Gui w SwitchGui
     */
    public int getPageNumber() {
        return this.pageNumber;
    }

    @Override
    public void open(Player opener) {
        parentGui.pageOnOpen(this.pageNumber, opener);
        super.open(opener);
        parentGui.pageAfterOpen(this.pageNumber, opener);
    }

    @Override
    public void removeFromListeners() {
        parentGui.pageOnClose(this.pageNumber);
        super.removeFromListeners();
        parentGui.pageAfterClose(this.pageNumber);
    }

}
