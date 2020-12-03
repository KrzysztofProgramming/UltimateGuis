package ad.guis.ultimateguis.examples;

import ad.guis.ultimateguis.engine.basics.BasicGui;
import ad.guis.ultimateguis.engine.basics.SwitchGui;
import ad.guis.ultimateguis.engine.interfaces.BasicAction;
import ad.guis.ultimateguis.engine.interfaces.ListableGui;
import ad.guis.ultimateguis.engine.interfaces.RefreshFunction;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;

@Deprecated
public abstract class ListSwitchGui<T> extends SwitchGui implements ListableGui<T> {

    protected Collection<? extends T> collection = new ArrayList<>();
    protected BasicAction<T> action;
    protected RefreshFunction<T> refreshFunction;
    protected String title = null;
    private Player lastClicker;

    public ListSwitchGui() {
    }

    public ListSwitchGui(BasicGui previousGui) {
        this(null, null, previousGui, null);
    }

    public ListSwitchGui(BasicGui previousGui, String title) {
        this(null, null, previousGui, title);
    }

    public ListSwitchGui(BasicAction<T> action, RefreshFunction<T> refreshFunction, BasicGui previousGui, String title) {
        this.title = title;
        this.action = action;
        this.previousGui = previousGui;
        this.refreshFunction = refreshFunction;
        init();
    }

    public ListSwitchGui(BasicAction<T> action, RefreshFunction<T> playerList) {
        this(action, playerList, null, null);
    }

    /**
     * @param player nick gracza, którego chcemy głowę
     * @return głowę wybranego gracza
     */
    static public ItemStack getPlayerHead(OfflinePlayer player) {

        return BasicGui.createItem(Material.SKULL_ITEM, player.getName(), (short) 3);
       /* boolean isInVersion = Arrays.stream(Material.values())
                .map(Material::name).collect(Collectors.toList()).contains("PLAYER_HEAD");

        Material type = Material.matchMaterial(isInVersion ? "PLAYER_HEAD" : "SKULL_ITEM");
        ItemStack item = new ItemStack(type, 1);

        if (!isInVersion) {
            item.setDurability((short) 3);
        }

        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwningPlayer(player);

        item.setItemMeta(meta);
        return item;*/
    }

    public Player getLastClicker() {
        return lastClicker;
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
    public RefreshFunction<T> getRefreshFunction() {
        return refreshFunction;
    }

    @Override
    public void setRefreshFunction(RefreshFunction<T> refreshFunction) {
        this.refreshFunction = refreshFunction;
    }

    public Collection<? extends T> getCollection() {
        return this.collection;
    }

    public abstract ItemStack getDescriptionItem(T element);

    protected void init() {
        if (this.collection == null) {
            this.collection = new ArrayList<>();
        }
        super.initPages(calcPageCount(collection.size()), this.title);
        ItemStack item;

        int counter = 0;
        int currentPage = 0;
        for (T element : collection) {
            if (counter >= 45) {
                currentPage++;
                counter = 0;
            }
            item = getDescriptionItem(element);

            this.guis.get(currentPage).addItem(item, playerWhoClicked -> {
                this.lastClicker = playerWhoClicked;
                if (element != null && action != null) {
                    action.action(element);
                }
            });
            counter++;
        }
    }

    @Override
    protected void guiAfterOpen(int pageNumber, Player opener) {
        super.guiAfterOpen(pageNumber, opener);
        if (refreshFunction == null) return;
        this.collection = refreshFunction.getList();
        init();
        this.open(opener);
    }
}
