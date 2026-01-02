package org.jmanagewallbag.batch.jpa.repository;

import org.jmanagewallbag.batch.jpa.entity.BookmarkText;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookmarkTextRepository extends JpaRepository<BookmarkText, Long> {

    Optional<BookmarkText> findByUrl(String url);

    List<BookmarkText> findByUrlIn(List<String> url);

}
