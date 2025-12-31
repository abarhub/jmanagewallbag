package org.jmanagewallbag.jpa.repository;

import org.jmanagewallbag.jpa.entity.TagFirefox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagFirefoxRepository extends JpaRepository<TagFirefox, Long> {

    List<TagFirefox> findByTagIn(List<String> url);

}
