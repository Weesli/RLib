package net.weesli.rozsLib.DataBaseManager.MySQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class Update {

    private String tableName;
    private List<String> keys;
    private List<Object> values;
    private Map<String, String> whereClause;

    public Update(String tableName, List<String> keys, List<Object> values, Map<String, String> whereClause) {
        this.tableName = tableName;
        this.keys = keys;
        this.values = values;
        this.whereClause = whereClause;
    }

    public String getSqlQuery() {
        StringBuilder sql = new StringBuilder("UPDATE ").append(tableName).append(" SET ");
        for (int i = 0; i < keys.size(); i++) {
            sql.append(keys.get(i)).append(" = ?");

            if (i < keys.size() - 1) {
                sql.append(", ");
            }
        }
        if (whereClause != null && !whereClause.isEmpty()) {
            sql.append(" WHERE ");
            boolean first = true;
            for (Map.Entry<String, String> entry : whereClause.entrySet()) {
                if (!first) {
                    sql.append(" AND ");
                }
                sql.append(entry.getKey()).append(" = ?");
                first = false;
            }
        }

        return sql.toString();
    }

    public void execute(Connection connection) throws SQLException {
        String query = getSqlQuery();
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            for (int i = 0; i < values.size(); i++) {
                statement.setObject(i + 1, values.get(i));
            }
            int parameterIndex = values.size() + 1;
            for (String value : whereClause.values()) {
                statement.setObject(parameterIndex++, value);
            }

            statement.executeUpdate();
            statement.close();
        }catch (SQLException e){
            System.err.println("SQL error: " + e.getMessage());
            throw e;
        }
    }
}
