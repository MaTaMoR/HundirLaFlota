package me.matamor.hundirlaflota.servidor.usuarios;

import me.matamor.hundirlaflota.servidor.encryptpassword.EncryptPassword;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.util.Arrays;

/**
 * No he podido probar esta clase,
 */

public class UserRegistry {

    private final String encryptPassword;
    private final File folder;

    public UserRegistry(String encryptPassword) {
        this.encryptPassword = encryptPassword;
        this.folder = new File("./jugadores");

        if (!this.folder.exists()) {
            this.folder.mkdir();
        }
    }

    public void registerUsuario(String user, String password) {
        try {
            byte[] encryptedPassword = EncryptPassword.encriptar(password.getBytes(StandardCharsets.UTF_8), this.encryptPassword.getBytes(StandardCharsets.UTF_8));
            File file = new File(this.folder, user + ".bin");
            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                outputStream.write(encryptedPassword);
            }
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    public boolean validLogin(String user, String password) {
        File file = new File(this.folder, user + ".bin");
        if (!file.exists()) {
            System.out.println("not exists");
            return false;
        }

        System.out.println("login");
        System.out.println(user);
        System.out.println(password);

        try {
            byte[] encryptedPassword = Files.readAllBytes(file.toPath());
            byte[] unencryptedPassword = EncryptPassword.desencriptar(encryptedPassword, this.encryptPassword.getBytes(StandardCharsets.UTF_8));

            return Arrays.equals(unencryptedPassword, password.getBytes(StandardCharsets.UTF_8));
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            return false;
        }
    }
}
