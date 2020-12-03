package ad.guis.ultimateguis.engine.interfaces;

import org.bukkit.inventory.ItemStack;

public interface ListableGui<T> {
    RefreshFunction<T> getRefreshFunction();

    void setRefreshFunction(RefreshFunction<T> refreshFunction);

    BasicAction<T> getAction();

    void setAction(BasicAction<T> action);

    ItemStack getDescriptionItem(T element);
}
