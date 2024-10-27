package net.weesli.rozsLib.database.mysql;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
@Deprecated
public class MySQLBuilder {

    private String host;
    private int port;
    private String database;
    private String username;
    private String password;
    private Connection connection;
    public MySQLBuilder(String host, int port, String database, String username, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    public Connection build() {
        if (connection == null) {
            Connection connection;
            try {
                connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port, username, password);
                createDatabase(database, connection);
                return DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return connection;
    }

    public void createDatabase(String dbName, Connection connection) {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.execute("CREATE DATABASE IF NOT EXISTS " + dbName);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void createTable(String tableName, Connection connection, List<Column> columns) throws SQLException {
        if (columns == null || columns.isEmpty()) {
            throw new IllegalArgumentException("Columns cannot be null or empty");
        }

        StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS ").append(tableName).append(" (");

        for (int i = 0; i < columns.size(); i++) {
            Column column = columns.get(i);
            String columnName = column.getName();
            boolean primaryKey = column.isPrimary();
            boolean auto_increment = column.isAutoIncrement();
            String columnType = column.getType();
            int columnLength = column.getLength();

            if (columnName == null || columnType == null) {
                throw new IllegalArgumentException("Column name and type cannot be null");
            }

            sb.append(columnName).append(" ").append(columnType);

            if (columnType.equalsIgnoreCase("VARCHAR")) {
                sb.append("(").append(columnLength).append(")");
            }

            if (primaryKey) {
                sb.append(" PRIMARY KEY");
            }

            if (auto_increment) {
                sb.append(" AUTO_INCREMENT");
            }

            if (i < columns.size() - 1) {
                sb.append(", ");
            }
        }

        sb.append(");");

        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.execute(sb.toString());
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    public void insert(Connection connection, Insert value) throws SQLException {
        if (value == null) {
            throw new IllegalArgumentException("Values cannot be null or empty");
        }
        value.execute(connection);
    }

    public void update(Connection connection, Update value) throws SQLException {
        if (value == null) {
            throw new IllegalArgumentException("Values cannot be null or empty");
        }
        value.execute(connection);
    }

    public void delete(Delete value) throws SQLException {
        if (value == null) {
            throw new IllegalArgumentException("Values cannot be null or empty");
        }
        value.execute();
    }

    public Result getResult(String sql) throws SQLException {
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            return new Result(resultSet);
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
        }
    }

    public ItemStack getItemStack(SQLItemStack value) throws SQLException {
        if (value == null) {
            throw new IllegalArgumentException("Values cannot be null or empty");
        }
        return value.deserialize();
    }

    public List<ItemStack> getItemStackList(SQLItemStackList value) throws SQLException {
        if (value == null) {
            throw new IllegalArgumentException("Values cannot be null or empty");
        }
        return value.deserialize();
    }

    public String serialize(ItemStack itemStack) throws IOException {
        ByteArrayOutputStream io = new ByteArrayOutputStream();
        BukkitObjectOutputStream os = new BukkitObjectOutputStream(io);
        os.writeObject(itemStack);
        os.flush();
        byte[] serialized = io.toByteArray();
        return Base64.getEncoder().encodeToString(serialized);
    }

    public String serialize(List<ItemStack> itemStacks) {
        List<String> itemList = new ArrayList<>();
        for (ItemStack itemStack : itemStacks) {
            try {
                ByteArrayOutputStream io = new ByteArrayOutputStream();
                BukkitObjectOutputStream os = new BukkitObjectOutputStream(io);
                os.writeObject(itemStack);
                os.flush();
                byte[] serialized = io.toByteArray();
                itemList.add(Base64.getEncoder().encodeToString(serialized));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return itemList.toString();
    }
}