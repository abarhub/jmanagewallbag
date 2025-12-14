package org.jmanagewallbag.stat;

import java.time.Duration;

public class StatImportTexte {

    private Duration duree;
    private int nbAjout;
    private int nbDejaPresent;
    private int nbFichierIgnore;
    private long nbBase;

    public Duration getDuree() {
        return duree;
    }

    public void setDuree(Duration duree) {
        this.duree = duree;
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

    public int getNbFichierIgnore() {
        return nbFichierIgnore;
    }

    public void setNbFichierIgnore(int nbFichierIgnore) {
        this.nbFichierIgnore = nbFichierIgnore;
    }

    public long getNbBase() {
        return nbBase;
    }

    public void setNbBase(long nbBase) {
        this.nbBase = nbBase;
    }

    @Override
    public String toString() {
        return "StatImportTexte{" +
                "duree=" + duree +
                ", nbAjout=" + nbAjout +
                ", nbDejaPresent=" + nbDejaPresent +
                ", nbFichierIgnore=" + nbFichierIgnore +
                ", nbBase=" + nbBase +
                '}';
    }
}
