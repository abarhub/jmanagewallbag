package org.jmanagewallbag.batch.stat;

import java.time.Duration;

public class StatFirefox {

    private Duration duree;
    private int nbUrlTotal;
    private int nbAjout;
    private int nbDejaPresent;
    private long nbBase;

    public Duration getDuree() {
        return duree;
    }

    public void setDuree(Duration duree) {
        this.duree = duree;
    }

    public int getNbUrlTotal() {
        return nbUrlTotal;
    }

    public void setNbUrlTotal(int nbUrlTotal) {
        this.nbUrlTotal = nbUrlTotal;
    }

    public int getNbAjout() {
        return nbAjout;
    }

    public void setNbAjout(int nbAjout) {
        this.nbAjout = nbAjout;
    }

    public int getNbDejaPresent() {
        return nbDejaPresent;
    }

    public void setNbDejaPresent(int nbDejaPresent) {
        this.nbDejaPresent = nbDejaPresent;
    }

    public long getNbBase() {
        return nbBase;
    }

    public void setNbBase(long nbBase) {
        this.nbBase = nbBase;
    }

    @Override
    public String toString() {
        return "StatFirefox{" +
                "duree=" + duree +
                ", nbUrlTotal=" + nbUrlTotal +
                ", nbAjout=" + nbAjout +
                ", nbDejaPresent=" + nbDejaPresent +
                ", nbBase=" + nbBase +
                '}';
    }
}
