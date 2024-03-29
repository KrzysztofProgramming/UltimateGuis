package ad.guis.ultimateguis.examples;

import ad.guis.ultimateguis.Colors;
import ad.guis.ultimateguis.engine.basics.BasicGui;
import ad.guis.ultimateguis.engine.interfaces.Action;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ConfirmGui extends BasicGui {
    private final static ItemStack accept;
    private final static ItemStack deny;
    private final static ItemStack backgroundBlack;
    private final static ItemStack backgroundRed;
    private static final ItemStack backgroundGreen;

    static {
        accept = BasicGui.createItem(Material.INK_SACK, ChatColor.GREEN + "" + ChatColor.BOLD + "✓", (short) 10);
        deny = BasicGui.createItem(Material.INK_SACK, ChatColor.RED + "" + ChatColor.BOLD + "✕", (short) 1); //ma być 1
        backgroundBlack = BasicGui.createBackground(Colors.BLACK);
        backgroundRed = BasicGui.createBackground(Colors.RED);
        backgroundGreen = BasicGui.createBackground(Colors.GREEN);
    }

    private final Action accepted;
    private final Action denied;
    @Getter
    private final boolean unCloseable;
    private boolean actionSelected = false;

    /**
     * @param message  tytuł gui będący równocześnie pytaniem/zdaniem do potwierdzenia
     * @param accepted akcja wykonywana po potwierdzeniu
     * @param denied   akcja wykonywana po odrzuceniu
     */
    public ConfirmGui(String message, Action accepted, Action denied) {
        this(message, accepted, denied, false);
    }

    public ConfirmGui(String message, Action accepted, Action denied, boolean unCloseable) {
        super(3, message, null);
        this.accepted = accepted;
        this.denied = denied;
        this.unCloseable = unCloseable;
        init();
    }

    private void init() {

        this.setItem(2, 1, accept, element -> {
            actionSelected = true;
            accepted.action(element);
        });
        this.setItem(6, 1, deny,  element -> {
            actionSelected = true;
            denied.action(element);
        });

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

    @Override
    public void onClose() {
        if(isUnCloseable() && !actionSelected){
            this.open(this.getLastViewer());
        }
        super.onClose();
    }
}
