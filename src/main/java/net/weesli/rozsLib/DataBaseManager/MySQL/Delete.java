package net.weesli.rozsLib.DataBaseManager.MySQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.stream.Collectors;

public class Delete {

    private Connection connection;
    private String table;
    private Map<String, String> where;

    public Delete(Connection connection, String table, Map<String, String> where) {
        this.table = table;
        this.where = where;
        this.connection = connection;
    }

    public String getSqlQuery() {
        String whereClause = where.entrySet().stream()
                .map(entry -> entry.getKey() + " = ?")
                .collect(Collectors.joining(" AND "));

        return "DELETE FROM " + table + " WHERE " + whereClause + ";";
    }

    public void execute() throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(getSqlQuery())) {

            int index = 1;
            for (String value : where.values()) {
                statement.setString(index++, value);
            }

            statement.executeUpdate();
        }
    }

    private Connection getConnection() {
        return connection;
    }

}
