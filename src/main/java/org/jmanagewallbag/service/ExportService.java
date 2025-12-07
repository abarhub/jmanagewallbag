package org.jmanagewallbag.service;

import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.jmanagewallbag.jpa.entity.Bookmark;
import org.jmanagewallbag.jpa.repository.BookmarkRepository;
import org.jmanagewallbag.properties.AppProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DurationFormat;
import org.springframework.format.datetime.standard.DurationFormatter;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ExportService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExportService.class);

    private final AppProperties appProperties;

    private final OAuthService oAuthService;

    private final BookmarkRepository bookmarkRepository;

    public ExportService(AppProperties appProperties, OAuthService oAuthService, BookmarkRepository bookmarkRepository) {
        this.appProperties = appProperties;
        this.oAuthService = oAuthService;
        this.bookmarkRepository = bookmarkRepository;
    }

    @Transactional(rollbackOn = Exception.class)
    public void export() {

        export3();
    }

    public void export3() {

        AtomicInteger nbAjout = new AtomicInteger(0);
        AtomicInteger nbModifications = new AtomicInteger(0);
        AtomicInteger nbUrlTotale = new AtomicInteger(0);

        var debut = Instant.now();

        Flux<Bookmark> fluxBookmark = Flux.create(sink -> {
            sendData(sink);
        });

        fluxBookmark
                .doOnNext(x -> nbUrlTotale.incrementAndGet())
                .filter(bookmark -> StringUtils.isNotBlank(bookmark.getUrl()))
                .buffer(10)
                .subscribe(x -> {
                    for (Bookmark bookmark : x) {
                        var resultatOpt = bookmarkRepository.findByUrl(bookmark.getUrl());

                        if (resultatOpt.isPresent()) {
                            if (StringUtils.isBlank(bookmark.getTitre())) {
                                nbModifications.incrementAndGet();
                                Bookmark bookmark2 = resultatOpt.get();
                                bookmark2.setTitre(bookmark.getTitre());
                                bookmarkRepository.save(bookmark2);
                            }
                        } else {
                            nbAjout.incrementAndGet();
                            bookmarkRepository.save(bookmark);
                        }
                    }
                });

        var fin = Instant.now();

        LOGGER.info("duree: {} ({})", Duration.between(debut, fin), new DurationFormatter(DurationFormat.Style.COMPOSITE).print(Duration.between(debut, fin), Locale.FRANCE));
        LOGGER.info("nb ajout: {}", nbAjout);
        LOGGER.info("nb modifications: {}", nbModifications);
        LOGGER.info("nb url totale: {}", nbUrlTotale);

        var nb = bookmarkRepository.count();
        LOGGER.info("nb bookmarks en base: {}", nb);
    }

    private void sendData(FluxSink<Bookmark> sink) {
        int no = 1;
        int nbPages;
        int max = 1;

        for (int i = 0; i < max; i++) {
            LOGGER.info("page {}", no + i);
            var res = oAuthService.getEntries(no + i, 100);

            if (res == null || res.isEmpty()) {
                break;
            }
            if (i == 0) {
                nbPages = res.get("pages").asInt();
                max = nbPages;
            }

            if (res.has("_embedded")) {
                var embedded = res.get("_embedded");
                if (embedded.has("items")) {
                    var items = embedded.get("items");
                    if (items.isArray() && !items.isEmpty()) {
                        for (var item : items) {

                            if (item.has("url")) {
                                var url = item.get("url").asString();

                                String titre = null;
                                if (item.has("title")) {
                                    titre = item.get("title").asString();
                                }

                                Bookmark bookmark = new Bookmark();
                                bookmark.setUrl(url);
                                bookmark.setTitre(titre);
                                sink.next(bookmark);
                            }
                        }
                    } else {
                        break;
                    }
                } else {
                    break;
                }
            } else {
                break;
            }

        }

        sink.complete();
    }

    public void export2() {

        int no = 1;
        int nbPages = 0;
        int nbUrls = 0;
        int max = 1;
        int nbDoublons = 0;

        List<String> urls = new ArrayList<>();

        var debut = Instant.now();

        for (int i = 0; i < max; i++) {
            LOGGER.info("page {}", no + i);
            var res = oAuthService.getEntries(no + i, 100);

            if (res == null || res.isEmpty()) {
                break;
            }
            if (i == 0) {
                nbPages = res.get("pages").asInt();
                nbUrls = res.get("total").asInt();
                max = nbPages;
            }

            if (res.has("_embedded")) {
                var embedded = res.get("_embedded");
                if (embedded.has("items")) {
                    var items = embedded.get("items");
                    if (items.isArray() && !items.isEmpty()) {
                        for (var item : items) {

                            if (item.has("url")) {
                                var url = item.get("url").asString();

                                String titre = null;
                                if (item.has("title")) {
                                    titre = item.get("title").asString();
                                }

                                if (url.length() > Bookmark.MAX_URL) {
                                    LOGGER.warn("url too long: {}", url);
                                    url = StringUtils.left(url, Bookmark.MAX_URL);
                                }

                                if (titre.length() > Bookmark.MAX_TITRE) {
                                    LOGGER.warn("titre too long: {}", url);
                                    titre = StringUtils.left(titre, Bookmark.MAX_TITRE);
                                }

                                var resultatOpt = bookmarkRepository.findByUrl(url);

                                if (resultatOpt.isPresent()) {
                                    Bookmark bookmark = resultatOpt.get();
                                    bookmark.setTitre(titre);
                                    bookmarkRepository.save(bookmark);
                                } else {
                                    Bookmark bookmark = new Bookmark();
                                    bookmark.setUrl(url);
                                    bookmark.setTitre(titre);
                                    bookmarkRepository.save(bookmark);
                                }

                                if (urls.contains(url)) {
                                    nbDoublons++;
                                    LOGGER.warn("doublon: {}", url);
                                }


                            }
                        }
                    } else {
                        break;
                    }
                } else {
                    break;
                }
            } else {
                break;
            }

        }

        var fin = Instant.now();

        LOGGER.info("nb pages: {}", nbPages);
        LOGGER.info("nb urls: {}", nbUrls);
        LOGGER.info("nb urls doublons: {}", nbDoublons);

        LOGGER.info("nb urls lues: {}", urls.size());

        LOGGER.info("duree: {} ({})", Duration.between(debut, fin), new DurationFormatter(DurationFormat.Style.COMPOSITE).print(Duration.between(debut, fin), Locale.FRANCE));

        var nb = bookmarkRepository.count();
        LOGGER.info("nb bookmarks: {}", nb);
    }

}
