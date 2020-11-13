package ad.guis.ultimateguis.engine.basics;

import ad.guis.ultimateguis.Colors;
import ad.guis.ultimateguis.UltimateGuis;
import ad.guis.ultimateguis.engine.interfaces.Action;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * pozwala stworzyć gui z przełączaniem stron
 */
public class SwitchGui {
    public final static int PAGE_CAPACITY = 45;
    protected final UltimateGuis plugin;
    protected List<SwitchGuiElement> guis = new ArrayList<>();
    protected BasicGui previousGui = null;
    int lastOpenedPage = 0;
    int lastClosedPage = 0;
    boolean isOpen = false;
    /**
     * Tworzy Switch Gui
     *
     * @param pageAmount liczba stron, jakie ma mieć gui
     */
    public SwitchGui(int pageAmount, String title, BasicGui previousGui) {
        this.plugin = UltimateGuis.getInstance();
        this.previousGui = previousGui;
        initPages(pageAmount, title);
    }

    public SwitchGui(int pageAmount, String titles) {
        this(pageAmount, titles, null);
    }

    protected SwitchGui(){
        this.plugin = UltimateGuis.getInstance();
    }

    protected boolean addActionItem(ItemStack item, Action action, int position, boolean force){
        if(!force) this.addActionItem(item, action, position);
        if(position < 0 || position >6) return false;
        for(SwitchGuiElement gui: guis){
            gui.setItem(position + 1,5,item, action, true);
        }
        return true;
    }

    /**
     * dodaje item z akcją do paska na dole strony
     * @param item item do dodania
     * @param action akcja po kliknięciu
     * @param position od 0 do 5;
     * @return
     */

    public boolean addActionItem(ItemStack item, Action action, int position) {
        if (position == 3) return false;
        return this.addActionItem(item, action, position, true);
    }

    protected void initPages(int pagesAmount, String title) {
        guis.clear();
        if(title==null) title = "";
        ItemStack next = BasicGui.createItem(Material.ARROW, "Next page");
        ItemStack previous = BasicGui.createItem(Material.ARROW, "Previous page");
        ItemStack fill = BasicGui.createBackground(Colors.GRAY);

        for (int i = 0; i < pagesAmount; i++) {
            guis.add(new SwitchGuiElement(6,title + " [" + (i+1) + "/" + pagesAmount + "]" , this, i, previousGui));
            guis.get(i).setPreviousGui(this.previousGui);


            SwitchGuiElement gui = guis.get(i);

            int I = i;
            if(i + 1 < pagesAmount )
                gui.setItem(8, 5, next, player -> {
                    open(I + 1, player);
                }, true);
            else if(pagesAmount > 1){
                gui.setItem(8, 5, next, player -> {
                    open(0, player);
                }, true);
            }
            else {
                gui.setItem(8,5, fill, null, true);
            }
            if(i - 1 >= 0)
                gui.setItem(0, 5, previous, player -> {
                    open(I - 1, player);
                    }, true);
            else{
                gui.setItem(0, 5, fill, null, true);
            }

            for(int j=1; j<8; j++){
                gui.setItem(j, 5, fill, null, true);
            }
        }
        if(pagesAmount > 1)
            guis.get(0).setItem(0, 5, previous, player -> {
                open(pagesAmount - 1, player);
            }, true);

        ItemStack back = BasicGui.createItem(Material.NETHER_STAR, ChatColor.BOLD + "Back");

        this.addActionItem(back, player -> {
            if (previousGui != null) previousGui.open(player);
            else player.closeInventory();
        },3,true);
    }



    public int getLastOpenedPage() {
        return lastOpenedPage;
    }

    public int getLastClosedPage() {
        return lastClosedPage;
    }

    public int getSize(){
        return guis.size();
    }

    public boolean isOpen(){
        return isOpen;
    }

    public SwitchGuiElement getGui(int page){
        if(guis.isEmpty()) throw new IndexOutOfBoundsException("SwitchGui is empty");
        if(page < 0) return this.guis.get(0);
        if(page > this.guis.size()) return this.guis.get(this.guis.size() - 1);
        return this.guis.get(page);
    }

    public boolean open(Player opener){
        return this.open(this.getLastClosedPage(), opener);
    }

    public SwitchGuiElement getRecentPage(){
        return this.getGui(this.getLastClosedPage());
    }

    public boolean open(int pageNumber, Player opener){
        if(!guis.isEmpty()){
            this.getGui(pageNumber).open(opener);
            return true;
        }
        return false;
    }

    public BasicGui getPreviousGui() {
        return previousGui;
    }

    public void setPreviousGui(BasicGui previousGui) {
        this.previousGui = previousGui;
        guis.forEach(gui -> gui.setPreviousGui(previousGui));
    }

    /**
     * can be override in subcalsses, calling open in this function cause infinity recursion
     */
    protected void pageAfterChange(int previousPage, int newPage, Player opener) {}

    protected void guiAfterClose(int previousPage){}
    protected void guiAfterOpen(int pageNumber, Player opener){}

    public static int calcPageCount(int itemsCount){
        return (itemsCount - 1) / PAGE_CAPACITY + 1;
    }
}
