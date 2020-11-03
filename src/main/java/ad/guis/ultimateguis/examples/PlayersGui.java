package ad.guis.ultimateguis.examples;

import ad.guis.ultimateguis.engine.basics.BasicGui;
import ad.guis.ultimateguis.engine.basics.SwitchGui;
import ad.guis.ultimateguis.engine.interfaces.OfflineAction;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * klasa powinna być tworzona tylko dynamicznie, aby uaktualniać gui z graczami online
 */
public class PlayersGui extends SwitchGui {

    private HashMap<String, OfflinePlayer> players = new HashMap<>();
    private OfflineAction action;
    Collection<? extends OfflinePlayer> playerList;

    public PlayersGui(OfflineAction action, Collection<? extends OfflinePlayer> playerList, BasicGui previousGui) {
        this.action = action;
        this.previousGui = previousGui;
        this.playerList = playerList;
        init();
    }

    /**
     * @param player nick gracza, którego chcemy głowę
     * @return głowę wybranego gracza
     */
    static public ItemStack getPlayerHead(OfflinePlayer player) {

        return new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
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

    public PlayersGui(OfflineAction action, Collection<? extends OfflinePlayer> playerList) {
        this(action, playerList, null);
    }

    public OfflineAction getAction() {
        return action;
    }

    public void setAction(OfflineAction action) {
        this.action = action;
    }

    public void setPlayers(Collection<? extends OfflinePlayer> playerList) {
        this.playerList = playerList;
        init();
    }


    protected void init() {
        if (playerList == null) {
            playerList = new ArrayList<>();
        }
        players.clear();
        super.initPages((playerList.size() / 45) + 1, null);
        ItemStack item;
        ItemMeta meta;
        int counter = 0;
        int currentPage = 0;
        for (OfflinePlayer p : playerList) {
            if (counter >= 45) {
                currentPage++;
                counter = 0;
            }
            players.put(p.getName(), p);
            item = getPlayerHead(p);
            meta = item.getItemMeta();
            meta.setDisplayName(p.getName());
            item.setItemMeta(meta);

            ItemStack finalItem = item;
            this.guis.get(currentPage).addItem(item, playerWhoClicked -> {
                OfflinePlayer playerFromHead = players.get(finalItem.getItemMeta().getDisplayName());
                if(playerFromHead != null) {
                    if(action != null) action.action(playerFromHead);
                }
                else{
                   // playerWhoClicked.sendMessage(glc.getSinglePlayerGuiPlayerLeftTheGame());
                    System.out.println("Player not found");
                }
            });
            counter++;
        }
    }
}
