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

        LOGGER.info("page size: {}", appProperties.getPageSize());
        LOGGER.info("batch insert size: {}", appProperties.getBatchInsertSize());

        Flux<Bookmark> fluxBookmark = Flux.create(this::sendData);

        fluxBookmark
                .doOnNext(x -> nbUrlTotale.incrementAndGet())
                .filter(bookmark -> StringUtils.isNotBlank(bookmark.getUrl()))
                .buffer(appProperties.getBatchInsertSize())
                .subscribe(listBookmark -> {

                    ajouteBookmark(listBookmark, nbAjout, nbModifications);

                });

        var fin = Instant.now();

        LOGGER.info("duree: {} ({})", Duration.between(debut, fin), new DurationFormatter(DurationFormat.Style.COMPOSITE).print(Duration.between(debut, fin), Locale.FRANCE));
        LOGGER.info("nb ajout: {}", nbAjout);
        LOGGER.info("nb modifications: {}", nbModifications);
        LOGGER.info("nb url totale: {}", nbUrlTotale);

        var nb = bookmarkRepository.count();
        LOGGER.info("nb bookmarks en base: {}", nb);
    }

    private void ajouteBookmark(List<Bookmark> listBookmark, AtomicInteger nbAjout, AtomicInteger nbModifications) {

        var modificationRealise = false;

        // suppression des doublons d'url
        List<Bookmark> listBookmark2 = new ArrayList<>();
        for (Bookmark bookmark : listBookmark) {
            if (listBookmark2.stream().noneMatch(b -> b.getUrl().equals(bookmark.getUrl()))) {
                listBookmark2.add(bookmark);
            }
        }

        var listeDejaPresent = bookmarkRepository.findByUrlIn(listBookmark2.stream().map(Bookmark::getUrl).toList());

        List<Bookmark> listeMaj = new ArrayList<>();
        List<Bookmark> listeMaj2 = new ArrayList<>();
        List<Bookmark> listeMaj3 = new ArrayList<>();
        List<Bookmark> listeAjout = new ArrayList<>();
        for (Bookmark bookmark : listBookmark2) {
            var bookmarkOpt = listeDejaPresent.stream()
                    .filter(b -> b.getUrl().equals(bookmark.getUrl()))
                    .findFirst();
            if (bookmarkOpt.isPresent()) {
                listeMaj.add(bookmark);
                listeMaj2.add(bookmarkOpt.get());
            } else {
                listeAjout.add(bookmark);
            }
        }

        if (!listeAjout.isEmpty()) {
            nbAjout.addAndGet(listeMaj.size());
            bookmarkRepository.saveAll(listeAjout);
            modificationRealise = true;
        }

        for (int i = 0; i < listeMaj.size(); i++) {
            var bookmark = listeMaj.get(i);
            if (StringUtils.isNotBlank(bookmark.getTitre()) &&
                    !bookmark.getTitre().equals(listeMaj2.get(i).getTitre())) {
                Bookmark bookmark2 = listeMaj2.get(i);
                bookmark2.setTitre(listeMaj.get(i).getTitre());
                listeMaj3.add(bookmark2);
            }
        }
        if (!listeMaj3.isEmpty()) {
            nbModifications.addAndGet(listeMaj3.size());
            bookmarkRepository.saveAll(listeMaj3);
            modificationRealise = true;
        }

        if (modificationRealise) {
            bookmarkRepository.flush();
        }
    }

    private void sendData(FluxSink<Bookmark> sink) {
        int no = 1;
        int nbPages;
        int max = 1;

        for (int i = 0; i < max; i++) {
            LOGGER.info("page {}", no + i);
            var resOpt = oAuthService.getEntries(no + i, appProperties.getPageSize());

            if (resOpt.isEmpty()) {
                break;
            }
            var res = resOpt.get();
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

}
