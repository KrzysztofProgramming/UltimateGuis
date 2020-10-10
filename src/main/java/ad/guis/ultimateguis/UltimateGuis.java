package ad.guis.ultimateguis;

import ad.guis.ultimateguis.engine.GuiListener;
import ad.guis.ultimateguis.engine.interfaces.DateAction;
import ad.guis.ultimateguis.examples.CalendarGui;
import com.google.common.cache.CacheLoader;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.SimpleDateFormat;
import java.util.*;

public final class UltimateGuis extends JavaPlugin {
    private static UltimateGuis instance;
    private GuiListener guiListener = new GuiListener();

    public GuiListener getGuiListener() {
        return guiListener;
    }

    public static UltimateGuis getInstance(){
        if(instance==null){
            System.out.println("New instance created");
            instance = new UltimateGuis();
        }
        return instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(label.equalsIgnoreCase("test")){
            if(sender instanceof Player){
                Player player = (Player) sender;
                GregorianCalendar calendar = new GregorianCalendar(2020, Calendar.NOVEMBER, 5);
                CalendarGui calendarGui = new CalendarGui();
                calendarGui.setDateAction(date -> {
                    Bukkit.broadcastMessage(new SimpleDateFormat("dd.MM.yyyy").format(date));
                });
                calendarGui.open(player);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onEnable() {
        instance = this;
        // Plugin startup logic
        guiListener.init();
        this.getCommand("test").setExecutor(this);

    }

    @Override
    public void onDisable() {
        instance = null;
        // Plugin shutdown logic
    }

    public static OfflinePlayer getOfflinePlayer(String name) {
        OfflinePlayer[] offlinePlayers = Bukkit.getServer().getOfflinePlayers();
        for (OfflinePlayer offlinePlayer : offlinePlayers) {
            if (offlinePlayer.getName().equalsIgnoreCase(name)) {
                return offlinePlayer;
            }
        }
        return null;
    }
    public static List<OfflinePlayer> getPlayers(List<UUID> players) {
        List<OfflinePlayer> list = new ArrayList<>();
        OfflinePlayer p;
        for (UUID uuid : players) {
            p = Bukkit.getOfflinePlayer(uuid);
            if (p != null) {
                list.add(p);
            }
        }
        return list;
    }

    public static OfflinePlayer getOfflinePlayer(UUID playerUUID) {
        OfflinePlayer[] offlinePlayers = Bukkit.getServer().getOfflinePlayers();
        for (OfflinePlayer offlinePlayer : offlinePlayers) {
            if (offlinePlayer.getUniqueId().equals(playerUUID)) {
                return offlinePlayer;
            }
        }
        return null;
    }
}
