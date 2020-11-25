package ad.guis.ultimateguis;

import ad.guis.ultimateguis.engine.GuiListener;
import ad.guis.ultimateguis.engine.basics.BasicGui;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
            if(sender instanceof Player) {
                Player player = (Player) sender;
                player.getInventory().addItem(BasicGui.createItem(Material.NETHER_STAR, "elo",
                        BasicGui.splitLoreWithConversion(
                                "&c&lWrong format: &f&l<playerName> <yyyy/MM/dd HH:mm:ss> " +
                                "<yyyy/MM/dd HH:mm:ss>", 20)));
            }
        return true;
    }
    Vector setModule(Vector v, double value){
        double m = value / v.length();
        return new Vector(v.getX() * m, v.getY() * m, v.getZ() * m);
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
        this.guiListener.disable();
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
        return Bukkit.getOfflinePlayer(playerUUID);
    }
}
