package me.matamor.hundirlaflota.servidor;

import java.util.Objects;

public class ServerInfo {

    private final String encryptPassword;
    private final String ftpPassword;

    public ServerInfo(String encryptPassword, String ftpPassword) {
        this.encryptPassword = encryptPassword;
        this.ftpPassword = ftpPassword;
    }

    public String getEncryptPassword() {
        return encryptPassword;
    }

    public String getFtpPassword() {
        return ftpPassword;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerInfo that = (ServerInfo) o;
        return Objects.equals(encryptPassword, that.encryptPassword) && Objects.equals(ftpPassword, that.ftpPassword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(encryptPassword, ftpPassword);
    }

    @Override
    public String toString() {
        return "ServerInfo{" +
                "encryptPassword='" + encryptPassword + '\'' +
                ", ftpPassword='" + ftpPassword + '\'' +
                '}';
    }
}
