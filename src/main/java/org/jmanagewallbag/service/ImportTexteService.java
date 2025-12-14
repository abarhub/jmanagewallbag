package org.jmanagewallbag.service;

import com.google.common.base.CharMatcher;
import org.apache.commons.lang3.StringUtils;
import org.jmanagewallbag.dto.AnalyseFichier;
import org.jmanagewallbag.dto.AnalyseFichierTotal;
import org.jmanagewallbag.jpa.entity.BookmarkText;
import org.jmanagewallbag.jpa.repository.BookmarkTextRepository;
import org.jmanagewallbag.properties.AppProperties;
import org.jmanagewallbag.stat.StatGlobal;
import org.jmanagewallbag.stat.StatImportTexte;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;

@Service
public class ImportTexteService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImportTexteService.class);

    private static final CharMatcher CARACTERE_CONTROLE = CharMatcher.inRange('\00', '\07')
            .or(CharMatcher.inRange('\u000B', '\u000C'))
            .or(CharMatcher.inRange('\u000E', '\u001F'))
            .or(CharMatcher.is('\u007F'));

    private final BookmarkTextRepository bookmarkTextRepository;

    private final AppProperties appProperties;


    public ImportTexteService(BookmarkTextRepository bookmarkTextRepository, AppProperties appProperties) {
        this.bookmarkTextRepository = bookmarkTextRepository;
        this.appProperties = appProperties;
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public void importFichiers(StatGlobal statGlobal) throws IOException {
        AnalyseFichierTotal analyseFichierTotal = new AnalyseFichierTotal();

        StatImportTexte statImportTexte = new StatImportTexte();
        statGlobal.setImportTexte(statImportTexte);

        var debut = Instant.now();

        if (!CollectionUtils.isEmpty(appProperties.getImportRepertoires())) {
            for (var repertoire : appProperties.getImportRepertoires()) {

                LOGGER.info("Analyse du repertoire: {}", repertoire);

                try (var stream = Files.list(repertoire)) {
                    stream.forEach(fichier -> analyseFichier(fichier, analyseFichierTotal));
                }
            }
        }

        var fin = Instant.now();

        LOGGER.info("Analyse finale: nbUrlAjoute:{}, nbUrlDejaPresent:{}, nbFichierIgnore:{}",
                analyseFichierTotal.getNbUrlAjoute(), analyseFichierTotal.getNbUrlDejaPresent(),
                analyseFichierTotal.getNbFichierIgnore());

        statImportTexte.setDuree(Duration.between(debut, fin));
        statImportTexte.setNbAjout(analyseFichierTotal.getNbUrlAjoute());
        statImportTexte.setNbDejaPresent(analyseFichierTotal.getNbUrlDejaPresent());
        statImportTexte.setNbFichierIgnore(analyseFichierTotal.getNbFichierIgnore());
    }

    private void analyseFichier(Path fichier, AnalyseFichierTotal analyseFichierTotal) {
        try {
            try (var stream = Files.lines(fichier)) {

                AnalyseFichier analyseFichier = new AnalyseFichier();

                stream
                        .filter(ligne -> !ligne.isBlank())
                        .takeWhile(x -> !fichierInvalide(x, analyseFichier))
                        .map(String::trim)
                        .filter(ligne -> ligne.startsWith("http://") || ligne.startsWith("https://"))
                        .forEach(url -> ajouteUrl(url, analyseFichier));

                LOGGER.info("analyse du fichier: {} (ignorer:{}, nbAjoute:{}, nbDejaPresent:{})",
                        fichier.getFileName(), analyseFichier.isFichierIgnore(),
                        analyseFichier.getNbUrlAjoute(), analyseFichier.getNbUrlDejaPresent());

                analyseFichierTotal.setNbUrlAjoute(analyseFichierTotal.getNbUrlAjoute() + analyseFichier.getNbUrlAjoute());
                analyseFichierTotal.setNbUrlDejaPresent(analyseFichierTotal.getNbUrlDejaPresent() + analyseFichier.getNbUrlDejaPresent());
                if (analyseFichier.isFichierIgnore()) {
                    analyseFichierTotal.setNbFichierIgnore(analyseFichierTotal.getNbFichierIgnore() + 1);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur pour analyser le fichier " + fichier, e);
        }
    }

    private void ajouteUrl(String url, AnalyseFichier analyseFichier) {
        if (url.length() > BookmarkText.MAX_URL) {
            LOGGER.info("trim de l'url: {}", url);
            url = StringUtils.left(url, BookmarkText.MAX_URL);
        }
        var bookmarkOpt = bookmarkTextRepository.findByUrl(url);
        if (bookmarkOpt.isEmpty()) {
            BookmarkText bookmarkText = new BookmarkText();
            bookmarkText.setUrl(url);
            bookmarkText.setDateCreation(LocalDateTime.now());
            bookmarkTextRepository.save(bookmarkText);
            analyseFichier.setNbUrlAjoute(analyseFichier.getNbUrlAjoute() + 1);
        } else {
            analyseFichier.setNbUrlDejaPresent(analyseFichier.getNbUrlDejaPresent() + 1);
        }
    }

    private boolean fichierInvalide(String ligne, AnalyseFichier analyseFichier) {
        if (ligne.length() > 100 && CARACTERE_CONTROLE.matchesAnyOf(ligne) && CARACTERE_CONTROLE.countIn(ligne) > 10) {
            analyseFichier.setFichierIgnore(true);
            return true;
        }
        return false;
    }
}
