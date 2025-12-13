package org.jmanagewallbag.service;

import org.jmanagewallbag.jpa.entity.BookmarkFirefox;
import org.jmanagewallbag.jpa.repository.BookmarkFirefoxRepository;
import org.jmanagewallbag.properties.AppProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class FirefoxService {


    private static final Logger LOGGER = LoggerFactory.getLogger(FirefoxService.class);

    private final AppProperties appProperties;

    private final JdbcTemplate firefoxJdbc;

    private final BookmarkFirefoxRepository bookmarkFirefoxRepository;

    private TransactionTemplate transactionTemplateFirefox;

    private TransactionTemplate transactionTemplateMain;

    public FirefoxService(AppProperties appProperties,
                          JdbcTemplate firefoxJdbc,
                          BookmarkFirefoxRepository bookmarkFirefoxRepository,
                          PlatformTransactionManager firefoxTxManager,
                          PlatformTransactionManager mainTxManager) {
        this.appProperties = appProperties;
        this.firefoxJdbc = firefoxJdbc;
        this.bookmarkFirefoxRepository = bookmarkFirefoxRepository;
        transactionTemplateFirefox = new TransactionTemplate(firefoxTxManager);
        transactionTemplateMain = new TransactionTemplate(mainTxManager);
    }

    //@Transactional(transactionManager = "mainTxManager", rollbackFor = Exception.class)
    public void backup() {
        LOGGER.info("Backup Firefox");

    }

    @Transactional(transactionManager = "firefoxTxManager", rollbackFor = Exception.class)
    public List<Object[]> recupereInfosFirefox() {

        List<Object[]> liste = new ArrayList<>();

//        transactionTemplateFirefox.execute(status -> {
            LOGGER.info("Requete Firefox");
            firefoxJdbc.query("""                        
                            
                                SELECT
                              b.id,
                              b.title,
                              p.url,
                              b.dateAdded
                            FROM moz_bookmarks b
                            JOIN moz_places p ON b.fk = p.id
                            WHERE b.type = 1
                            ORDER BY b.dateAdded;
                            
                            """,
                    rs
                            -> {
                        String title
                                = rs.getString("title");
                        String url = rs.getString("url");
                        String date = rs.getString("dateAdded");
                        var date2 = rs.getDate("dateAdded");
                        Instant instant =
                                Instant.ofEpochMilli(date2.getTime() / 1000);
                        LOGGER.info(
                                "title: {}, url: {}, date:{} ({},{})", title, url,
                                date, date2, instant);

                        liste.add(new Object[]{title, url, instant});


                    });

            LOGGER.info("Requete Firefox OK");

//            return "Ref-1";
//        });

        return liste;
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public void insereBase(List<Object[]> liste) {

//        transactionTemplateFirefox.execute(status -> {
            LOGGER.info("insertion base");
            for (Object[] obj : liste) {
                if (obj != null && obj.length == 3) {
                    String title = (String) obj[0];
                    String url = (String) obj[1];
                    Instant instant = (Instant) obj[2];
                    ajouteBookmark(url, title, instant);
                }
            }

            LOGGER.info("insertion base ok");

//            return "Ref-2";
//        });


    }

    private void ajouteBookmark(String url, String title, Instant instant) {
        var bookmarkFirefoxOpt = bookmarkFirefoxRepository.findByUrl(url);
        if (bookmarkFirefoxOpt.isEmpty()) {
            LOGGER.info("ajout de l'url: {}", url);
            var bookmarkFirefox = new BookmarkFirefox();
            bookmarkFirefox.setUrl(url);
            bookmarkFirefox.setTitre(title);
            bookmarkFirefox.setDateCreationPocket(LocalDateTime.ofInstant(instant, ZoneId.systemDefault()));
            bookmarkFirefoxRepository.save(bookmarkFirefox);
            LOGGER.info("ajout de l'url: {} OK", url);
        } else {
            LOGGER.info("url déjà en base: {}", url);
        }
    }
}
