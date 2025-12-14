package org.jmanagewallbag.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jmanagewallbag.jpa.entity.BookmarkPocket;
import org.jmanagewallbag.jpa.repository.BookmarkPocketRepository;
import org.jmanagewallbag.jpa.repository.BookmarkRepository;
import org.jmanagewallbag.properties.AppProperties;
import org.jmanagewallbag.stat.StatGlobal;
import org.jmanagewallbag.stat.StatPocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DurationFormat;
import org.springframework.format.datetime.standard.DurationFormatter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.file.Files;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Enumeration;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Service
public class ComparePocketService {


    private static final Logger LOGGER = LoggerFactory.getLogger(ExportService.class);

    private final AppProperties appProperties;

    private final BookmarkRepository bookmarkRepository;

    private final BookmarkPocketRepository bookmarkPocketRepository;

    public ComparePocketService(AppProperties appProperties, BookmarkRepository bookmarkRepository, BookmarkPocketRepository bookmarkPocketRepository) {
        this.appProperties = appProperties;
        this.bookmarkRepository = bookmarkRepository;
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
            try (ZipFile zipFile = new ZipFile(fichierZip.toFile())) {
                Enumeration<? extends ZipEntry> entries = zipFile.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    // Check if entry is a directory
                    if (!entry.isDirectory()) {
                        LOGGER.info("entry: {}", entry.getName());
                        if (entry.getName().endsWith(".csv")) {
                            String contenuFichier;
                            statPocket.setNbFichiers(statPocket.getNbFichiers() + 1);
                            try (InputStream inputStream = zipFile.getInputStream(entry)) {


                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                IOUtils.copy(inputStream, baos);
                                contenuFichier = baos.toString();
                            }

                            if (StringUtils.isNotBlank(contenuFichier)) {
                                String[] HEADERS = {"title", "url", "time_added", "tags", "status"};
                                CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                                        .setHeader(HEADERS)
                                        .setSkipHeaderRecord(true)
                                        .get();

                                StringReader in = new StringReader(contenuFichier);

                                Iterable<CSVRecord> records = csvFormat.parse(in);

                                for (CSVRecord record : records) {
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

                                    var bookmarckOpt = bookmarkPocketRepository.findByUrl(url);
                                    if (bookmarckOpt.isPresent()) {
                                        LOGGER.debug("bookmark existe deja: {}", url);
                                        statPocket.setNbDejaPresent(statPocket.getNbDejaPresent() + 1);
                                    } else {
                                        BookmarkPocket bookmarkPocket = new BookmarkPocket();
                                        bookmarkPocket.setUrl(url);
                                        bookmarkPocket.setTitre(titre);
                                        bookmarkPocket.setDateCreationPocket(dateCreation);
                                        bookmarkPocketRepository.save(bookmarkPocket);
                                        statPocket.setNbAjout(statPocket.getNbAjout() + 1);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Erreur lors de la lecture du fichier zip", e);
            }

            Instant fin = Instant.now();

            LOGGER.info("duree: {} ({})", Duration.between(debut, fin), new DurationFormatter(DurationFormat.Style.COMPOSITE).print(Duration.between(debut, fin), Locale.FRANCE));

            var nb = bookmarkPocketRepository.count();
            LOGGER.info("nb bookmarks pocket en base: {}", nb);

            statPocket.setDuree(Duration.between(debut, fin));
            statPocket.setNbBase(nb);

        } else {
            LOGGER.warn("rien à comparer");
        }

    }

}
