package ad.guis.ultimateguis.engine.basics;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

public class ModifiableGui extends BasicGui {

    @Setter
    @Getter
    protected Set<Integer> modifiableSlots = new HashSet<>();

    public ModifiableGui(int rowsAmount, String title, BasicGui previousGui) throws IllegalArgumentException {
        super(rowsAmount, title, previousGui);
    }

    public void addModifiableSlots(int slot){
        if(slot >= this.gui.getSize() || slot < 0) return;
    }

    public void setModifiableSlots(int start, int end){
        if(start < 0) start = 0;
        if(end > this.gui.getSize()) end = this.gui.getSize();
        for(int i=start; i<end; i++){
            this.modifiableSlots.add(i);
        }
    }

    public void clearModifiableSlots(){
        this.modifiableSlots.clear();
    }
}
