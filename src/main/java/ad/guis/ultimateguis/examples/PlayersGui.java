package ad.guis.ultimateguis.examples;

import ad.guis.ultimateguis.engine.basics.BasicGui;
import ad.guis.ultimateguis.engine.basics.ListGui;
import ad.guis.ultimateguis.engine.interfaces.OfflineAction;
import ad.guis.ultimateguis.engine.interfaces.PlayersRefreshFunction;
import ad.guis.ultimateguis.multithreading.Operation;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * klasa powinna być tworzona tylko dynamicznie, aby uaktualniać gui z graczami online
 */
public class PlayersGui extends ListGui<UUID> {

    private static final List<UUID> heads = new ArrayList<>();

    public PlayersGui() {
        this(null);
    }

    public PlayersGui(BasicGui previousGui) {
        this(null, null, previousGui, null);
    }

    public PlayersGui(BasicGui previousGui, String title) {
        this(null, null, previousGui, title);
    }

    public PlayersGui(OfflineAction action, PlayersRefreshFunction refreshFunction, BasicGui previousGui, String title) {
        super(action, refreshFunction, previousGui, title);
    }

    public static ItemStack calcHead(UUID player) {
        return calcHead(Bukkit.getOfflinePlayer(player).getName());
    }

    public static ItemStack calcHead(String name) {
        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwner(name);
        meta.setDisplayName(name);
        head.setItemMeta(meta);
        return head;
    }

    public static boolean isHeadCalc(UUID player) {
        return heads.contains(player);
    }

    public synchronized static void addUUIDtoHeads(UUID player) {
        heads.add(player);
    }

    @Override
    public ItemStack getDescriptionItem(UUID player) {
        if (heads.contains(player)) return calcHead(player);
        new Operation<>(() -> calcHead(player)).asyncSubscribe(item -> {
            replaceItem(player, item);
            addUUIDtoHeads(player);
        }).run();
        return BasicGui.createItem(Material.SKULL_ITEM, Bukkit.getOfflinePlayer(player).getName(), (short) 3);
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
}
