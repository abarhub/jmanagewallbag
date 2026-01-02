package org.jmanagewallbag.batch.jpa.repository;

import org.jmanagewallbag.batch.jpa.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    Optional<Bookmark> findByUrl(String url);

    List<Bookmark> findByUrlIn(List<String> url);

}
