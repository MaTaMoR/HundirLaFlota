package me.matamor.hundirlaflota.servidor.ftp;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.text.SimpleDateFormat;
import java.util.Properties;

public class FTPTask implements Runnable {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");

    private final Properties serverConfig;
    private final String password;
    private final Properties stats;

    public FTPTask(Properties serverConfig, String password, Properties stats) {
        this.serverConfig = serverConfig;
        this.password = password;
        this.stats = stats;
    }

    @Override
    public void run() {
        try {
            FTPClient ftpClient = new FTPClient();

            //Leemos la dirección del servidor y el puerto
            String direccion = this.serverConfig.getProperty("direccion");
            int puerto = Integer.parseInt(this.serverConfig.getProperty("puerto"));

            //Iniciamos la conexión del FTP
            try {
                ftpClient.connect(direccion, puerto);
            } catch (ConnectException e) {
                System.err.println("No se ha podido crear la conexión!");
                return;
            }

            // Comprobamos si el intento de conexión ha sido exitoso antes de seguir
            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                System.err.println("El servidor FTP ha rechazado la conexión");
                return;
            }

            ftpClient.enterLocalPassiveMode(); // modo pasivo

            String username = this.serverConfig.getProperty("username");

            //Iniciamos sesión en el cliente
            if (!ftpClient.login(username, this.password)) {
                ftpClient.disconnect();
                System.err.println("No se ha podido logger en el FTP!");
                return;
            }

            String fecha = DATE_FORMAT.format(System.currentTimeMillis());

            //Intentamos crear la carpeta del dia
            if (!ftpClient.changeWorkingDirectory(fecha)) {
                if (ftpClient.makeDirectory(fecha)) {
                    if (!ftpClient.changeWorkingDirectory(fecha)) {
                        ftpClient.logout();
                        ftpClient.disconnect();
                        System.err.println("No se ha podido mover a la carpeta del día!");
                        return;
                    }
                } else {
                    ftpClient.logout();
                    ftpClient.disconnect();
                    System.err.println("No se ha podido crear la carpeta del día!");
                    return;
                }
            }

            int fileCount = 1;
            boolean exists;

            //Buscamos el nombre de un archivo que no exista!
            do {
               exists = ftpClient.listFiles(fileCount + ".txt").length > 0;

                if (exists) {
                    fileCount = fileCount + 1;
                }
            } while (exists && fileCount < 1000);

            //Comprobamos si se ha pasado
            if (exists) {
                System.out.println("No hay ningún nombre de archivo disponible!");
                return;
            }

            //Escribimos las estadísticas en el servidor
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            this.stats.store(outputStream, null);

            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

            if (ftpClient.storeFile(fileCount + ".txt", inputStream)) {
                System.out.println("Archivo guardado correctamente en el FTP!");
            } else {
                System.out.println("No se ha podido guardar el archivo en el FTP!");
            }

            ftpClient.logout();
            ftpClient.disconnect();
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }
}
