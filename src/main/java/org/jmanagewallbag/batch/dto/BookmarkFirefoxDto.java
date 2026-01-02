package org.jmanagewallbag.batch.dto;

import java.time.LocalDateTime;
import java.util.List;

public class BookmarkFirefoxDto {

    private String url;
    private String title;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
    private long nbVisites;
    private LocalDateTime dateDerniereVisite;
    private String motCle;
    private List<String> tags;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public LocalDateTime getDateModification() {
        return dateModification;
    }

    public void setDateModification(LocalDateTime dateModification) {
        this.dateModification = dateModification;
    }

    public long getNbVisites() {
        return nbVisites;
    }

    public void setNbVisites(long nbVisites) {
        this.nbVisites = nbVisites;
    }

    public LocalDateTime getDateDerniereVisite() {
        return dateDerniereVisite;
    }

    public void setDateDerniereVisite(LocalDateTime dateDerniereVisite) {
        this.dateDerniereVisite = dateDerniereVisite;
    }

    public String getMotCle() {
        return motCle;
    }

    public void setMotCle(String motCle) {
        this.motCle = motCle;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
