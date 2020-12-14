package ad.guis.ultimateguis.engine.basics;

import ad.guis.ultimateguis.Colors;
import ad.guis.ultimateguis.engine.Pair;
import ad.guis.ultimateguis.engine.interfaces.Action;
import ad.guis.ultimateguis.engine.interfaces.BasicAction;
import ad.guis.ultimateguis.engine.interfaces.ListableGui;
import ad.guis.ultimateguis.engine.interfaces.RefreshFunction;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ListGui<T> extends BasicGui implements ListableGui<T> {

    public final static int CAPACITY = 45;
    private static final ItemStack previousItem;
    private static final ItemStack nextItem;
    private static final ItemStack backItem;
    private static final ItemStack backgroundItem;

    static {
        previousItem = BasicGui.createItem(Material.ARROW, ChatColor.BOLD + "Previous page");
        nextItem = BasicGui.createItem(Material.ARROW, ChatColor.BOLD + "Next page");
        backItem = BasicGui.createItem(Material.WOOD_DOOR, ChatColor.RED + "" + ChatColor.BOLD + "Back");
        backgroundItem = BasicGui.createBackground(Colors.GRAY);
    }

    private final Map<Integer, Pair<ItemStack, Action>> specialItems = new HashMap<>();
    protected String title;
    protected List<? extends T> list = new ArrayList<>();
    protected RefreshFunction<T> refreshFunction;
    protected BasicAction<T> action;
    protected Player lastClicker;
    private int pageNumber = 0;
    private int pageCount = 1;

    public ListGui(BasicGui previousGui, String title) {
        this(null, null, previousGui, title);
    }

    public ListGui(BasicAction<T> action, RefreshFunction<T> refreshFunction, BasicGui previousGui, String title) {
        this.action = action;
        this.refreshFunction = refreshFunction;
        this.previousGui = previousGui;
        this.title = title;

        this.setActionItem(backItem, this::backOrClose, 3);
    }

    private void init() {
        if (title == null) title = "";
        this.gui = BasicGui.createFullInventory(title + " [" + (pageNumber + 1) + '/' + pageCount + ']');
        int counter = 0;
        this.actions.clear();

        for (int i = pageNumber * CAPACITY; i < (pageNumber + 1) * CAPACITY; i++) {
            if (this.list.size() <= i) break;
            T element = this.list.get(i);
            ItemStack descriptionItem = getDescriptionItem(element);
            this.setItem(counter, descriptionItem, playerWhoClick -> {
                lastClicker = playerWhoClick;
                ListGui.this.action.action(element);
            });
            counter++;
        }

        initSwitchItems();
        initActionItems();
        bottomBackground();
    }

    private void initSwitchItems() {
        if (pageCount > 1) {
            this.setItem(0, 5, previousItem, playerWhoClick -> {
                previousPage();
                init();
                playerWhoClick.openInventory(this.gui);
            });
            this.setItem(8, 5, nextItem, playerWhoClick -> {
                nextPage();
                init();
                playerWhoClick.openInventory(this.gui);
            });
        }
    }

    private void nextPage() {
        if (pageCount == 1) return;
        if (pageNumber >= pageCount - 1) pageNumber = 0;
        else pageNumber++;
    }

    private void previousPage() {
        if (pageCount == 1) return;
        if (pageNumber <= 0) pageNumber = pageCount - 1;
        else pageNumber--;
    }

    private void bottomBackground() {
        for (int i = 45; i < 54; i++) {
            if (this.gui.getItem(i) != null) continue;
            this.setItem(i, backgroundItem, null);
        }
    }

    protected boolean setActionItem(ItemStack item, Action action, int position) {
        if (position < 0 || position > 6) return false;
        this.specialItems.put(position, new Pair<>(item, action));
        return true;
    }

    private void initActionItems() {
        specialItems.forEach((key, value) -> this.setItem(key + 1, 5,
                value.getFirsValue(), value.getSecondValue()));
    }

    public List<? extends T> getList() {
        return list;
    }

    public Player getLastClicker() {
        return lastClicker;
    }

    @Override
    public RefreshFunction<T> getRefreshFunction() {
        return refreshFunction;
    }

    @Override
    public void setRefreshFunction(RefreshFunction<T> refreshFunction) {
        this.refreshFunction = refreshFunction;
    }

    @Override
    public BasicAction<T> getAction() {
        return action;
    }

    @Override
    public void setAction(BasicAction<T> action) {
        this.action = action;
    }

    @Override
    public void open(Player opener) {
        if (refreshFunction != null) {
            List<? extends T> copyList = this.refreshFunction.getList();
            if (copyList == null) this.list = new ArrayList<>();
            else this.list = new ArrayList<>(copyList);
        }
        calcPageCount();
        init();
        super.open(opener);
    }

    @Override
    public void onClose() {
        this.list.clear();
        super.onClose();
    }

    private void calcPageCount() {
        this.pageCount = Math.max((this.list.size() - 1) / CAPACITY + 1, 1);
        if (pageNumber >= pageCount) pageNumber = pageCount - 1;
    }
}
