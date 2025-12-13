package org.jmanagewallbag.executor;

import org.jmanagewallbag.properties.AppProperties;
import org.jmanagewallbag.service.ComparePocketService;
import org.jmanagewallbag.service.ExportService;
import org.jmanagewallbag.service.FirefoxService;
import org.jmanagewallbag.service.ImportTexteService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

@Service
public class RunService implements ApplicationRunner {

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
        if (appProperties.isExportServiceActif()) {
            exportService.export();
        }
        if (appProperties.isCompareServiceActif()) {
            comparePocketService.compare();
        }
        if (appProperties.isImportServiceActif()) {
            importTexteService.importFichiers();
        }
        if (appProperties.isFirefoxActif()) {
            //firefoxService.backup();
            var liste = firefoxService.recupereInfosFirefox();
            firefoxService.insereBase(liste);
        }
    }
}
