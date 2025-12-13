package org.jmanagewallbag.jpa.repository;

import org.jmanagewallbag.jpa.entity.BookmarkPocket;
import org.jmanagewallbag.jpa.entity.BookmarkText;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookmarkTextRepository extends JpaRepository<BookmarkText, Long> {

    Optional<BookmarkText> findByUrl(String url);

    List<BookmarkText> findByUrlIn(List<String> url);

}
