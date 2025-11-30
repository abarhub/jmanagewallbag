package org.jmanagewallbag.service;

import org.jmanagewallbag.properties.AppProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Service
public class ExportService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExportService.class);

    private final AppProperties appProperties;

    public ExportService(AppProperties appProperties) {
        this.appProperties = appProperties;
    }


    public void export() {
        RestClient restClient = RestClient.builder()
                .baseUrl(appProperties.getUrl())
                .build();

        String result = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/oauth/v2/token")
                        .queryParam("grant_type", appProperties.getGrantType())
                        .queryParam("client_id", appProperties.getClientId())
                        .queryParam("client_secret", appProperties.getClientSecret())
                        .queryParam("username", appProperties.getUsername())
                        .queryParam("password", appProperties.getPassword())
                        .build())
                .accept(APPLICATION_JSON)
                .retrieve()
                .body(String.class);

        LOGGER.info("resultat: {}", result);
    }

}
