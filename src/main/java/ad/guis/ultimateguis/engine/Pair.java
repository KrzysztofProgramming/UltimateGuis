package ad.guis.ultimateguis.engine;

public class Pair<T, K> {
    private T firsValue;
    private K secondValue;

    public Pair(T firsValue, K secondValue) {
        this.firsValue = firsValue;
        this.secondValue = secondValue;
    }

    public T getFirsValue() {
        return firsValue;
    }

    public void setFirsValue(T firsValue) {
        this.firsValue = firsValue;
    }

    public K getSecondValue() {
        return secondValue;
    }

    public void setSecondValue(K secondValue) {
        this.secondValue = secondValue;
    }
}
