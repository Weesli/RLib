package net.weesli.rozsLib.database.builders;

import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Getter@Setter
public class SQLBuilder {

    public Connection build(net.weesli.rozsLib.database.builders.Connection connection){
        return connection.getConnection();
    }

    public static void sendSQL(Connection connection, String sql){
        try(PreparedStatement statement = connection.prepareStatement(sql)){
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
