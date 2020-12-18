package ad.guis.ultimateguis.examples.calendargui;

import ad.guis.ultimateguis.Colors;
import ad.guis.ultimateguis.engine.basics.BasicGui;
import ad.guis.ultimateguis.engine.interfaces.Action;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class CalendarGui extends BasicGui {
    private YearMonth currentMonth;
    private CalendarGuiAction calendarGuiAction;
    private Action acceptAction = null;

    @Getter
    @Setter
    private static ItemStack nextYear;
    @Getter
    @Setter
    private static ItemStack previousYear;
    @Getter
    @Setter
    private static ItemStack nextMonth;
    @Getter
    @Setter
    private static ItemStack previousMonth;
    @Getter
    @Setter
    private static ItemStack backgroundBlack;
    @Getter
    @Setter
    private static ItemStack backItem;
    @Getter
    @Setter
    private static ItemStack exitItem;
    @Getter
    @Setter
    private static ItemStack acceptItem;

    static {
        nextYear = BasicGui.createItem(Material.BLAZE_ROD, ChatColor.BOLD + "Next Year");
        previousYear = BasicGui.createItem(Material.BLAZE_ROD, ChatColor.BOLD + "Previous Year");
        nextMonth = BasicGui.createItem(Material.ARROW, "Next Month");
        previousMonth = BasicGui.createItem(Material.ARROW, "Previous Month");
        backgroundBlack = BasicGui.createBackground(Colors.BLACK);
        backItem = BasicGui.createItem(Material.WOOD_DOOR, ChatColor.GRAY + "" + ChatColor.BOLD + "Back");
        exitItem = BasicGui.createItem(Material.BARRIER, ChatColor.RED + "" + ChatColor.BOLD + "Exit");
        acceptItem = BasicGui.createItem(Material.DOUBLE_PLANT, ChatColor.GREEN + "Accept");
    }

    private final String title;
    private final List<SpecialDate> specialDateList;
    private SpecialDate firstSpecialDate;
    private SpecialDate secondSpecialDate;
    private final DateTimeFormatter dateFormatter;


    public CalendarGui(YearMonth month, CalendarGuiAction action, String title, BasicGui previousGui) {
        super(6,'[' + DateTimeFormatter.ofPattern("MM.yyyy").format(month) + "] " + title , previousGui);
        this.specialDateList = new ArrayList<>();
        this.currentMonth = month;
        this.title = title;
        this.calendarGuiAction = action;
        this.dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    }

    public CalendarGui(YearMonth month, CalendarGuiAction action, String title){
        this(month, action, title, null);
    }

    public CalendarGui(YearMonth month, CalendarGuiAction action){
        this(month, action, "");
    }

    public CalendarGui(YearMonth month){
        this(month, null);
    }
    public CalendarGui(){
        this(YearMonth.now());
    }

    public void setFirstSpecialDate(SpecialDate firstSpecialDate){
        this.firstSpecialDate = firstSpecialDate;
        this.init(false);
    }

    public void setSecondSpecialDate(SpecialDate secondSpecialDate){
        this.secondSpecialDate = secondSpecialDate;
        this.init(false);
    }

    public void setSpecialDateRange(SpecialDate firstSpecialDate, SpecialDate secondSpecialDate){
        this.firstSpecialDate = firstSpecialDate;
        this.secondSpecialDate = secondSpecialDate;
        this.init(false);
    }

    public void addSpecialDate(SpecialDate specialDate){
        this.specialDateList.add(specialDate);
        this.init(false);
    }

    public void addSpecialDates(List<SpecialDate> specialDates){
        this.specialDateList.addAll(specialDates);
        this.init(false);
    }

    public void setAcceptAction(Action action){
        boolean init = (action!=null && acceptAction==null) || (action==null && acceptAction!=null);
        this.acceptAction = action;
        if (init) this.init(false);
    }

    private void init(boolean newTitle) {
        int dayInMonth = currentMonth.lengthOfMonth();
        if (newTitle)
            this.gui = BasicGui.createFullInventory('[' + DateTimeFormatter.ofPattern("MM.yyyy").format(currentMonth) + "] " + title);

        this.actions.clear();
        for (int i = 1; i <= dayInMonth; i++) {
            LocalDate date = currentMonth.atDay(i);
            ItemStack itemStack = getDateItem(date);

            this.setItem(date.getDayOfWeek().getValue(),
                    date.get(WeekFields.of(DayOfWeek.MONDAY, 1).weekOfMonth()) - 1,
                    itemStack, player -> {
                        if (calendarGuiAction == null) return;
                calendarGuiAction.action(LocalDate.parse(clearColors(itemStack.getItemMeta().getDisplayName()),
                        dateFormatter), player, CalendarGui.this);
            });
        }

        this.setItem(8, 5, nextMonth, player -> {
            this.currentMonth = this.currentMonth.plus(1, ChronoUnit.MONTHS);
            this.open(player);
        });

        this.setItem(0,5, previousMonth, player -> {
            this.currentMonth = this.currentMonth.minus(1, ChronoUnit.MONTHS);
            this.open(player);
        });

        this.setItem(0,4, previousYear, player -> {
            this.currentMonth = this.currentMonth.minus(1, ChronoUnit.YEARS);
            this.open(player);
        });

        this.setItem(8,4, nextYear, player -> {
            this.currentMonth = this.currentMonth.plus(1, ChronoUnit.YEARS);
            this.open(player);
        });

        this.setItem(0,1, backItem, player -> {
           if(previousGui == null) player.closeInventory();
           else previousGui.open(player);
        });

        this.setItem(0,0, exitItem, HumanEntity::closeInventory);

        if(this.acceptAction!=null) this.setItem(4,5 ,acceptItem, acceptAction);

        this.autoFill(backgroundBlack);

    }

    public void setCalendarGuiAction(CalendarGuiAction calendarGuiAction){
        this.calendarGuiAction = calendarGuiAction;
    }

    private ItemStack getDateItem(LocalDate date){
        ItemStack stack;
        ChatColor color;
        DayOfWeek dayOfWeek = date.getDayOfWeek();

        ItemMeta meta;
        for(SpecialDate specialDate: specialDateList){
            if(specialDate.date.isEqual(date)){
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
                if ((date.isAfter(firstSpecialDate.date) && date.isBefore(secondSpecialDate.date)) ||
                        (date.isBefore(firstSpecialDate.date) && date.isAfter(secondSpecialDate.date))) {
                    return this.createSpecialDateItem(new SpecialDate(date, "", firstSpecialDate.color));
                }
            }
        }

        if (dayOfWeek == DayOfWeek.SUNDAY || dayOfWeek == DayOfWeek.SATURDAY) { //SUNDAY OR SATURDAY
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
            meta.setLore(BasicGui.splitLore(specialDate.comment, 20));
        }
        stack.setItemMeta(meta);

        return stack;
    }

    private String createItemName(LocalDate date, ChatColor color){
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

    @Override
    public void open(Player opener) {
        init(true);
        super.open(opener);
    }
}
