package org.jmanagewallbag.jpa.repository;

import org.jmanagewallbag.jpa.entity.BookmarkFirefox;
import org.jmanagewallbag.jpa.entity.BookmarkPocket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookmarkFirefoxRepository extends JpaRepository<BookmarkFirefox, Long> {

    Optional<BookmarkFirefox> findByUrl(String url);

    List<BookmarkFirefox> findByUrlIn(List<String> url);

}
