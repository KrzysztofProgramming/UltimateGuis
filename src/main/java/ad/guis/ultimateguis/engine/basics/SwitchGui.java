package ad.guis.ultimateguis.engine.basics;

import ad.guis.ultimateguis.Colors;
import ad.guis.ultimateguis.UltimateGuis;
import ad.guis.ultimateguis.engine.interfaces.Action;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * pozwala stworzyć gui z przełączaniem stron
 */
public class SwitchGui {
    protected final UltimateGuis plugin;
    protected List<SwitchGuiElement> guis = new ArrayList<>();
    protected BasicGui previousGui = null;
    private int lastOpenedPage = 0;
    private int lastClosedPage = 0;
    private boolean pageChanging = false;
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
        if (position >= 3) position++;
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
                    changePage(I + 1, player);
                }, true);
            else if(pagesAmount > 1){
                gui.setItem(8, 5, next, player -> {
                    changePage(0, player);
                }, true);
            }
            else{
                gui.setItem(8,5, fill, null);
            }
            if(i - 1 >= 0)
                gui.setItem(0, 5, previous, player -> {
                    changePage(I - 1, player);
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
                changePage(pagesAmount - 1, player);
            }, true);

        ItemStack back = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = back.getItemMeta();
        meta.setDisplayName(ChatColor.BOLD + "Back");
        back.setItemMeta(meta);

        this.addActionItem(back, player -> {
            if (previousGui != null) previousGui.open(player);
            else player.closeInventory();
        },3,true);
    }

    protected void changePage(int number, Player player){
        pageChanging = true;
        guis.get(number).open(player);
    }

    public int getLastOpenedPage() {
        return lastOpenedPage;
    }

    public int getLastClosedPage() {
        return lastClosedPage;
    }

    /**
     * @return zwraca ilość stron, tak jak w tablicach
     */
    public int getSize(){
        return guis.size();
    }

    /**
     *
     * @return prawda, jesli jakiekolwiek ze stron jest otwarta przez kogoś
     */
    public boolean isOpen(){
        boolean open = false;
        for(int i=0; i<guis.size() && !open; i++){
            open = open || guis.get(i).isOpen();
        }
        return open;
    }
    /**
     * Pozwala na dostęp do poszczególnych stron
     * @param page numer strony do zwrócenia
     * @return gui o numerze page jeśli takie istnieje, w przeciwnym wypadku zwraca null
     */
    public BasicGui getGui(int page){
        if(page < 0 || page >= guis.size()){
            return null;
        }
        return guis.get(page);
    }

    public boolean isPageChanging(){
        return pageChanging;
    }

    /**
     * otwiera wybraną stronę
     * @param page numer strony
     * @param opener gracz otwierający gui
     * @return prawda jeśli jest taka strona
     */
    public boolean open(int page, Player opener){
        if(page < guis.size()){
            guis.get(page).open(opener);
            return true;
        }
        return false;
    }

    /**
     * Otwiera pierwszą stronę gui
     * @param opener gracz, ktory otwiera gui
     * @return fałsz jeśli gui jest puste
     */
    public boolean open(Player opener){
        if(!guis.isEmpty()){
            guis.get(0).open(opener);
            return true;
        }
        return false;
    }

    public BasicGui getPreviousGui() {
        return previousGui;
    }

    public void setPreviousGui(BasicGui previousGui) {
        this.previousGui = previousGui;
        for (SwitchGuiElement gui : guis) {
            gui.setPreviousGui(previousGui);
        }
    }

    /**
     * called before page open
     */
    protected boolean pageOnOpen(int pageNumber, Player opener) {
        if(!pageChanging)
            return guiOnOpen(pageNumber, opener);
        return true;
    }

    /**
     * called before page close
     */
    protected boolean pageOnClose(int pageNumber){
        if(!pageChanging)
           return guiOnClose(pageNumber);
        return true;
    }

    protected boolean guiOnClose(int pageNumber){return true;}
    protected boolean guiOnOpen(int pageNumber, Player opener){return true;}

    protected void pageAfterOpen(int pageNumber, Player opener){
        lastOpenedPage = pageNumber;
        if(pageChanging){
            pageChanging = false;
        }
        else{
            guiAfterOpen(pageNumber, opener);
        };
    }

    protected void pageAfterClose(int pageNumber){
        if(!pageChanging) guiAfterClose(pageNumber);
        lastClosedPage = pageNumber;
    }

    protected void guiAfterClose(int pageNumber){}
    protected void guiAfterOpen(int pageNumber, Player opener){}

    protected void switchToLastClosedPage(Player player){

        if(this.getLastClosedPage() >= this.getSize()) {
            changePage(this.getSize() - 1, player);
        }
        else {
            changePage(this.getLastClosedPage(), player);
        }
    }

    protected void switchToLastOpenedPage(Player player){
        if(this.getLastOpenedPage() >= this.getSize()) {
            changePage(this.getSize() - 1, player);
        }
        else {
            changePage(this.getLastOpenedPage(), player);
        }
    }

    public static int calcPageCount(int itemsCount){
        return (itemsCount - 1) / 45 + 1;
    }
}
