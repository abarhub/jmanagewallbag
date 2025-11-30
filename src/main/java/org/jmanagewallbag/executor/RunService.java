package org.jmanagewallbag.executor;

import org.jmanagewallbag.service.ExportService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

@Service
public class RunService implements ApplicationRunner {

    private final ExportService exportService;

    public RunService(ExportService exportService) {
        this.exportService = exportService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        exportService.export();
    }
}
