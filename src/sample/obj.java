package sample;

import java.util.Objects;

public class obj {
    private int gain,poids;

    public int getGain() {
        return gain;
    }

    public void setGain(int gain) {
        this.gain = gain;
    }

    public int getPoids() {
        return poids;
    }

    public void setPoids(int poids) {
        this.poids = poids;
    }

    public obj(int gain, int poids) {
        this.gain = gain;
        this.poids = poids;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof obj){
        return this.gain == ((obj)o).getGain() &&
                this.poids == ((obj)o).getPoids();}
                else { return false ;}
    }

    @Override
    public int hashCode() {
        return Objects.hash(gain, poids);
    }
}
