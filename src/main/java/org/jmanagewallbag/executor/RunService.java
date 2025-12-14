package org.jmanagewallbag.executor;

import org.jmanagewallbag.properties.AppProperties;
import org.jmanagewallbag.service.ComparePocketService;
import org.jmanagewallbag.service.ExportService;
import org.jmanagewallbag.service.FirefoxService;
import org.jmanagewallbag.service.ImportTexteService;
import org.jmanagewallbag.stat.StatGlobal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

@Service
public class RunService implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(RunService.class);

    private final ExportService exportService;
    private final ComparePocketService comparePocketService;
    private final ImportTexteService importTexteService;
    private final AppProperties appProperties;
    private final FirefoxService firefoxService;

    public RunService(ExportService exportService, ComparePocketService comparePocketService,
                      ImportTexteService importTexteService, AppProperties appProperties, FirefoxService firefoxService) {
        this.exportService = exportService;
        this.comparePocketService = comparePocketService;
        this.importTexteService = importTexteService;
        this.appProperties = appProperties;
        this.firefoxService = firefoxService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        StatGlobal statGlobal = new StatGlobal();
        if (appProperties.isExportServiceActif()) {
            exportService.export(statGlobal);
        }
        if (appProperties.isCompareServiceActif()) {
            comparePocketService.compare(statGlobal);
        }
        if (appProperties.isImportServiceActif()) {
            importTexteService.importFichiers(statGlobal);
        }
        if (appProperties.isFirefoxActif()) {
            firefoxService.backup(statGlobal);
        }
        LOGGER.info("stat global: {}", statGlobal);
    }
}
