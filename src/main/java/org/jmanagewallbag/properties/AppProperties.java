package org.jmanagewallbag.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;
import java.util.List;

@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private String url;
    private String grantType;
    private String clientId;
    private String clientSecret;
    private String username;
    private String password;
    private int pageSize;
    private int batchInsertSize;
    private boolean exportServiceActif;
    private Path fichierPocket;
    private boolean compareServiceActif;
    private List<Path> importRepertoires;
    private boolean importServiceActif;
    private boolean firefoxActif;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getGrantType() {
        return grantType;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getBatchInsertSize() {
        return batchInsertSize;
    }

    public void setBatchInsertSize(int batchInsertSize) {
        this.batchInsertSize = batchInsertSize;
    }

    public Path getFichierPocket() {
        return fichierPocket;
    }

    public void setFichierPocket(Path fichierPocket) {
        this.fichierPocket = fichierPocket;
    }

    public boolean isExportServiceActif() {
        return exportServiceActif;
    }

    public void setExportServiceActif(boolean exportServiceActif) {
        this.exportServiceActif = exportServiceActif;
    }

    public boolean isCompareServiceActif() {
        return compareServiceActif;
    }

    public void setCompareServiceActif(boolean compareServiceActif) {
        this.compareServiceActif = compareServiceActif;
    }

    public boolean isImportServiceActif() {
        return importServiceActif;
    }

    public void setImportServiceActif(boolean importServiceActif) {
        this.importServiceActif = importServiceActif;
    }

    public List<Path> getImportRepertoires() {
        return importRepertoires;
    }

    public void setImportRepertoires(List<Path> importRepertoires) {
        this.importRepertoires = importRepertoires;
    }

    public boolean isFirefoxActif() {
        return firefoxActif;
    }

    public void setFirefoxActif(boolean firefoxActif) {
        this.firefoxActif = firefoxActif;
    }
}
