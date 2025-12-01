package org.jmanagewallbag.service;

import org.jmanagewallbag.dto.OAuthDto;
import org.jmanagewallbag.properties.AppProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Service
public class OAuthService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OAuthService.class);

    private final AppProperties appProperties;

    private RestClient restClient;

    private Optional<OAuthDto> oAuthDtoCurrent = Optional.empty();

    private Optional<Instant> limit = Optional.empty();

    public OAuthService(AppProperties appProperties) {
        this.appProperties = appProperties;
        this.restClient = RestClient.builder()
                .baseUrl(appProperties.getUrl())
                .build();
    }

    public JsonNode getEntries(int noPage, int nbParPage) {

        updateToken();

        String result = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/entries.json")
                        .queryParam("page", noPage)
                        .queryParam("perPage", nbParPage)
                        .queryParam("sort", "created")
                        .queryParam("order", "desc")
                        .build())
                .accept(APPLICATION_JSON)
                .header("Authorization", "Bearer " + oAuthDtoCurrent.get().getAccessToken())
                .retrieve()
                .body(String.class);

        LOGGER.debug("resultat: {}", result);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(result);
    }

    private void updateToken() {
        boolean expired;
        expired = limit.map(instant -> Instant.now().isAfter(instant)).orElse(true);
        if (expired) {
            OAuthDto result = restClient.get()
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
                    .body(OAuthDto.class);

            LOGGER.info("resultat token: {}", result);

            oAuthDtoCurrent = Optional.of(result);
            limit = Optional.of(Instant.now().plus(result.getExpiresIn(), ChronoUnit.SECONDS));
        }
    }

}
