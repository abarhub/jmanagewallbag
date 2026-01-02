package org.jmanagewallbag.batch.dto;

public class AnalyseFichierTotal {

    private int nbUrlAjoute;
    private int nbUrlDejaPresent;
    private int nbFichierIgnore;

    public int getNbUrlAjoute() {
        return nbUrlAjoute;
    }

    public void setNbUrlAjoute(int nbUrlAjoute) {
        this.nbUrlAjoute = nbUrlAjoute;
    }

    public int getNbUrlDejaPresent() {
        return nbUrlDejaPresent;
    }

    public void setNbUrlDejaPresent(int nbUrlDejaPresent) {
        this.nbUrlDejaPresent = nbUrlDejaPresent;
    }

    public int getNbFichierIgnore() {
        return nbFichierIgnore;
    }

    public void setNbFichierIgnore(int nbFichierIgnore) {
        this.nbFichierIgnore = nbFichierIgnore;
    }
}
