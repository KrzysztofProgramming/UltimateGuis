package ad.guis.ultimateguis.engine.interfaces;

import java.util.Collection;

public interface RefreshFunction<T> {
    Collection<? extends T> getCollection();
}
