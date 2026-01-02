package org.jmanagewallbag.batch.dto;

public class AnalyseFichier {

    private String nomFichier;
    private int nbUrlAjoute;
    private int nbUrlDejaPresent;
    private boolean fichierIgnore;

    public String getNomFichier() {
        return nomFichier;
    }

    public void setNomFichier(String nomFichier) {
        this.nomFichier = nomFichier;
    }

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

    public boolean isFichierIgnore() {
        return fichierIgnore;
    }

    public void setFichierIgnore(boolean fichierIgnore) {
        this.fichierIgnore = fichierIgnore;
    }
}
