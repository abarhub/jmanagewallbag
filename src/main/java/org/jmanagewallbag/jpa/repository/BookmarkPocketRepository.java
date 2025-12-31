package org.jmanagewallbag.jpa.repository;

import org.jmanagewallbag.jpa.entity.BookmarkPocket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookmarkPocketRepository extends JpaRepository<BookmarkPocket, Long> {

    Optional<BookmarkPocket> findByUrl(String url);

    List<BookmarkPocket> findByUrlIn(List<String> url);

}
