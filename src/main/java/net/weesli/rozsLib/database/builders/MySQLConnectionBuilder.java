package net.weesli.rozsLib.database.builders;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.DriverManager;
@Getter@Setter
public class MySQLConnectionBuilder extends net.weesli.rozsLib.database.builders.Connection {

    private String host;
    private int port;
    private String database;
    private String username;
    private String password;

    public MySQLConnectionBuilder(String host, int port, String database, String username, String password){
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }


    @SneakyThrows
    @Override
    public Connection getConnection() {
        return DriverManager.getConnection(
                "jdbc:mysql://" + host + ":" + port + "/" + database,
                username,
                password
        );
    }
}
