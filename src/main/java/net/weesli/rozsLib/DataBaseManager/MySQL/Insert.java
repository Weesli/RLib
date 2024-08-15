package net.weesli.rozsLib.DataBaseManager.MySQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class Insert {

    private String tableName;
    private List<String> keys;
    private List<Object> values;

    public Insert(String tableName, List<String> keys, List<Object> values) {
        this.tableName = tableName;
        this.keys = keys;
        this.values = values;
    }

    public String getSqlQuery() {
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

    public void execute(Connection connection) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(getSqlQuery());

        for (int i = 0; i < values.size(); i++) {
            statement.setObject(i + 1, values.get(i));
        }

        statement.executeUpdate();
        statement.close();
    }
}
