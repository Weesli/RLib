package net.weesli.rozsLib.DataBaseManager.MySQL;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public class SQLItemStackList {

    private Connection connection;
    private String table;
    private Map<String, String> where;
    private String itemColumn;

    public SQLItemStackList(Connection connection, String table, Map<String, String> where, String itemColumn) {
        this.connection = connection;
        this.table = table;
        this.where = where;
        this.itemColumn = itemColumn;
    }

    public List<ItemStack> deserialize() {
        List<ItemStack> itemStacks = new ArrayList<>();
        String value = fetchValueFromSQL();

        if (value == null || value.isEmpty()) {
            return itemStacks;
        }

        String[] values = value.replace("[", "").replace("]", "").split(", ");

        for (String val : values) {
            try {
                byte[] itemBytes = Base64.getDecoder().decode(val);
                ByteArrayInputStream in = new ByteArrayInputStream(itemBytes);
                try (BukkitObjectInputStream is = new BukkitObjectInputStream(in)) {
                    ItemStack itemStack = (ItemStack) is.readObject();
                    itemStacks.add(itemStack);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace(); // Log the exception to understand what's going wrong
            }
        }

        return itemStacks;
    }

    private String fetchValueFromSQL() {
        StringBuilder query = new StringBuilder("SELECT ").append(itemColumn).append(" FROM ").append(table);
        StringBuilder whereClause = new StringBuilder(" WHERE ");

        int index = 0;
        for (String key : where.keySet()) {
            if (index > 0) {
                whereClause.append(" AND ");
            }
            whereClause.append(key).append(" = ?");
            index++;
        }

        query.append(whereClause);

        try (PreparedStatement statement = connection.prepareStatement(query.toString())) {
            int parameterIndex = 1;
            for (String value : where.values()) {
                statement.setString(parameterIndex++, value);
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString(itemColumn);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log the exception for debugging
        }

        return null;
    }

    public String getTable() {
        return table;
    }

    public Map<String, String> getWhere() {
        return where;
    }

    private Connection getConnection() {
        return connection;
    }
}
