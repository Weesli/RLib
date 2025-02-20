package net.weesli.rozsLib.database;

import lombok.Getter;
import lombok.SneakyThrows;
import net.weesli.rozsLib.enums.DatabaseType;

import java.net.URI;
import java.sql.*;
import java.util.List;
@Getter
public class Database {

    private ConnectionInfo info;
    private Connection connection;

    @SneakyThrows
    public Database(ConnectionInfo info){
        this.info = info;
        this.connection = DatabaseFactory.createConnection(info);
        if (info.getType().equals(DatabaseType.MySQL)){
            URI uri = new URI(info.getUrl().replace("mysql://", "http://"));
            String dbName = uri.getPath().substring(1);
            try (Connection conn = DriverManager.getConnection(info.getUrl())) {
                String checkDbQuery = "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = ?";
                try (PreparedStatement stmt = conn.prepareStatement(checkDbQuery)) {
                    stmt.setString(1, dbName);
                    ResultSet rs = stmt.executeQuery();

                    if (!rs.next()) {
                        String createDbQuery = "CREATE DATABASE " + dbName;
                        try (Statement stmtCreate = conn.createStatement()) {
                            stmtCreate.executeUpdate(createDbQuery);
                            System.out.println("Database created: " + dbName);
                        }
                    }
                }
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

    public void executeStatement(String sql){
        try (PreparedStatement statement = connection.prepareStatement(sql)){
            statement.execute();
        } catch (Exception e){
            throw new RuntimeException("Error executing statement: " + sql, e);
        }
    }

    public void executePreparedStatement(String sql, List<Object> values){
        try (PreparedStatement statement = connection.prepareStatement(sql)){
            for (int i = 0; i < values.size(); i++){
                statement.setObject(i+1, values.get(i));
            }
            statement.execute();
        } catch (Exception e){
            throw new RuntimeException("Error executing prepared statement: " + sql, e);
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
