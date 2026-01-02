package org.jmanagewallbag;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.modulith.core.ApplicationModules;

public class ArchitectureModulithTests {


    private static final Logger LOGGER = LoggerFactory.getLogger(ArchitectureModulithTests.class);

    @Test
    void createApplicationModuleModel() {
        ApplicationModules modules = ApplicationModules.of(JmanagewallbagApplication.class);
        modules.forEach(x -> LOGGER.info("{}", x));
    }

    @Test
    void verifiesModularStructure() {
        ApplicationModules modules = ApplicationModules.of(JmanagewallbagApplication.class);
        modules.verify();
    }
}
