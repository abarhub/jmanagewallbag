package org.jmanagewallbag.batch.stat;

import java.time.Duration;

public class StatExportWallbag {

    private Duration duree;
    private int nbAjout;
    private int nbModification;
    private int nbTotal;
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

    public int getNbModification() {
        return nbModification;
    }

    public void setNbModification(int nbModification) {
        this.nbModification = nbModification;
    }

    public int getNbTotal() {
        return nbTotal;
    }

    public void setNbTotal(int nbTotal) {
        this.nbTotal = nbTotal;
    }

    public long getNbBase() {
        return nbBase;
    }

    public void setNbBase(long nbBase) {
        this.nbBase = nbBase;
    }

    @Override
    public String toString() {
        return "StatExportWallbag{" +
                "duree=" + duree +
                ", nbAjout=" + nbAjout +
                ", nbModification=" + nbModification +
                ", nbTotal=" + nbTotal +
                ", nbBase=" + nbBase +
                '}';
    }
}
