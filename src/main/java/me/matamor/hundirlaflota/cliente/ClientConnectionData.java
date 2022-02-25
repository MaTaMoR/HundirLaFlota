package me.matamor.hundirlaflota.cliente;

public class ClientConnectionData {

    private final String address;
    private final int port;
    private final String username;
    private final String password;

    public ClientConnectionData(String address, int port, String username, String password) {
        this.address = address;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public String getAddress() {
        return this.address;
    }

    public int getPort() {
        return this.port;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }
}
