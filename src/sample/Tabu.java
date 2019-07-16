package sample;

import java.util.Arrays;
import java.util.Objects;

public class Tabu {
    private int cout;
    private int[] objects;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tabu tabu = (Tabu) o;
        return cout == tabu.cout &&
                Arrays.equals(objects, tabu.objects);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(cout);
        result = 31 * result + Arrays.hashCode(objects);
        return result;
    }

    public Tabu(int cout, int[] objects) {
        this.cout = cout;
        this.objects = objects;
    }

    public Tabu() {
    }

    public int getCout() {
        return cout;
    }

    public void setCout(int cout) {
        this.cout = cout;
    }

    public int[] getObjects() {
        return objects;
    }

    public void setObjects(int[] objects) {
        this.objects = objects;
    }
}
