package ad.guis.ultimateguis.examples;

import ad.guis.ultimateguis.Colors;
import ad.guis.ultimateguis.engine.basics.BasicGui;
import ad.guis.ultimateguis.engine.interfaces.Action;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ConfirmGui extends BasicGui {
    private final static ItemStack accept;
    private final static ItemStack deny;
    private final static ItemStack backgroundBlack;
    private final static ItemStack backgroundRed;
    private static final ItemStack backgroundGreen;

    static {
        ItemMeta meta;

        accept = new ItemStack(Material.INK_SACK, 1, (short) 10);
        meta = accept.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "✓");
        accept.setItemMeta(meta);

        deny = new ItemStack(Material.INK_SACK, 1, (short) 1); //ma być 1
        meta = deny.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "✕");
        deny.setItemMeta(meta);

        backgroundBlack = BasicGui.createBackground(Colors.BLACK);
        backgroundRed = BasicGui.createBackground(Colors.RED);
        backgroundGreen =BasicGui.createBackground(Colors.GREEN);
    }

    private Action accepted;
    private Action denied;

    /**
     * @param message  tytuł gui będący równocześnie pytaniem/zdaniem do potwierdzenia
     * @param accepted akcja wykonywana po potwierdzeniu
     * @param denied   akcja wykonywana po odrzuceniu
     */
    public ConfirmGui(String message, Action accepted, Action denied) {
        super(3, message, null);
        this.accepted = accepted;
        this.denied = denied;
        init();
    }

    private void init() {

        this.setItem(2, 1, accept, accepted);
        this.setItem(6, 1, deny, denied);

        this.setItem(4, 0, backgroundBlack, null);
        this.setItem(4, 1, backgroundBlack, null);
        this.setItem(4, 2, backgroundBlack, null);

        this.setItem(5, 0, backgroundRed, null);
        this.setItem(5, 1, backgroundRed, null);
        this.setItem(5, 2, backgroundRed, null);
        this.setItem(6, 0, backgroundRed, null);
        this.setItem(6, 2, backgroundRed, null);

        this.setItem(7,0,backgroundRed,null);
        this.setItem(7,1,backgroundRed,null);
        this.setItem(7,2,backgroundRed,null);
        this.setItem(8,0,backgroundRed,null);
        this.setItem(8,1,backgroundRed,null);
        this.setItem(8,2,backgroundRed,null);

        this.autoFill(backgroundGreen);
    }
}
