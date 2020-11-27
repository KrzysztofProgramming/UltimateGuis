package ad.guis.ultimateguis.examples.calendargui;

import org.bukkit.entity.Player;

import java.time.LocalDate;
import java.util.Date;

public interface CalendarGuiAction {
    void action(LocalDate date, Player player, CalendarGui gui);
}
