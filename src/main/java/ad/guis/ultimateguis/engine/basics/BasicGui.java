package ad.guis.ultimateguis.engine.basics;

import ad.guis.ultimateguis.UltimateGuis;
import ad.guis.ultimateguis.engine.interfaces.Action;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class BasicGui {
    protected Inventory gui;
    protected Map<Integer, Action> actions = new HashMap<>();
    protected BasicGui previousGui;
    protected Player viewer;
    private boolean isOpen = false;
    private long lastClick = 0;

    public long getLastClick() {
        return lastClick;
    }

    void setLastClick(long lastClick) {
        this.lastClick = lastClick;
    }

    /**
     * @return player who last open this Inventory
     */
    public Player getLastViewer() {
        return viewer;
    }

    /**
     * rejestruje event obsługujący dane gui
     * Zwraca wyjątek gdy rowsAmount jest większe od 6 bądź niedodatnie
     * @param rowsAmount  wielkość gui (inventory)
     * @param title tytuł gui
     */
    public BasicGui(int rowsAmount, String title, BasicGui previousGui) throws IllegalArgumentException {
        this.previousGui = previousGui;
        if (rowsAmount > 6) throw new IllegalArgumentException("Wrong rowsAmount!");
        gui = Bukkit.createInventory(null, rowsAmount * 9, title);
    }

    protected BasicGui() {
    }

    public static Inventory createInventory(int rowsCount, String title) {
        return Bukkit.createInventory(null, 9 * rowsCount, title);
    }

    public BasicGui(int rowsAmount, String title) throws IllegalArgumentException {
        this(rowsAmount, title, null);
    }

    public static Inventory createFullInventory(String title) {
        return BasicGui.createInventory(6, title);
    }


    /**
     * zwraca previous gui, należy sprawdzić czy to nie null
     */
    public BasicGui getPreviousGui() {
        return previousGui;
    }

    /**
     * ustawia previous gui
     */
    public void setPreviousGui(BasicGui previousGui) {
        this.previousGui = previousGui;
    }

    /**
     * Wypełnia puste miejsca gui wybranym itemem
     *
     * @param item item, którym będzie wypełniane gui
     */
    public void autoFill(ItemStack item) {
        for (int i = 0; i < gui.getSize(); i++) {
            if(gui.getItem(i) == null){
                gui.setItem(i,item);
            }
        }
    }

    /**
     * Wypełnia puste miejsca gui szachownicą 2 itemów zaczynając od itemu1
     * @param item1 pierwszy item wypełniający
     * @param item2 drugi item wypełniający
     */
    public void autoFill(ItemStack item1, ItemStack item2) {
        boolean firts = true;
        for (int i = 0; i < gui.getSize(); i++) {
            if (gui.getItem(i) == null) {
                gui.setItem(i, (firts) ? item1 : item2);
            }
            firts = !firts;
        }
    }

    /**
     * Obramowuje gui, podanym itemem, ale nie usuwa itemów
     *
     * @param item
     */
    public boolean autoFrame(ItemStack item) {
        if (this.getRowsAmount() < 3) return false;
        for (int i = 0; i < 9; i++) {
            if (gui.getItem(i) == null) {
                gui.setItem(i, item);
            }
        }
        for (int i = 9; i < gui.getSize(); i += 9) {
            if (gui.getItem(i) == null) {
                gui.setItem(i,item);
            }
        }
        for(int i = 17; i<gui.getSize(); i+=9){
            if(gui.getItem(i)==null){
                gui.setItem(i, item);
            }
        }
        for (int i = gui.getSize() - 9; i < gui.getSize(); i++) {
            if (gui.getItem(i) == null) {
                gui.setItem(i, item);
            }
        }
        return true;
    }

    void setClosed() {
        this.isOpen = false;
    }

    /**
     * Dodaje item w pierwsze wolne miejsce, jeśli takie istnieje
     * UWAGA: może połączyć ten item z innym będącym w gui, jeśli będą TAKIE SAME
     *
     * @param item   item do dodania
     * @param action akcja wywołana po klikniąciu itemu
     * @return prawda jeśli item został dodany
     */
    public boolean addItem(ItemStack item, Action action) {
        int firstEmptySlot = gui.firstEmpty();
        if (firstEmptySlot != -1) {
            gui.addItem(item);
            putToActions(firstEmptySlot, action);
            return true;
        }
        return false;
    }

    public static ItemStack modifyLore(ItemStack item, List<String> newLore) {
        ItemMeta meta = item.getItemMeta();
        meta.setLore(newLore);
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Zwraca prawdę, jeśli item został dodany
     * UWAGA: może usunąć item jeśli taki istnieje w podanym miejscu
     *
     * @param positionX Numer kolumny gdzie dodawany jest item, numeracja od 0
     * @param positionY Numer wiersza gdzie dodawany jest item, numeracja od 0
     * @param item      item do dodania
     * @param action    akcja wywołana po klikniąciu itemu
     */

    public boolean setItem(int positionX, int positionY, ItemStack item, Action action) {
        return this.setItem(positionY * 9 + positionX, item, action);
    }

    protected boolean setItem(int position, ItemStack item, Action action) {
        if (position >= gui.getSize()) return false;
        gui.setItem(position, item);
        putToActions(position, action);
        return true;
    }

    protected void replaceItem(int position, ItemStack newItem) {
        this.gui.setItem(position, newItem);
    }

    /**
     * otwiera gui, podobnie jak removeFromListeners powinno być nadpisywane, gdy mamy dodać jakieś gui do innego Listenera
     * przykład: SingleAreaGui i PlayerActionGui
     *
     * @param opener
     */
    public void open(Player opener) {
        this.isOpen = true;
        viewer = opener;
        UltimateGuis.getInstance().getGuiListener().addGui(this, opener);
    }

    /**
     * Aby otworzyć gui przez gracza użyj .open Ta funkcja nie rejestruje gui do listenera
     * używać do porównywania gui itp.
     * @return gui Inventory
     */
    public Inventory getGui(){
        return gui;
    }

    /**
     * @return akcje wykonywane po kliknięciu jakiegoś przedmiotu
     */
    public Map<Integer, Action> getActions() {
        return actions;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public int getRowsAmount() {
        return gui.getSize() / 9;
    }

    public static ItemStack createBackItem(String name) {
        return BasicGui.createItem(Material.WOOD_DOOR, name);
    }

    /**
     * funkcja, która powinna być nadpisywana, jeżeli gui wymaga specjalnego usunięcia przy zamknięciu
     * np. gdy jakieś bardziej wyspecjalizowane gui jest jeszcze zarejestrowane w innym listenerze
     * przykładowo: PlayerActionGui i SingleAreaGui
     */
    public void onClose() {
    }


    public static List<String> splitLore(String lore, int characterLimit, char colorChar) {
        String[] splitedByLine = lore.split("\n");
        List<String> newLore = new ArrayList<>();
        for (String s : splitedByLine) {
            newLore.addAll(splitLoreBasic(s, characterLimit, colorChar));
        }
        return newLore;
    }

    public static ItemStack createItem(Material materialType, String name, List<String> lore, short data) {
        ItemStack item = new ItemStack(materialType, 1, data);
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        if (name != null) meta.setDisplayName(name);
        if (lore != null) meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createSegmentedItem(Material materialType, String nameWithLore, short data) {
        List<String> splited = splitLore(nameWithLore, 25);
        if (splited.size() == 0) return BasicGui.createItem(materialType, nameWithLore, data);
        String name = splited.get(0);
        splited.remove(0);
        return BasicGui.createItem(materialType, name, splited, data);
    }

    public static ItemStack createSegmentedItem(Material materialType, String nameWithLore) {
        return createSegmentedItem(materialType, nameWithLore, (short) 0);
    }

    public static ItemStack createItem(Material materialType, String name, short data) {
        return createItem(materialType, name, null, data);
    }

    public static ItemStack createItem(Material materialType, String name, List<String> lore) {
        return createItem(materialType, name, lore, (short) 0);
    }

    public static ItemStack createItem(Material materialType, String name) {
        return createItem(materialType, name, null, (short) 0);
    }

    public static ItemStack createBackground(short color) {
        return createItem(Material.STAINED_GLASS_PANE, ChatColor.MAGIC + "", color);
    }

    public void replaceItem(int positionX, int positionY, ItemStack newItem) {
        this.replaceItem(positionY * 9 + positionX, newItem);
    }

    public static ItemStack createExitItem(String name) {
        return BasicGui.createItem(Material.BARRIER, name);
    }

    public static String clearColors(String phrase) {
        return clearColors(phrase, '§');
    }

    private static final String colorsChars = "0123456789aAbBcCdDeEfF";
    private static final String formattingChars = "kKlLmMnNoO";
    private static final String resetChars = "rR";

    public static String clearColors(String phrase, char colorChar) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < phrase.length(); i++) {
            if (phrase.charAt(i) == colorChar) {
                i++;
                continue;
            }
            builder.append(phrase.charAt(i));
        }
        return builder.toString();
    }

    protected void backOrClose(Player p) {
        if (previousGui != null) previousGui.open(p);
        else p.closeInventory();
    }

    public static List<String> simpleSplitLore(String... lore) {
        return new ArrayList<>(Arrays.asList(lore));
    }

    public static List<String> splitLore(String lore, int characterLimit) {
        return splitLore(lore, characterLimit, '§');
    }

    public static List<String> splitLoreWithConversion(String lore, int characterLimit) {
        return splitLore(lore.replace('&', '§'), characterLimit, '§');
    }

    private static List<String> splitLoreBasic(String lore, int characterLimit, char colorChar) {
        if (characterLimit <= 0) characterLimit = 1;
        if (lore.length() <= characterLimit) return new ArrayList<>(Collections.singleton(lore));

        String currentColors = "";
        String currentFormatting = "";
        String reset = "";

        StringBuilder singleLine = new StringBuilder();
        List<String> splitedLore = new ArrayList<>();

        String[] loreWords = lore.split(" ");
        int position = 0;

        for(int i=0; i<loreWords.length; i++){
            String word = loreWords[i];
            String[] wrappedWord;
            if(lengthWithoutSpecialCharacters(word, colorChar) > characterLimit) {
                wrappedWord = splitStartingFrom(word, characterLimit - position, characterLimit, colorChar);
            }
            else{
                wrappedWord = new String[1];
                wrappedWord[0] = word;
            }

            for (int j=0; j<wrappedWord.length; j++) {
                word = wrappedWord[j];
                word = shortColors(word, colorChar);
                String[] fixedFormatting = fixFormatting(word, currentFormatting, colorChar);
                word = fixedFormatting[0];
                int wordLength = lengthWithoutSpecialCharacters(word, colorChar);

                if (singleLine.length() == 0) {
                    singleLine.append(reset).append(currentColors).append(currentFormatting).append(word);
                } else if (lengthWithoutSpecialCharacters(singleLine.toString(), colorChar) +
                        wordLength + 1 > characterLimit) {
                    splitedLore.add(shortColors(singleLine.toString(), colorChar));
                    singleLine.setLength(0);
                    --j;
                    continue;
                } else {
                    singleLine.append(' ').append(word);
                }

                if (reset.isEmpty()) reset = reloadReset(word, colorChar);

                currentFormatting = fixedFormatting[1];
                currentColors = getColorsFromPhrase(word, currentColors, colorChar);
                position = lengthWithoutSpecialCharacters(singleLine.toString(), colorChar);
            }
        }
        if (singleLine.length() != 0) splitedLore.add(shortColors(singleLine.toString(), colorChar));
        return splitedLore;
    }

    private void putToActions(int position, Action action) {
        if (action == null) return;
        this.actions.put(position, action);
    }

    private static String shortColors(String phrase, char colorChar) {
        char currentColor = ' ';
        StringBuilder newString = new StringBuilder();
        for (int i = 0; i < phrase.length(); i++) {
            if (phrase.charAt(i) == colorChar && i + 1 < phrase.length()) {
                char specialChar = phrase.charAt(i + 1);
                if (isColor(specialChar)) {
                    if (specialChar == currentColor) {
                        i++;
                        continue;
                    }
                    currentColor = specialChar;
                } else if (isReset(specialChar)) {
                    currentColor = ' ';
                }
            }
            newString.append(phrase.charAt(i));
        }
        return newString.toString();
    }

    private static int lengthWithoutSpecialCharacters(String phrase, char colorChar) {
        int colorsCounter = 0;
        for (int i = 0; i + 1 < phrase.length(); i++) {
            if (phrase.charAt(i) == colorChar) {
                colorsCounter += 2;
            }
        }
        return phrase.length() - colorsCounter;
    }

    private static String customSubstring(String phrase, int start, int stop, char colorChar){
        StringBuilder builder = new StringBuilder();
        int counter = 0;
        boolean ignore = false;
        for(int i=0; i<phrase.length(); i++){
            if(counter >= start && counter < stop){
                builder.append(phrase.charAt(i));
            }
            if(phrase.charAt(i) == colorChar){
                ignore = true;
            }
            else if(ignore){
                ignore = false;
            }
            else{
                counter++;
            }
        }
        return builder.toString();
    }

    private static String[] splitStartingFrom(String phrase, int start, int characterLimit, char colorChar){
        int phraseLength = lengthWithoutSpecialCharacters(phrase, colorChar);
        if(characterLimit <=0 ) characterLimit = 1;
        if(start > phraseLength) start = phraseLength;

        String[] splited = new String[((start!=0)? 1 : 0) + (phraseLength - start) / characterLimit +
                (((phraseLength - start) % characterLimit != 0) ? 1 : 0)];
        splited[0] = customSubstring(phrase, 0, start, colorChar);
        int indexCounter = (start!=0)? 1 : 0;

        for(int i = start; i < phraseLength; i+=characterLimit){
            splited[indexCounter] = (customSubstring(phrase, i, i+characterLimit, colorChar));
            indexCounter++;
        }
        return splited;
    }

    private static String getColorsFromPhrase(String phrase, String currentColors, char colorChar){
        for(int i = 0; i + 1 < phrase.length(); i++){
            if(phrase.charAt(i) == colorChar){
                if(isReset(phrase.charAt(i + 1))){
                    currentColors = "";
                }
                else if(isColor(phrase.charAt(i + 1))){
                    currentColors = replaceColor(currentColors, phrase.charAt(i + 1), colorChar);
                }
            }
        }
        return currentColors;
    }

    private static String replaceColor(String phrase, char newColor, char colorChar){
        for(int i=0; i+1 < phrase.length(); i++){
            if(phrase.charAt(i) == colorChar && isColor(phrase.charAt(i + 1))){
                return phrase.replace(phrase.charAt(i + 1), newColor);
            }
        }
        return phrase + colorChar + newColor;
    }


    //first fixed phare, second - changedFormatting
    private static String[] fixFormatting(String phrase, String formatting, char colorChar){
        StringBuilder formattingBuilder = new StringBuilder(formatting);

        for(int i = 0; i + 1 < phrase.length(); i++){
            if(phrase.charAt(i) == colorChar) {
                if (isColor(phrase.charAt(i + 1))) {
                    String regex = colorChar + "" + phrase.charAt(i + 1);
                    phrase = phrase.replaceAll(regex, regex + formattingBuilder.toString());
                }
                else if(isFormatting(phrase.charAt(i + 1)) && formattingBuilder.indexOf(phrase.charAt(i + 1) + "") < 0){
                    formattingBuilder.append(colorChar).append(phrase.charAt(i + 1));
                }
                else if(isReset(phrase.charAt(i + 1))){
                    formattingBuilder.setLength(0);
                }
            }
        }
        String[] returnedValue= new String[2];
        returnedValue[0] = phrase;
        returnedValue[1] = formattingBuilder.toString();
        return returnedValue;
    }

    private static boolean isColor(char character){
        return colorsChars.indexOf(character) >= 0;
    }

    private static boolean isFormatting(char character){
        return formattingChars.indexOf(character) >= 0;
    }

    private static boolean isReset(char character){
        return resetChars.indexOf(character) >= 0;
    }

    private static String reloadReset(String phrase, char colorChar){
        for(int i=0 ;i + 1<phrase.length(); i++){
            if(phrase.charAt(i) == colorChar && isReset(phrase.charAt(i + 1))){
                return colorChar + "" + resetChars.charAt(0);
            }
        }
        return "";
    }

}
