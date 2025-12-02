package org.jmanagewallbag.jpa.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

@Entity
public class Bookmark {

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
}
