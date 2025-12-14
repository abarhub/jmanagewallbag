package org.jmanagewallbag.service;

import com.google.common.base.Splitter;
import org.jmanagewallbag.dto.BookmarkFirefoxDto;
import org.jmanagewallbag.jpa.entity.BookmarkFirefox;
import org.jmanagewallbag.jpa.entity.TagFirefox;
import org.jmanagewallbag.jpa.repository.BookmarkFirefoxRepository;
import org.jmanagewallbag.jpa.repository.TagFirefoxRepository;
import org.jmanagewallbag.properties.AppProperties;
import org.jmanagewallbag.stat.StatFirefox;
import org.jmanagewallbag.stat.StatGlobal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class FirefoxService {


    private static final Logger LOGGER = LoggerFactory.getLogger(FirefoxService.class);

    private static final Splitter SPLITTER = Splitter.on(',').trimResults().omitEmptyStrings();

    private final AppProperties appProperties;

    private final JdbcTemplate firefoxJdbc;

    private final BookmarkFirefoxRepository bookmarkFirefoxRepository;

    private final TagFirefoxRepository tagFirefoxRepository;

    private TransactionTemplate transactionTemplateFirefox;

    private TransactionTemplate transactionTemplateMain;

    public FirefoxService(AppProperties appProperties,
                          JdbcTemplate firefoxJdbc,
                          BookmarkFirefoxRepository bookmarkFirefoxRepository, TagFirefoxRepository tagFirefoxRepository,
                          PlatformTransactionManager firefoxTxManager,
                          PlatformTransactionManager mainTxManager) {
        this.appProperties = appProperties;
        this.firefoxJdbc = firefoxJdbc;
        this.bookmarkFirefoxRepository = bookmarkFirefoxRepository;
        this.tagFirefoxRepository = tagFirefoxRepository;
        transactionTemplateFirefox = new TransactionTemplate(firefoxTxManager);
        transactionTemplateMain = new TransactionTemplate(mainTxManager);
    }

    public void backup(StatGlobal statGlobal) {
        LOGGER.info("Backup Firefox");
        StatFirefox statFirefox = new StatFirefox();
        statGlobal.setFirefox(statFirefox);

        var debut = Instant.now();

        var liste = recupereInfosFirefox(statFirefox);
        insereBase(liste, statFirefox);

        var fin = Instant.now();
        statFirefox.setDuree(Duration.between(debut, fin));
    }

    private Flux<BookmarkFirefoxDto> recupereInfosFirefox(StatFirefox statFirefox) {

        Flux<BookmarkFirefoxDto> flux;

        flux = transactionTemplateFirefox.execute(status ->
                Flux.create((sink) -> {

                    LOGGER.info("Requete Firefox");

                    firefoxJdbc.query("""                        
                                    SELECT
                                        p.url,
                                        (select b.title from moz_bookmarks b where b.fk=p.id and b.type=1 and b.title is not null) AS bookmark_title,
                                        (select min(b.dateAdded) from moz_bookmarks b where b.fk=p.id and b.type=1) AS date_added,
                                        (select max(b.lastModified) from moz_bookmarks b where b.fk=p.id and b.type=1) AS last_modified,
                                        p.visit_count                         AS visit_count,
                                        p.last_visit_date                     AS last_visit_date,
                                        (select GROUP_CONCAT(tag.title, ', ') from moz_bookmarks tag, moz_bookmarks b
                                         where b.parent=tag.id and tag.type=2 and tag.fk is null and tag.id>80
                                           and b.fk=p.id and b.type=1) AS tags,
                                        (select GROUP_CONCAT(k.keyword, ', ') from moz_keywords k, moz_bookmarks b
                                         where k.id = b.keyword_id and b.fk=p.id and b.type=1) AS keywords
                                    FROM moz_places p
                                    where bookmark_title is not null
                                    ORDER BY p.url, bookmark_title, date_added;
                                    """,
                            rs
                                    -> {
                                String title = rs.getString("bookmark_title");
                                String url = rs.getString("url");
                                var dateCreation = getDate(rs, "date_added");
                                var dateModification = getDate(rs, "last_modified");
                                long nbVisites = rs.getLong("visit_count");
                                var dateDerniereVisite = getDate(rs, "last_visit_date");
                                String motCle = rs.getString("keywords");
                                List<String> tags = rs.getString("tags") == null ? List.of() : SPLITTER.splitToList(rs.getString("tags"));
                                LOGGER.debug("title: {}, url: {}, date:{}, dateModif:{}, nbVisites:{}, dateDerniereVisite:{}, motCle:{}, tags: {}",
                                        title, url, dateCreation, dateModification,
                                        nbVisites, dateDerniereVisite, motCle, tags);

                                BookmarkFirefoxDto dto = new BookmarkFirefoxDto();
                                dto.setTitle(title);
                                dto.setUrl(url);
                                dto.setDateCreation(dateCreation);
                                dto.setDateModification(dateModification);
                                dto.setNbVisites(nbVisites);
                                dto.setDateDerniereVisite(dateDerniereVisite);
                                dto.setMotCle(motCle);
                                dto.setTags(tags);

                                sink.next(dto);

                                statFirefox.setNbUrlTotal(statFirefox.getNbUrlTotal() + 1);

                            });

                    sink.complete();

                    LOGGER.info("Requete Firefox OK");
                }));

        return flux;
    }

    private LocalDateTime getDate(ResultSet rs, String columnName) throws SQLException {
        var date2 = rs.getDate(columnName);
        if (date2 == null) {
            return null;
        } else {
            Instant instant =
                    Instant.ofEpochMilli(date2.getTime() / 1000);
            return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        }
    }

    private void insereBase(Flux<BookmarkFirefoxDto> flux, StatFirefox statFirefox) {


        LOGGER.info("insertion base");
        flux
                .filter(obj -> obj != null)
                .concatMap(obj -> Mono.fromCallable(() -> {
                    transactionTemplateMain.execute(status -> {
                        ajouteBookmark(obj, statFirefox);
                        return null;
                    });
                    return obj;
                }))
                .then()
                .block();

        LOGGER.info("insertion base ok");

        long nbElement = bookmarkFirefoxRepository.count();

        LOGGER.info("nb element en base: {}", nbElement);

        statFirefox.setNbBase(nbElement);
    }

    private void ajouteBookmark(BookmarkFirefoxDto obj, StatFirefox statFirefox) {
        var bookmarkFirefoxOpt = bookmarkFirefoxRepository.findByUrl(obj.getUrl());
        if (bookmarkFirefoxOpt.isEmpty()) {
            //LOGGER.info("ajout de l'url: {}", url);
            var bookmarkFirefox = new BookmarkFirefox();
            bookmarkFirefox.setUrl(obj.getUrl());
            bookmarkFirefox.setTitre(obj.getTitle());
            bookmarkFirefox.setDateCreation(obj.getDateCreation());
            bookmarkFirefox.setDateModification(obj.getDateModification());
            bookmarkFirefox.setNbVisites(obj.getNbVisites());
            bookmarkFirefox.setDateDerniereVisite(obj.getDateDerniereVisite());
            bookmarkFirefox.setMotsCles(obj.getMotCle());
            bookmarkFirefox.setTags(getTags(obj.getTags()));
            bookmarkFirefoxRepository.save(bookmarkFirefox);
            LOGGER.debug("ajout de l'url: {} OK", obj.getUrl());
            statFirefox.setNbAjout(statFirefox.getNbAjout() + 1);
        } else {
            LOGGER.debug("url déjà en base: {}", obj.getUrl());
            statFirefox.setNbDejaPresent(statFirefox.getNbDejaPresent() + 1);
        }
    }

    private Collection<TagFirefox> getTags(List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return List.of();
        }
        var liste = tagFirefoxRepository.findByTagIn(tags);
        if (liste == null) {
            liste = new ArrayList<>();
        }
        for (String tag : tags) {
            var existe = liste.stream().anyMatch(t -> t.getTag().equals(tag));
            if (!existe) {
                TagFirefox tagFirefox = new TagFirefox();
                tagFirefox.setTag(tag);
                tagFirefox = tagFirefoxRepository.save(tagFirefox);
                liste.add(tagFirefox);
            }
        }
        return liste;
    }
}
