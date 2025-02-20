package net.weesli.rozsLib.database;

import lombok.SneakyThrows;
import net.weesli.rozsLib.enums.DatabaseType;

import java.net.URI;
import java.sql.*;

public class DatabaseFactory {

    @SneakyThrows
    public static Connection createConnection(ConnectionInfo info){
        try {
            if (info.getType().equals(DatabaseType.SQLite)){
                return DriverManager.getConnection(info.getUrl());
            } else if (info.getType().equals(DatabaseType.MySQL)) {
                return DriverManager.getConnection(info.getUrl(), info.getUsername(), info.getPassword());
            }
        }catch (SQLSyntaxErrorException e){
            if (e.getMessage().contains("Unknown database")){
                if (info.getType().equals(DatabaseType.MySQL)){
                    try (Connection conn = DriverManager.getConnection(info.getUrl())) {
                        String checkDbQuery = "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = ?";
                        try (PreparedStatement stmt = conn.prepareStatement(checkDbQuery)) {
                            stmt.setString(1, info.getDbName());
                            ResultSet rs = stmt.executeQuery();

                            if (!rs.next()) {
                                String createDbQuery = "CREATE DATABASE " + info.getDbName();
                                try (Statement stmtCreate = conn.createStatement()) {
                                    stmtCreate.executeUpdate(createDbQuery);
                                    System.out.println("Database created: " + info.getDbName());
                                }
                            }
                        }
                    }
                    return DriverManager.getConnection(info.getUrl(), info.getUsername(), info.getPassword());
                }
            }
        }
        throw new IllegalArgumentException("Unsupported database type: " + info.getType());
    }
}
