package net.weesli.rozsLib.database.mysql;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Map;
import java.util.stream.Collectors;
@Deprecated
public class SQLItemStack {

    private String tableName;
    private Map<String, String> where;
    private String itemColumn;
    private Connection connection;

    public SQLItemStack(Connection connection, String tableName, Map<String, String> where, String itemColumn) {
        this.tableName = tableName;
        this.where = where;
        this.connection = connection;
        this.itemColumn = itemColumn;
    }

    public ItemStack deserialize() {
        String sql = getSqlQuery();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            int index = 1;
            for (String value : where.values()) {
                statement.setString(index++, value);
            }

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                byte[] bytes = Base64.getDecoder().decode(resultSet.getString(itemColumn));
                ByteArrayInputStream in = new ByteArrayInputStream(bytes);
                BukkitObjectInputStream is = new BukkitObjectInputStream(in);
                return (ItemStack) is.readObject();
            }
        } catch (SQLException | IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private String getSqlQuery() {
        String whereClause = where.entrySet().stream()
                .map(entry -> entry.getKey() + " = ?")
                .collect(Collectors.joining(" AND "));

        return "SELECT * FROM " + tableName + " WHERE " + whereClause;
    }

    public Connection getConnection() {
        return connection;
    }

    public String getTableName() {
        return tableName;
    }

    public Map<String, String> getWhere() {
        return where;
    }
}
