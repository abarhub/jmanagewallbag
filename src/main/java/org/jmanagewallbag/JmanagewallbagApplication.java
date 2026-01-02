package org.jmanagewallbag;

import org.jmanagewallbag.compactage.Compactage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JmanagewallbagApplication {

    public static void main(String[] args) throws Exception {
        if (compactageAFaire(args)) {
            compactage(args);
        } else {
            SpringApplication.run(JmanagewallbagApplication.class, args);
        }
    }

    private static void compactage(String[] args) throws Exception {
        String fichierConfig = null;
        if (args != null && args.length > 1) {
            fichierConfig = args[1];
        }
        var compactage = new Compactage();
        compactage.run(fichierConfig);
    }

    private static boolean compactageAFaire(String[] args) {
        if (args != null && args.length > 0) {
            return args[0].equals("compactage");
        }
        return false;
    }

}
