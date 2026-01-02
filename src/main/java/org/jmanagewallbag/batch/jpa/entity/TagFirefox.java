package org.jmanagewallbag.batch.jpa.entity;

import jakarta.persistence.*;

import java.util.Collection;

@Entity
public class TagFirefox {

    public static final int MAX_TAG = 50;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = MAX_TAG)
    private String tag;

    @ManyToMany
    private Collection<BookmarkFirefox> bookmarks;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Collection<BookmarkFirefox> getBookmarks() {
        return bookmarks;
    }

    public void setBookmarks(Collection<BookmarkFirefox> bookmarks) {
        this.bookmarks = bookmarks;
    }
}
