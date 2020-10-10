package ad.guis.ultimateguis.examples;

import ad.guis.ultimateguis.Colors;
import ad.guis.ultimateguis.engine.basics.BasicGui;
import ad.guis.ultimateguis.engine.interfaces.Action;
import ad.guis.ultimateguis.engine.interfaces.DateAction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class CalendarGui extends BasicGui {
    GregorianCalendar calendar = new GregorianCalendar(new Locale("FR", "FR"));
    DateAction dateAction;
    private static ItemStack nextYear;
    private static ItemStack previousYear;
    private static ItemStack nextMonth;
    private static ItemStack previousMonth;
    private static ItemStack backgroundBlack;
    private static ItemStack backItem;
    private static ItemStack exitItem;


    private static SimpleDateFormat dateFormatter;

    static {
        nextYear = new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta = nextYear.getItemMeta();
        meta.setDisplayName(ChatColor.BOLD + "Next Year");
        nextYear.setItemMeta(meta);

        previousYear = new ItemStack(Material.BLAZE_ROD);
        meta = previousYear.getItemMeta();
        meta.setDisplayName(ChatColor.BOLD + "Previous Year");
        previousYear.setItemMeta(meta);

        nextMonth = new ItemStack(Material.ARROW);
        meta = nextMonth.getItemMeta();
        meta.setDisplayName("Next Month");
        nextMonth.setItemMeta(meta);

        previousMonth = new ItemStack(Material.ARROW);
        meta = previousMonth.getItemMeta();
        meta.setDisplayName("Previous Month");
        previousMonth.setItemMeta(meta);

        dateFormatter = new SimpleDateFormat("dd.MM.yyyy");

        backgroundBlack = new ItemStack(Material.STAINED_GLASS_PANE, 1, Colors.BLACK);
        meta = backgroundBlack.getItemMeta();
        meta.setDisplayName(ChatColor.BOLD + "");
        backgroundBlack.setItemMeta(meta);

        backItem = new ItemStack(Material.WOOD_DOOR);
        meta = backItem.getItemMeta();
        meta.setDisplayName(ChatColor.GRAY + "" + ChatColor.BOLD + "Back");
        backItem.setItemMeta(meta);

        exitItem = new ItemStack(Material.BARRIER);
        meta = exitItem.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Exit");
        exitItem.setItemMeta(meta);
    }


    public CalendarGui(Date date, DateAction action, BasicGui previousGui) {
        super(6, new SimpleDateFormat("MM.yyyy").format(date), previousGui);
        this.dateAction = action;
        this.setDate(date);
    }

    public CalendarGui(Date date, DateAction action){
        this(date, action, null);
    }

    public CalendarGui(Date date){
        this(date, null);
    }
    public CalendarGui(){
        this(new Date());
    }

    private void init(){
        this.gui.clear();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int subValue = (calendar.get(Calendar.WEEK_OF_MONTH)==0) ? 0 : 1;
        int dayInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        for(int i=1; i <=dayInMonth; i++ ){
            ItemStack itemStack = getDateItem(calendar);
            this.setItem(getDayOfWeek(calendar), calendar.get(Calendar.WEEK_OF_MONTH) - subValue, itemStack, player -> {
                if(dateAction == null) return;
                dateAction.action(dateFormatter.parse(itemStack.getItemMeta().getDisplayName(), new ParsePosition(2)));
            });
            if(i<dayInMonth) calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
        }

        this.setItem(8, 5, nextMonth, player -> {
           if(calendar.get(Calendar.MONTH) == Calendar.DECEMBER){
               calendar.set(calendar.get(Calendar.YEAR) + 1, Calendar.JANUARY, 1);
           }
           else
               calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, 1);
           new CalendarGui(calendar.getTime(), dateAction, previousGui).open(player);
        });

        this.setItem(0,5, previousMonth, player -> {
            if(calendar.get(Calendar.MONTH) == Calendar.JANUARY){
                calendar.set(calendar.get(Calendar.YEAR) - 1, Calendar.DECEMBER, 1);
            }
            else
                calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) - 1, 1);
            new CalendarGui(calendar.getTime(), dateAction, previousGui).open(player);
        });

        this.setItem(0,4, previousYear, player -> {
                calendar.set(calendar.get(Calendar.YEAR) - 1, calendar.get(Calendar.MONTH), 1);
            new CalendarGui(calendar.getTime(), dateAction, previousGui).open(player);
        });

        this.setItem(8,4, nextYear, player -> {
            calendar.set(calendar.get(Calendar.YEAR) + 1, calendar.get(Calendar.MONTH), 1);
            new CalendarGui(calendar.getTime(), dateAction, previousGui).open(player);
        });

        this.setItem(0,1, backItem, player -> {
           if(previousGui == null) player.closeInventory();
           else previousGui.open(player);
        });

        this.setItem(0,0, exitItem, HumanEntity::closeInventory);

        this.autoFill(backgroundBlack);

    }

    void setDate(Date date){
        calendar.setTime(date);
        init();
    }

    public void setDateAction(DateAction dateAction){
        this.dateAction = dateAction;
    }

    private static ItemStack getDateItem(GregorianCalendar calendar){
        ItemStack stack;
        ChatColor color;
        int dayOfWeek = getDayOfWeek(calendar);

        if(dayOfWeek==7 || dayOfWeek == 6) { //SUNDAY OR SATURDAY
            stack = new ItemStack(Material.STAINED_GLASS_PANE,1, Colors.RED);
            color = ChatColor.RED;
        }
        else{
            stack = new ItemStack(Material.STAINED_GLASS_PANE,1, Colors.GREEN);
            color = ChatColor.GREEN;
        }
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(color + "" + dateFormatter.format(calendar.getTime()));
        stack.setItemMeta(meta);

        return stack;
    }

    private static int getDayOfWeek(GregorianCalendar calendar){
        //return calendar.get(Calendar.DAY_OF_WEEK);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if(dayOfWeek==1) dayOfWeek = 7;
        else --dayOfWeek;
        return dayOfWeek;
    }


    public final static int PREVIOUS_YEAR = 0;
    public final static int NEXT_YEAR = 1;
    public final static int PREVIOUS_MONTH = 2;
    public final static int NEXT_MONTH = 3;
    public final static int BACK = 4;
    public final static int EXIT = 5;
    /**
     * @param itemID 0-previousYear, 1-nextYear, 2-previousMonth, 3-nextMonth, 4-back, 5-exit
     */
    public static boolean setItemName(int itemID, String name){
        ItemMeta meta;
        ItemStack item;
        switch (itemID){
            case PREVIOUS_YEAR:{
                item = previousYear;
                break;
            }
            case NEXT_YEAR:{
               item = nextYear;
                break;
            }
            case PREVIOUS_MONTH:{
                item = previousMonth;
                break;
            }
            case NEXT_MONTH:{
                item = nextMonth;
                break;
            }
            case BACK:{
                item = backItem;
                break;
            }
            case EXIT:{
                item = exitItem;
                break;
            }
            default: return false;
        }
        meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return true;
    }

}
