package me.matamor.hundirlaflota.servidor;

import me.matamor.hundirlaflota.servidor.encryptpassword.EncryptPassword;
import me.matamor.hundirlaflota.util.Input;

import javax.net.ssl.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.*;
import java.security.cert.CertificateException;

public class Server {

    private static final int PUERTO = 503;
    private static final String DIRECCION = "localhost";
    private static final int BACKLOG = 50;

    private static final File PASSWORD_FILE = new File("./password.bin");
    private static final File SERVER_KEY = new File("./seguridad/server/serverKey.jks");
    private static final File SERVER_TRUST = new File("./seguridad/server/serverTrustedCerts.jks");

    public static void main(String[] args) {
        try {
            String unencryptedPassword = null;
            String encryptPassword = null;

            if (PASSWORD_FILE.exists()) {
                System.out.println("Contraseña de FTP detectada...");
                System.out.println("¿ Deseas seguir usando la contraseña actual o crear una nueva ? (si/no)");
                boolean preguntar = Input.leerPregunta();

                if (preguntar) {
                    do {
                        System.out.println("Introduce la contraseña de encriptación:");
                        encryptPassword = Input.leerLinea();

                        byte[] password = Files.readAllBytes(PASSWORD_FILE.toPath());

                        try {
                            byte[] encryptPasswordBytes = encryptPassword.getBytes(StandardCharsets.UTF_8);
                            unencryptedPassword = new String(EncryptPassword.desencriptar(password, encryptPasswordBytes), StandardCharsets.UTF_8);
                            System.out.println("Contraseña de encriptación correcta!");
                        } catch (GeneralSecurityException | IllegalArgumentException e) {
                            System.out.println("Contraseña de encriptación incorrecta!");
                        }
                    } while (unencryptedPassword == null);
                }
            }

            while (unencryptedPassword == null) {
                System.out.println("Introduce la nueva contraseña del FTP:");
                String password = Input.leerLinea();

                System.out.println("Introduce la contraseña de encriptación (16 caracteres):");
                encryptPassword = Input.leerLinea(16, 16);

                try {
                    byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);
                    byte[] encryptPasswordBytes = encryptPassword.getBytes(StandardCharsets.UTF_8);

                    byte[] encryptedPassword = EncryptPassword.encriptar(passwordBytes, encryptPasswordBytes);
                    try (FileOutputStream outputStream = new FileOutputStream(PASSWORD_FILE)) {
                        outputStream.write(encryptedPassword);
                    }

                    unencryptedPassword = password;
                    System.out.println("Contraseña creada correctamente!");
                } catch (GeneralSecurityException e) {
                    System.out.println("Ha ocurrido un error al encriptar la contraseña!");
                }
            }

            ServerSocket serverSocket = new ServerSocket(PUERTO, BACKLOG, InetAddress.getByName(DIRECCION));

            System.out.printf("Server waiting for clients on '%s:%d'\n",
                    serverSocket.getInetAddress().getHostAddress(), serverSocket.getLocalPort());

            ServerConnectionHandler serverController = new ServerConnectionHandler(serverSocket, new ServerInfo(encryptPassword, unencryptedPassword));
            serverController.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}