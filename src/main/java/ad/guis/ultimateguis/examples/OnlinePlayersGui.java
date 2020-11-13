package ad.guis.ultimateguis.examples;

import ad.guis.ultimateguis.engine.basics.BasicGui;
import ad.guis.ultimateguis.engine.interfaces.OfflineAction;
import org.bukkit.OfflinePlayer;

import java.util.Collection;

public class OnlinePlayersGui extends PlayersGui {
    public OnlinePlayersGui(OfflineAction action, Collection<? extends OfflinePlayer> playerList, BasicGui previousGui) {
        super(action, playerList, previousGui);
    }
}
