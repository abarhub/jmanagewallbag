package org.jmanagewallbag.compactage;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Map;

public class Compactage {

    private static final Logger LOGGER = LoggerFactory.getLogger(Compactage.class);

    public void run(String fichierConfig) throws Exception {
        LOGGER.info("Compactage de la base de donnée ...");
        LOGGER.info("fichier de configuration: {}", fichierConfig);

        if (fichierConfig == null) {
            fichierConfig = "config/application.yml";
        }
        Map<String, Object> config = null;
        config = load(fichierConfig);

        String url = getConfig(config, "spring.datasource.main.jdbc-url");
        String user = getConfig(config, "spring.datasource.main.username");
        String password = getConfig(config, "spring.datasource.main.password");
        String driver = getConfig(config, "spring.datasource.main.driver-class-name");

        if (StringUtils.isBlank(url)) {
            LOGGER.error("L'url est vide");
            throw new Exception("L'url est vide");
        }
        if (StringUtils.isBlank(user)) {
            LOGGER.error("Le user est vide");
            throw new Exception("Le user est vide");
        }
        if (StringUtils.isBlank(driver)) {
            LOGGER.error("Le driver est vide");
            throw new Exception("Le driver est vide");
        }
        Class.forName(driver);

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {

            LOGGER.info("Lancement de la compactage ...");
            stmt.execute("SHUTDOWN COMPACT");
            LOGGER.info("Compactage terminé");
        }
    }

    private Map<String, Object> load(String fichierConfig) throws Exception {

        Path path = Path.of(fichierConfig);

        try (InputStream in = Files.newInputStream(path)) {
            Yaml yaml = new Yaml();
            return yaml.load(in);
        }
    }

    private String getConfig(Map<String, Object> config, String parametre) {
        if (config == null) {
            return null;
        }
        if (config.containsKey(parametre)) {
            return (String) config.get(parametre);
        }
        if (parametre.contains(".")) {
            var pos = parametre.indexOf(".");
            var parametre2 = parametre.substring(pos + 1);
            var map = config.get(parametre.substring(0, pos));
            if (map == null) {
                return null;
            } else if (map instanceof Map) {
                return getConfig((Map<String, Object>) map, parametre2);
            } else if (map instanceof String) {
                return (String) map;
            } else {
                return null;
            }

        }
        return "test";
    }

    //private String parcourtConfig()
}
