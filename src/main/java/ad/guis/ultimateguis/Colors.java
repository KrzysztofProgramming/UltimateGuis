package ad.guis.ultimateguis;

import org.bukkit.ChatColor;


public class Colors {
    public final static short WHITE = 0;
    public final static short ORANGE = 1;
    public final static short MAGENTA = 2;
    public final static short LIGHT_BLUE = 3;
    public final static short YELLOW = 4;
    public final static short LIME = 5;
    public final static short PINK = 6;
    public final static short GRAY = 7;
    public final static short LIGHT_GRAY = 8;
    public final static short CYAN = 9;
    public final static short PURPLE = 10;
    public final static short BLUE = 11;
    public final static short BROWN = 12;
    public final static short GREEN = 13;
    public final static short RED = 14;
    public final static short BLACK = 15;

    public static ChatColor toChatColor(short c){
        switch (c){
            case WHITE: return ChatColor.WHITE;
            case ORANGE: return ChatColor.GOLD;
            case MAGENTA:
            case PINK: return ChatColor.LIGHT_PURPLE;
            case LIGHT_BLUE: return ChatColor.BLUE;
            case YELLOW: return ChatColor.YELLOW;
            case LIME: return ChatColor.GREEN;
            case GRAY: return ChatColor.DARK_GRAY;
            case LIGHT_GRAY: return ChatColor.GRAY;
            case CYAN: return ChatColor.AQUA;
            case PURPLE: return ChatColor.DARK_PURPLE;
            case BLUE: return ChatColor.DARK_BLUE;
            case BROWN: return ChatColor.DARK_RED;
            case GREEN: return ChatColor.DARK_GREEN;
            case BLACK: return ChatColor.BLACK;
            default: return ChatColor.RESET;
        }
    }
}
