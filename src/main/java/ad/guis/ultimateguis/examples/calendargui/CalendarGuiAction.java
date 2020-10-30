package ad.guis.ultimateguis.examples.calendargui;

import org.bukkit.entity.Player;

import java.util.Date;

public interface CalendarGuiAction {
    void action(Date date, Player player, CalendarGui gui);
}
