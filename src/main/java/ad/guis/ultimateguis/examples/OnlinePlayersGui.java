package ad.guis.ultimateguis.examples;

import ad.guis.ultimateguis.engine.basics.BasicGui;
import ad.guis.ultimateguis.engine.interfaces.OfflineAction;
import org.bukkit.Bukkit;

public class OnlinePlayersGui extends PlayersGui {

    public OnlinePlayersGui(BasicGui previousGui) {
        this(previousGui, null);
    }

    public OnlinePlayersGui(BasicGui previousGui, String title) {
        this(null, previousGui, title);
    }

    public OnlinePlayersGui(OfflineAction action, BasicGui previousGui, String title) {
        super(action, null, previousGui, title);
        this.setRefreshFunction(Bukkit::getOnlinePlayers);
    }
}
