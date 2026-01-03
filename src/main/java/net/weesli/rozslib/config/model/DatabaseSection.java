package net.weesli.rozslib.config.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import net.weesli.rozslib.database.ConnectionInfo;
import net.weesli.rozslib.database.DatabaseFactory;
import net.weesli.rozslib.enums.DatabaseType;

import java.nio.file.Path;
import java.sql.Connection;
@NoArgsConstructor
@Getter
public class DatabaseSection {
    private String driver;
    private String hostname;
    private Integer port;
    private String username;
    private String password;
    private String database;

    // this method for MySQL
    public Connection toConnection(){
        return DatabaseFactory.createConnection(new ConnectionInfo(
                getEnumType(),
                hostname,
                port,
                username,
                password,
                database
        ),null);
    }

    // this method for SQLite
    public Connection toConnection(Path path){
        return DatabaseFactory.createConnection(new ConnectionInfo(
                getEnumType(),
                hostname,
                port,
                username,
                password,
                database
        ), path);
    }

    private DatabaseType getEnumType(){
        if (driver.toLowerCase().contains("mysql")){
            return DatabaseType.MySQL;
        } else if (driver.toLowerCase().contains("sqlite")) {
            return DatabaseType.SQLite;
        }
        throw new RuntimeException("Unknown database type");
    }
}
