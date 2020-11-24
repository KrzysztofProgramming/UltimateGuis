package ad.guis.ultimateguis.engine.basics;

import ad.guis.ultimateguis.UltimateGuis;
import ad.guis.ultimateguis.engine.interfaces.Action;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class BasicGui {
    protected Inventory gui;
    protected HashMap<Integer, Action> actions = new HashMap<>();
    protected BasicGui previousGui;
    protected Player viewer;
    private boolean isOpen = false;

    /**
     * @return player who last open this Inventory
     */
    public Player getLastViewer() {
        return viewer;
    }

    /**
     * Pozwala na utworzenie klasy dziedziczącej po Gui, z własnymi konfiguracjami
     */

    /**
     * rejestruje event obsługujący dane gui
     * Zwraca wyjątek gdy rowsAmount jest większe od 6 bądź niedodatnie
     * @param rowsAmount  wielkość gui (inventory)
     * @param title tytuł gui
     */
    public BasicGui(int rowsAmount, String title, BasicGui previousGui) throws IllegalArgumentException {
        this.previousGui = previousGui;
        if(rowsAmount > 6) throw new IllegalArgumentException("Wrong rowsAmount!");
        gui = Bukkit.createInventory(null, rowsAmount * 9, title);
    }

    public BasicGui(int rowsAmount, String title) throws IllegalArgumentException {
        this(rowsAmount,title,null);
    }
    /**
     * Dodaje item w pierwsze wolne miejsce, jeśli takie istnieje
     * UWAGA: może połączyć ten item z innym będącym w gui, jeśli będą TAKIE SAME
     * @param item item do dodania
     * @param action akcja wywołana po klikniąciu itemu
     * @return prawda jeśli item został dodany
     */
    public boolean addItem(ItemStack item, Action action) {
        int firstEmptySlot = gui.firstEmpty();
        if (firstEmptySlot != -1) {
            gui.addItem(item);
            actions.put(firstEmptySlot, action);
            return true;
        }
        return false;
    }

    /**
     * zwraca previous gui, należy sprawdzić czy to nie null
     *
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
                gui.setItem(i,item);
            }
        }
        for(int i = gui.getSize() - 9; i<gui.getSize(); i++){
            if(gui.getItem(i)==null){
                gui.setItem(i,item);
            }
        }
        return true;
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
        if (positionX > 8) return false;
        if (positionY > gui.getSize() / 9 - 1) return false;
        gui.setItem(positionY * 9 + positionX, item);
        if(action!=null) actions.put(positionY * 9 + positionX, action);
        return true;
    }


    protected boolean setItem(int position, ItemStack item, Action action) {
        if (position >= gui.getSize()) return false;
        gui.setItem(position, item);
        if(action!=null) actions.put(position, action);
        return true;
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
        opener.openInventory(gui);
        UltimateGuis.getInstance().getGuiListener().addGui(this);
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
     *
     * @return akcje wykonywane po kliknięciu jakiegoś przedmiotu
     */
    public HashMap<Integer, Action> getActions() {
        return actions;
    }

    boolean isOpen() {
        return !gui.getViewers().isEmpty();
    }

    public int getRowsAmount() {
        return gui.getSize() / 9;
    }

    /**
     * funkcja, która powinna być nadpisywana, jeżeli gui wymaga specjalnego usunięcia przy zamknięciu
     * np. gdy jakieś bardziej wyspecjalizowane gui jest jeszcze zarejestrowane w innym listenerze
     * przykładowo: PlayerActionGui i SingleAreaGui
     */
    public void onClose() {
        this.isOpen = false;
    }


    public static ArrayList<String> splitLoreNicely(String text, int charactersLimit){
       return splitLoreNicely(text, charactersLimit, null);
    }

    public static ArrayList<String> splitLoreNicely(String text, int charactersLimit, String addPrefix) {
        if(addPrefix==null) addPrefix = "";
        ArrayList<String> lore = new ArrayList<>();
        String[] words = text.split(" "); // Get the "words" in the line of text by splitting the space characters
        int wordsUsed = 0; // A counter for how many words have been placed in lines so far
        while (wordsUsed < words.length) { // Repeat this process until all words have been placed into separate lines
            StringBuilder line = new StringBuilder(); // The line that will be added to the lore list
            for (int i = wordsUsed; i < words.length; i++) { // For each remaining word in the array
                if (line.length() + words[i].length() + addPrefix.length() >= charactersLimit) { // If adding the next word exceeds or matches the character limit...
                    line.append(addPrefix).append(words[i]); // Add the last word in the line without a space character
                    wordsUsed++;
                    break; // Break out of this inner loop, since we have reached/exceeded the character limit for this line
                }
                else { // If adding this word does not exceed or match the character limit...
                    line.append(addPrefix).append(words[i]).append(" "); // Add the word with a space character, continue for loop
                    wordsUsed++;
                }
            }
            lore.add(line.toString()); // Add the line of text to the list
        }
        return lore;
    }

    public static List<String> newSplitLoreNicely(String lore, int characterLimit, String prefix){
        if(characterLimit <= 0 ) characterLimit = 1;
        if(prefix == null) prefix = "";
        String[] loreList = lore.split(" ");
        StringBuilder currentColor = new StringBuilder();
        List<String> splitedLore = new LinkedList<>();
        StringBuilder lorePart = new StringBuilder();
        List<Character> actualColorsList = new ArrayList<>();
        int addedColorsLength = 0;

        for(int i=0; i<loreList.length; i++){
            if(lorePart.length() == 0){
                lorePart.append(prefix).append(currentColor.toString()).append(loreList[i]);
                addedColorsLength = currentColor.length();
            }
            else if(lorePart.length() + loreList[i].length() - addedColorsLength > characterLimit){
                splitedLore.add(lorePart.toString());
                lorePart.setLength(0);
                --i;
                continue;
            }
            else{
                lorePart.append(' ').append(loreList[i]);
            }
            for(int j = 0; j < loreList[i].length(); j++){
                if(loreList[i].charAt(j) == '&' &&
                        j + 1 < loreList[i].length() &&
                        !actualColorsList.contains(loreList[i].charAt(j + 1)))//ignore repetitions
                {
                    if(loreList[i].charAt(j + 1) == 'r' || loreList[i].charAt(j + 1) == 'R'){
                        currentColor.setLength(0);
                        actualColorsList.clear();
                        continue;
                    }
                    currentColor.append('&').append(loreList[i].charAt(j + 1));
                    actualColorsList.add(loreList[i].charAt(j + 1));
                }
            }
        }
        splitedLore.add(lorePart.toString());
        return splitedLore;
    }

    public static ItemStack createItem(Material materialType, String name, List<String> lore, short data){
        ItemStack item = new ItemStack(materialType,1,  data);
        ItemMeta meta = item.getItemMeta();
        if (name != null) meta.setDisplayName(name);
        if (lore != null) meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createItem(Material materialType, String name, short data){
       return createItem(materialType, name, null, data);
    }

    public static ItemStack createItem(Material materialType, String name, List<String> lore){
        return createItem(materialType, name, lore, (short) 0);
    }

    public static ItemStack createItem(Material materialType, String name){
         return createItem(materialType, name, null, (short) 0);
    }

    public static ItemStack createBackground(short color){
        ItemStack item = new ItemStack(Material.STAINED_GLASS_PANE, 1, color);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.BOLD + "");
        item.setItemMeta(meta);
        return item;
    }


}
