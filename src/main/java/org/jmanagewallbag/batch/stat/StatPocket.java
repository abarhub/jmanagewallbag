package org.jmanagewallbag.batch.stat;

import java.time.Duration;

public class StatPocket {
    private Duration duree;
    private int nbUrlTotal;
    private int nbAjout;
    private int nbDejaPresent;
    private long nbBase;
    private int nbFichiers;

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

    public int getNbFichiers() {
        return nbFichiers;
    }

    public void setNbFichiers(int nbFichiers) {
        this.nbFichiers = nbFichiers;
    }

    @Override
    public String toString() {
        return "StatPocket{" +
                "duree=" + duree +
                ", nbUrlTotal=" + nbUrlTotal +
                ", nbAjout=" + nbAjout +
                ", nbDejaPresent=" + nbDejaPresent +
                ", nbBase=" + nbBase +
                ", nbFichiers=" + nbFichiers +
                '}';
    }
}
