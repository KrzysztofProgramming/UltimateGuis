package ad.guis.ultimateguis.examples.calendargui;

import ad.guis.ultimateguis.Colors;
import ad.guis.ultimateguis.engine.basics.BasicGui;
import ad.guis.ultimateguis.engine.interfaces.Action;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;

public class CalendarGui extends BasicGui {
    private GregorianCalendar calendar;
    private CalendarGuiAction calendarGuiAction;
    private Action acceptAction = null;

    private static ItemStack nextYear;
    private static ItemStack previousYear;
    private static ItemStack nextMonth;
    private static ItemStack previousMonth;
    private static ItemStack backgroundBlack;
    private static ItemStack backItem;
    private static ItemStack exitItem;
    private static ItemStack acceptItem;
    static {
        nextYear = BasicGui.createItem(Material.BLAZE_ROD, ChatColor.BOLD + "Next Year");
        previousYear = BasicGui.createItem(Material.BLAZE_ROD, ChatColor.BOLD + "Previous Year");
        nextMonth = BasicGui.createItem(Material.ARROW,"Next Month");
        previousMonth = BasicGui.createItem(Material.ARROW, "Previous Month");
        backgroundBlack = BasicGui.createBackground(Colors.BLACK);
        backItem = BasicGui.createItem(Material.WOOD_DOOR, ChatColor.GRAY + "" + ChatColor.BOLD + "Back");
        exitItem = BasicGui.createItem(Material.BARRIER, ChatColor.RED + "" + ChatColor.BOLD + "Exit");
        acceptItem = BasicGui.createItem(Material.DOUBLE_PLANT, ChatColor.GREEN + "Accept");
    }

    private String title;
    private List<SpecialDate> specialDateList;
    private SpecialDate firstSpecialDate;
    private SpecialDate secondSpecialDate;



    private static SimpleDateFormat dateFormatter;
    public CalendarGui(CalendarGui another){
        super(6,'[' + new SimpleDateFormat("MM.yyyy").format(another.calendar.getTime()) + "] " + another.title ,
                another.previousGui);
        this.title = another.title;
        this.calendarGuiAction = another.calendarGuiAction;
        this.specialDateList = another.specialDateList;
        this.firstSpecialDate = another.firstSpecialDate;
        this.secondSpecialDate = another.secondSpecialDate;
        this.calendar = another.calendar;
        this.acceptAction = another.acceptAction;
        this.dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
        this.init();
    }

    public CalendarGui(Date date, CalendarGuiAction action, String title, BasicGui previousGui) {
        super(6,'[' + new SimpleDateFormat("MM.yyyy").format(date) + "] " + title , previousGui);
        specialDateList = new ArrayList<>();
        this.calendar = new GregorianCalendar(new Locale("FR", "FR"));
        this.title = title;
        this.calendarGuiAction = action;
        this.dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
        this.setDate(date);
    }

    public CalendarGui(Date date, CalendarGuiAction action, String title){
        this(date, action, title, null);
    }

    public CalendarGui(Date date, CalendarGuiAction action){
        this(date, action, "");
    }

    public CalendarGui(Date date){
        this(date, null);
    }
    public CalendarGui(){
        this(new Date());
    }

    public void setFirstSpecialDate(SpecialDate firstSpecialDate){
        this.firstSpecialDate = firstSpecialDate;
        this.init();
    }

    public void setSecondSpecialDate(SpecialDate secondSpecialDate){
        this.secondSpecialDate = secondSpecialDate;
        this.init();
    }

    public void setSpecialDateRange(SpecialDate firstSpecialDate, SpecialDate secondSpecialDate){
        this.firstSpecialDate = firstSpecialDate;
        this.secondSpecialDate = secondSpecialDate;
        this.init();
    }

    public void addSpecialDate(SpecialDate specialDate){
        this.specialDateList.add(specialDate);
        this.init();
    }

    public void addSpecialDates(List<SpecialDate> specialDates){
        this.specialDateList.addAll(specialDates);
        this.init();
    }

    public void setAcceptAction(Action action){
        boolean init = (action!=null && acceptAction==null) || (action==null && acceptAction!=null);
        this.acceptAction = action;
        if(init) this.init();
    }

    private void init(){

        this.gui.clear();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int subValue = (calendar.get(Calendar.WEEK_OF_MONTH) == 0) ? 0 : 1;
        int dayInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        for(int i=1; i <=dayInMonth; i++ ){
            ItemStack itemStack = getDateItem(calendar);
            this.setItem(getDayOfWeek(calendar), calendar.get(Calendar.WEEK_OF_MONTH) - subValue, itemStack, player -> {
                if(calendarGuiAction == null) return;
                calendarGuiAction.action(dateFormatter.parse(itemStack.getItemMeta().getDisplayName(), new ParsePosition(2)),
                        player, CalendarGui.this);
            });
            if(i<dayInMonth) calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
        }

        this.setItem(8, 5, nextMonth, player -> {
           if(calendar.get(Calendar.MONTH) == Calendar.DECEMBER){
               calendar.set(calendar.get(Calendar.YEAR) + 1, Calendar.JANUARY, 1);
           }
           else
               calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, 1);
           new CalendarGui(this).open(player);
        });

        this.setItem(0,5, previousMonth, player -> {
            if(calendar.get(Calendar.MONTH) == Calendar.JANUARY){
                calendar.set(calendar.get(Calendar.YEAR) - 1, Calendar.DECEMBER, 1);
            }
            else
                calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) - 1, 1);
            new CalendarGui(this).open(player);
        });

        this.setItem(0,4, previousYear, player -> {
                calendar.set(calendar.get(Calendar.YEAR) - 1, calendar.get(Calendar.MONTH), 1);
            new CalendarGui(this).open(player);
        });

        this.setItem(8,4, nextYear, player -> {
            calendar.set(calendar.get(Calendar.YEAR) + 1, calendar.get(Calendar.MONTH), 1);
            new CalendarGui(this).open(player);
        });

        this.setItem(0,1, backItem, player -> {
           if(previousGui == null) player.closeInventory();
           else previousGui.open(player);
        });

        this.setItem(0,0, exitItem, HumanEntity::closeInventory);

        if(this.acceptAction!=null) this.setItem(4,5,acceptItem, acceptAction);

        this.autoFill(backgroundBlack);

    }

    void setDate(Date date){
        calendar.setTime(date);
        init();
    }

    public void setCalendarGuiAction(CalendarGuiAction calendarGuiAction){
        this.calendarGuiAction = calendarGuiAction;
    }

    private ItemStack getDateItem(GregorianCalendar calendar){
        ItemStack stack;
        ChatColor color;
        Date date = calendar.getTime();
        int dayOfWeek = getDayOfWeek(calendar);

        ItemMeta meta;
        for(SpecialDate specialDate: specialDateList){
            if(specialDate.date.equals(date)){
                return this.createSpecialDateItem(specialDate);
            }
        }


        if(firstSpecialDate!= null && firstSpecialDate.date.equals(date)){
            return this.createSpecialDateItem(firstSpecialDate);
        }
        else if(secondSpecialDate!=null && secondSpecialDate.date.equals(date)){
            return this.createSpecialDateItem(secondSpecialDate);
        }
        else {
            if(secondSpecialDate!=null && firstSpecialDate!=null) {
                if ((date.getTime() > firstSpecialDate.date.getTime() && date.getTime() < secondSpecialDate.date.getTime()) ||
                        (date.getTime() < firstSpecialDate.date.getTime() && date.getTime() > secondSpecialDate.date.getTime())) {
                    return this.createSpecialDateItem(new SpecialDate(date, "", firstSpecialDate.color));
                }
            }
        }

        if (dayOfWeek == 7 || dayOfWeek == 6) { //SUNDAY OR SATURDAY
            stack = new ItemStack(Material.STAINED_GLASS_PANE, 1, Colors.RED);
            color = ChatColor.RED;
        } else {
            stack = new ItemStack(Material.STAINED_GLASS_PANE, 1, Colors.GREEN);
            color = ChatColor.GREEN;
        }

        meta = stack.getItemMeta();
        meta.setDisplayName(createItemName(date, color));
        stack.setItemMeta(meta);
        return stack;
    }

    private ItemStack createSpecialDateItem(SpecialDate specialDate){

        ItemStack stack = new ItemStack(Material.STAINED_GLASS_PANE, 1, specialDate.color);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(createItemName(specialDate.date, Colors.toChatColor(specialDate.color)));

        if(!specialDate.comment.isEmpty()) {
            meta.setLore(BasicGui.splitLoreNicely(specialDate.comment, 15));
        }
        stack.setItemMeta(meta);

        return stack;
    }

    private String createItemName(Date date, ChatColor color){
        return color + "" + dateFormatter.format(date);
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
    public boolean setItemName(int itemID, String name){
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
