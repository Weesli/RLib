package net.weesli.rozsLib.database;

import lombok.Getter;
import java.sql.*;
import java.util.List;

@Getter
public class Database {

    private final Connection connection;

    public Database(ConnectionInfo info){
        this.connection = DatabaseFactory.createConnection(info);
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
}
