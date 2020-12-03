package ad.guis.ultimateguis.examples;

import ad.guis.ultimateguis.engine.basics.BasicGui;
import ad.guis.ultimateguis.engine.basics.ListGui;
import ad.guis.ultimateguis.engine.interfaces.OfflineAction;
import ad.guis.ultimateguis.engine.interfaces.PlayersRefreshFunction;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

/**
 * klasa powinna być tworzona tylko dynamicznie, aby uaktualniać gui z graczami online
 */
public class PlayersGui extends ListGui<OfflinePlayer> {

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

    @Override
    public ItemStack getDescriptionItem(OfflinePlayer player) {
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
}
