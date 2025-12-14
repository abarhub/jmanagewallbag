package org.jmanagewallbag.stat;

import java.time.Duration;

public class StatGlobal {
    private StatExportWallbag exportWallbag;

    private StatPocket statPocket;

    private StatImportTexte importTexte;

    private StatFirefox firefox;

    private Duration duree;

    public StatExportWallbag getExportWallbag() {
        return exportWallbag;
    }

    public void setExportWallbag(StatExportWallbag exportWallbag) {
        this.exportWallbag = exportWallbag;
    }

    public StatPocket getStatPocket() {
        return statPocket;
    }

    public void setStatPocket(StatPocket statPocket) {
        this.statPocket = statPocket;
    }

    public StatImportTexte getImportTexte() {
        return importTexte;
    }

    public void setImportTexte(StatImportTexte importTexte) {
        this.importTexte = importTexte;
    }

    public StatFirefox getFirefox() {
        return firefox;
    }

    public void setFirefox(StatFirefox firefox) {
        this.firefox = firefox;
    }

    public Duration getDuree() {
        return duree;
    }

    public void setDuree(Duration duree) {
        this.duree = duree;
    }

    @Override
    public String toString() {
        return "StatGlobal{" +
                "exportWallbag=" + exportWallbag +
                ", statPocket=" + statPocket +
                ", importTexte=" + importTexte +
                ", firefox=" + firefox +
                ", duree=" + duree +
                '}';
    }
}
