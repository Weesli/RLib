package net.weesli.rozslib.database;

import lombok.Getter;
import lombok.SneakyThrows;
import net.weesli.rozslib.enums.DatabaseType;

import java.sql.*;

@Getter
public class Database {

    private ConnectionInfo info;
    private Connection connection;

    @SneakyThrows
    public Database(ConnectionInfo info){
        this.info = info;
        this.connection = DatabaseFactory.createConnection(info);
        if (info.getType().equals(DatabaseType.MySQL)){
            String checkDbQuery = "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = ?";
            try (PreparedStatement stmt = getConnection().prepareStatement(checkDbQuery)) {
                stmt.setString(1, info.getDbName());
                ResultSet rs = stmt.executeQuery();

                if (!rs.next()) {
                    String createDbQuery = "CREATE DATABASE " + info.getDbName();
                    try (Statement stmtCreate = getConnection().createStatement()) {
                        stmtCreate.executeUpdate(createDbQuery);
                        System.out.println("Database created: " + info.getDbName());
                    }
                }
            }
            this.connection = DriverManager.getConnection(info.getUrl() + info.getDbName(), info.getUsername(), info.getPassword());
        }else if (info.getType().equals(DatabaseType.SQLite)){
            // use timeout
            try (Statement stmt = connection.createStatement()){
                stmt.execute("PRAGMA busy_timeout = 10000");
            }
        }
    }

    public void close(){
        try {
            connection.close();
        } catch (Exception e){
            throw new RuntimeException("Error closing database connection", e);
        }
    }

    @SneakyThrows
    public Connection getConnection() {
        if (connection == null || connection.isClosed()){
            return this.connection = DatabaseFactory.createConnection(info);
        }
        return connection;
    }
}
