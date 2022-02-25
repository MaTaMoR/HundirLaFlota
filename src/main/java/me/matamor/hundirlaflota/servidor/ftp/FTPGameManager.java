package me.matamor.hundirlaflota.servidor.ftp;

import me.matamor.hundirlaflota.servidor.ServerConnectionHandler;
import me.matamor.hundirlaflota.tasks.TaskExecutor;

import java.io.IOException;
import java.util.Properties;

public class FTPGameManager {

    private final TaskExecutor taskExecutor;
    private final ServerConnectionHandler connectionHandler;

    public FTPGameManager(ServerConnectionHandler connectionHandler) {
        this.taskExecutor = new TaskExecutor();
        this.connectionHandler = connectionHandler;

        this.taskExecutor.start();
    }

    public Properties readFTPConfig() throws IOException {
        Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream("/server.conf"));
        return properties;
    }

    public void saveToFTP(Properties gameStats) {
        try {
            //Leemos la información del FTP
            Properties gameConfig = readFTPConfig();

            FTPTask task = new FTPTask(gameConfig, this.connectionHandler.getServerInfo().getFtpPassword(), gameStats);
            this.taskExecutor.runTask(task, 1);
        } catch (IOException e) {
            System.out.println("No se ha podido leer la información FTP del servidor!");
        }
    }
}
