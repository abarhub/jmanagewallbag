package org.jmanagewallbag.service;

import org.jmanagewallbag.properties.AppProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DurationFormat;
import org.springframework.format.datetime.standard.DurationFormatter;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class ExportService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExportService.class);

    private final AppProperties appProperties;

    private final OAuthService oAuthService;

    public ExportService(AppProperties appProperties, OAuthService oAuthService) {
        this.appProperties = appProperties;
        this.oAuthService = oAuthService;
    }


    public void export() {

        int no = 1;
        int nbPages = 0;
        int nbUrls = 0;
        int max=10;

        List<String> urls = new ArrayList<>();

        var debut= Instant.now();

        for (int i = 0; i < max; i++) {
            LOGGER.info("page {}", no + i);
            var res = oAuthService.getEntries(no + i, 100);

            if (res == null || res.isEmpty()) {
                break;
            }
            if (i == 0) {
                nbPages = res.get("pages").asInt();
                nbUrls = res.get("total").asInt();
                max=nbPages;
            }

            if (res.has("_embedded")) {
                var embedded = res.get("_embedded");
                if (embedded.has("items")) {
                    var items = embedded.get("items");
                    if (items.isArray() && !items.isEmpty()) {
                        for (var item : items) {

                            var url = item.get("url").asString();

                            urls.add(url);

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

        var fin= Instant.now();

        LOGGER.info("nb pages: {}", nbPages);
        LOGGER.info("nb urls: {}", nbUrls);

        LOGGER.info("nb urls lues: {}", urls.size());

        LOGGER.info("duree: {} ({})", Duration.between(debut, fin), new DurationFormatter(DurationFormat.Style.COMPOSITE).print(Duration.between(debut, fin), Locale.FRANCE));

    }

}
