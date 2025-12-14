package org.jmanagewallbag.jpa.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Collection;

@Entity
public class BookmarkFirefox {

    public static final int MAX_URL = 2000;
    public static final int MAX_TITRE = 2000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = MAX_URL)
    @Column(unique = true, length = MAX_URL, nullable = false)
    private String url;

    @Size(max = MAX_TITRE)
    @Column(length = MAX_TITRE)
    private String titre;

    @CreationTimestamp
    private LocalDateTime dateCreation;

    @UpdateTimestamp
    private LocalDateTime dateModification;

    @Column
    private long nbVisites;

    @Column
    private LocalDateTime dateDerniereVisite;

    @Column
    private String motsCles;

    @ManyToMany
    private Collection<TagFirefox> tags;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
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

    public String getMotsCles() {
        return motsCles;
    }

    public void setMotsCles(String motscles) {
        this.motsCles = motscles;
    }

    public Collection<TagFirefox> getTags() {
        return tags;
    }

    public void setTags(Collection<TagFirefox> tags) {
        this.tags = tags;
    }
}
