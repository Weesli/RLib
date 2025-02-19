package net.weesli.rozsLib.database;

import lombok.SneakyThrows;
import net.weesli.rozsLib.enums.DatabaseType;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseFactory {

    @SneakyThrows
    public static Connection createConnection(ConnectionInfo info){
        if (info.getType().equals(DatabaseType.SQLite)){
            return DriverManager.getConnection(info.getUrl());
        } else if (info.getType().equals(DatabaseType.MySQL)) {
            return DriverManager.getConnection(info.getUrl(), info.getUsername(), info.getPassword());
        }
        throw new IllegalArgumentException("Unsupported database type: " + info.getType());
    }
}
