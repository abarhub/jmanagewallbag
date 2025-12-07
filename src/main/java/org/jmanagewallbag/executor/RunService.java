package org.jmanagewallbag.executor;

import org.jmanagewallbag.service.ComparePocketService;
import org.jmanagewallbag.service.ExportService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

@Service
public class RunService implements ApplicationRunner {

    private final ExportService exportService;
    private final ComparePocketService comparePocketService;

    public RunService(ExportService exportService, ComparePocketService comparePocketService) {
        this.exportService = exportService;
        this.comparePocketService = comparePocketService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
//        exportService.export();
        comparePocketService.compare();
    }
}
