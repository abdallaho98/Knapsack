package sample;

public class object {
    private int nbr , poid , gain ;

    public object(int nbr ,int poid, int gain) {
        this.poid = poid;
        this.gain = gain;
        this.nbr = nbr;
    }

    public int getPoid() {
        return poid;
    }

    @Override
    public String toString() {
        return "object{" +
                "nbr=" + nbr +
                ", poid=" + poid +
                ", gain=" + gain +
                '}';
    }

    public void setPoid(int poid) {
        this.poid = poid;
    }

    public int getGain() {
        return gain;
    }

    public void setGain(int gain) {
        this.gain = gain;
    }

    public int getNbr() {
        return nbr;
    }

    public void setNbr(int nbr) {
        this.nbr = nbr;
    }
}
