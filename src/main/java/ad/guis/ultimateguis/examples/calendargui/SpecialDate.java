package ad.guis.ultimateguis.examples.calendargui;

import ad.guis.ultimateguis.Colors;

import java.util.Date;

public class SpecialDate{
    public Date date;
    public String comment;
    public short color;

    public SpecialDate(Date date, String comment, short color) {
       /* if((date.getTime() % 86400)!=0){
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            this.date = calendar.getTime();
            Bukkit.broadcastMessage("konwersja");
            Bukkit.broadcastMessage(date + "----" + calendar.getTime());
        }
        else */this.date = date;
        this.comment = comment;
        this.color = color;
    }

    public SpecialDate(Date date, String comment){
        this(date, comment, Colors.ORANGE);
    }

    public SpecialDate(Date date){
        this(date, "");
    }
}
