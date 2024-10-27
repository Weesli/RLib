package net.weesli.rozsLib.database.component;

import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Map;
import java.util.stream.Collectors;

public class Delete implements SQLComponent{

    private Connection connection;
    private String table;
    private Map<String, String> where;

    public Delete(Connection connection, String table, Map<String, String> where) {
        this.table = table;
        this.where = where;
        this.connection = connection;
    }

    @Override
    public String getSQL() {
        String whereClause = where.keySet().stream()
                .map(s -> s + " = ?")
                .collect(Collectors.joining(" AND "));

        return "DELETE FROM " + table + " WHERE " + whereClause + ";";
    }

    @SneakyThrows
    @Override
    public void execute() {
        try (PreparedStatement statement = connection.prepareStatement(getSQL())) {
            int index = 1;
            for (String value : where.values()) {
                statement.setString(index++, value);
            }

            statement.executeUpdate();
        }
    }

}
