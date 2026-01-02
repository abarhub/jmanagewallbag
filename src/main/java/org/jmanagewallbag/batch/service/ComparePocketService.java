package org.jmanagewallbag.batch.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jmanagewallbag.batch.dto.CSVLignePocket;
import org.jmanagewallbag.batch.jpa.entity.BookmarkPocket;
import org.jmanagewallbag.batch.jpa.repository.BookmarkPocketRepository;
import org.jmanagewallbag.batch.properties.AppProperties;
import org.jmanagewallbag.batch.stat.StatGlobal;
import org.jmanagewallbag.batch.stat.StatPocket;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DurationFormat;
import org.springframework.format.datetime.standard.DurationFormatter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Service
public class ComparePocketService {


    private static final Logger LOGGER = LoggerFactory.getLogger(ComparePocketService.class);

    private final AppProperties appProperties;

    private final BookmarkPocketRepository bookmarkPocketRepository;

    public ComparePocketService(AppProperties appProperties, BookmarkPocketRepository bookmarkPocketRepository) {
        this.appProperties = appProperties;
        this.bookmarkPocketRepository = bookmarkPocketRepository;
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public void compare(StatGlobal statGlobal) {
        LOGGER.info("Comparaison des bookmarks avec Pocket");

        StatPocket statPocket = new StatPocket();
        statGlobal.setStatPocket(statPocket);

        var fichierZip = appProperties.getFichierPocket();

        if (fichierZip != null && Files.exists(fichierZip)) {
            Instant debut = Instant.now();

            LOGGER.info("fichier zip: {}", fichierZip);

            Flux.create((FluxSink<String> sink) -> lectureZip(sink, fichierZip, statPocket))
                    .flatMap((contenuFichier) -> lectureCSV(contenuFichier, statPocket))
                    .buffer(appProperties.getBatchPocketInsertSize())
                    .map(lignePockets -> enregistrementBase(lignePockets, statPocket))
                    .blockLast();


            Instant fin = Instant.now();

            LOGGER.info("duree: {} ({})", Duration.between(debut, fin), new DurationFormatter(DurationFormat.Style.COMPOSITE).print(Duration.between(debut, fin), Locale.FRANCE));

            var nb = bookmarkPocketRepository.count();
            LOGGER.info("nb bookmarks pocket en base: {}", nb);

            statPocket.setDuree(Duration.between(debut, fin));
            statPocket.setNbBase(nb);
        }
    }


    private void lectureZip(FluxSink<String> sink, Path fichierZip, StatPocket statPocket) {
        try (ZipFile zipFile = new ZipFile(fichierZip.toFile())) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                // Check if entry is a directory
                if (!entry.isDirectory()) {

                    LOGGER.info("entry: {}", entry.getName());
                    if (entry.getName().endsWith(".csv")) {
                        statPocket.setNbFichiers(statPocket.getNbFichiers() + 1);

                        try (InputStream inputStream = zipFile.getInputStream(entry)) {


                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            IOUtils.copy(inputStream, baos);
                            var contenuFichier = baos.toString();

                            if (StringUtils.isNotBlank(contenuFichier)) {
                                sink.next(contenuFichier);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Erreur lors de la lecture du fichier zip", e);
            sink.error(e);
        } finally {
            sink.complete();
        }
    }

    private @NonNull Flux<CSVLignePocket> lectureCSV(String contenuFichier, StatPocket statPocket) {
        String[] HEADERS = {"title", "url", "time_added", "tags", "status"};
        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader(HEADERS)
                .setSkipHeaderRecord(true)
                .get();

        StringReader in = new StringReader(contenuFichier);

        Iterable<CSVRecord> records = null;
        try {
            records = csvFormat.parse(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        int nbLignes = 0;
        List<CSVLignePocket> listeLignes = new ArrayList<>();
        for (CSVRecord record : records) {
            nbLignes++;
            String url = record.get("url");
            LOGGER.debug("url: {}", url);
            String titre = record.get("title");
            String epochStr = record.get("time_added");
            LocalDateTime dateCreation = null;
            if (StringUtils.isNotBlank(epochStr)) {
                Instant instant = Instant.ofEpochSecond(Long.parseLong(epochStr));
                dateCreation = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
            }
            statPocket.setNbUrlTotal(statPocket.getNbUrlTotal() + 1);

            CSVLignePocket csvLignePocket = new CSVLignePocket();
            csvLignePocket.setUrl(url);
            csvLignePocket.setTitle(titre);
            csvLignePocket.setDateCreation(dateCreation);
            listeLignes.add(csvLignePocket);
        }
        LOGGER.info("nb lignes: {}", nbLignes);

        return Flux.fromIterable(listeLignes);
    }

    private String enregistrementBase(List<CSVLignePocket> lignePockets, StatPocket statPocket) {
        var listeUrl = lignePockets.stream().map(CSVLignePocket::getUrl).toList();

        var bookmarckList = bookmarkPocketRepository.findByUrlIn(listeUrl);

        List<BookmarkPocket> listeBookmarkPocket = new ArrayList<>();
        for (var ligne : lignePockets) {

            var existe = bookmarckList.stream().anyMatch(b -> b.getUrl().equals(ligne.getUrl()));
            if (existe) {
                LOGGER.debug("bookmark existe deja: {}", ligne.getUrl());
                statPocket.setNbDejaPresent(statPocket.getNbDejaPresent() + 1);

            } else {

                BookmarkPocket bookmarkPocket = new BookmarkPocket();
                bookmarkPocket.setUrl(ligne.getUrl());
                bookmarkPocket.setTitre(ligne.getTitle());
                bookmarkPocket.setDateCreationPocket(ligne.getDateCreation());
                listeBookmarkPocket.add(bookmarkPocket);
                statPocket.setNbAjout(statPocket.getNbAjout() + 1);
            }

        }

        if (!listeBookmarkPocket.isEmpty()) {
            bookmarkPocketRepository.saveAll(listeBookmarkPocket);
        }
        return "";
    }


}
