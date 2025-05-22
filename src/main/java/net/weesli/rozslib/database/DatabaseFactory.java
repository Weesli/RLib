package net.weesli.rozslib.database;

import lombok.SneakyThrows;
import net.weesli.rozslib.enums.DatabaseType;

import java.sql.*;

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
