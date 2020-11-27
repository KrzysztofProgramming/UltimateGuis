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
        boolean wasOpen = this.parentGui.isOpen;
        this.parentGui.duringOpening = true;

        super.open(opener);
        this.parentGui.isOpen = true;
        this.parentGui.lastOpenedPage = this.pageNumber;
        this.parentGui.duringOpening = false;

        if (wasOpen)
            this.parentGui.pageAfterChange(this.parentGui.getLastClosedPage(), this.pageNumber, opener);
        else
            this.parentGui.guiAfterOpen(this.pageNumber, opener);
    }

    @Override
    public void onClose() {
        super.onClose();
        this.parentGui.isOpen = false;
        this.parentGui.lastClosedPage = this.pageNumber;
        if (!this.parentGui.duringOpening) this.parentGui.guiOnClose(this.pageNumber);
    }
}
