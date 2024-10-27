package net.weesli.rozsLib.database.component;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
@Getter@Setter
public class Insert implements SQLComponent{

    private String tableName;
    private List<String> keys;
    private List<Object> values;
    private Connection connection;

    public Insert(Connection connection, String tableName, List<String> keys, List<Object> values) {
        this.tableName = tableName;
        this.keys = keys;
        this.values = values;
        this.connection = connection;
    }


    @Override
    public String getSQL() {
        StringBuilder sql = new StringBuilder("INSERT INTO ").append(tableName).append(" (");
        StringBuilder valuePlaceholders = new StringBuilder("VALUES (");

        for (int i = 0; i < keys.size(); i++) {
            sql.append(keys.get(i));
            valuePlaceholders.append("?");

            if (i < keys.size() - 1) {
                sql.append(", ");
                valuePlaceholders.append(", ");
            }
        }

        sql.append(") ");
        valuePlaceholders.append(")");

        return sql.append(valuePlaceholders).toString();
    }

    @SneakyThrows
    @Override
    public void execute() {
        PreparedStatement statement = connection.prepareStatement(getSQL());

        for (int i = 0; i < values.size(); i++) {
            statement.setObject(i + 1, values.get(i));
        }

        statement.executeUpdate();
        statement.close();
    }
}
